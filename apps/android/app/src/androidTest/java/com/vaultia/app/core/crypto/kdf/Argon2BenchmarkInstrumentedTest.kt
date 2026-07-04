package com.vaultia.app.core.crypto.kdf

import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2Mode
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Phase 22 — KDF Dependency Evaluation.
 *
 * Instrumented benchmark of Argon2id (via lambdapioneer/argon2kt 1.6.0)
 * on a real Android device. This is evaluation code only — it is NOT
 * integrated with CryptoService, SessionManager, VaultRepository, UI,
 * storage, or any production flow. Its sole purpose is to produce timing
 * evidence for the Phase 22 DONE document.
 *
 * Inputs (password, salt) are fixed dummy values, deliberately chosen to
 * isolate the "parameters" variable. Real master password derivation is
 * out of scope for this phase.
 */
@RunWith(AndroidJUnit4::class)
class Argon2BenchmarkInstrumentedTest {

    private companion object {
        const val TAG = "Vaultia.Phase22.Bench"

        // Fixed dummy inputs for reproducibility of timing measurements.
        // These are NOT credentials and never reach storage or any real flow.
        val DUMMY_PASSWORD: ByteArray = "benchmark-only-not-a-real-password".toByteArray()
        val DUMMY_SALT: ByteArray = ByteArray(16) { i -> (i + 1).toByte() }

        const val HASH_LENGTH_BYTES = 32

        // Warm-up runs are not measured. They exist because the very first
        // call loads the native .so and performs allocations that are not
        // representative of steady-state behaviour.
        const val WARMUP_RUNS = 1
        const val MEASURED_RUNS = 3
    }

    private data class Candidate(
        val label: String,
        val memoryCostKiB: Int,
        val iterations: Int,
        val parallelism: Int,
    )

    private val candidates = listOf(
        Candidate(label = "FAST",         memoryCostKiB = 32 * 1024, iterations = 2, parallelism = 1),
        Candidate(label = "CONSERVATIVE", memoryCostKiB = 64 * 1024, iterations = 2, parallelism = 1),
    )

    @Test
    fun benchmarkArgon2idCandidates() {
        val argon2 = Argon2Kt()

        logHeader()
        logDeviceInfo()

        for (candidate in candidates) {
            runSingleCandidate(argon2, candidate)
        }

        logFooter()
    }

    private fun runSingleCandidate(argon2: Argon2Kt, candidate: Candidate) {
        emit("---")
        emit("Candidate: ${candidate.label}")
        emit("  memoryCostKiB = ${candidate.memoryCostKiB} (${candidate.memoryCostKiB / 1024} MiB)")
        emit("  iterations    = ${candidate.iterations}")
        emit("  parallelism   = ${candidate.parallelism}")
        emit("  hashLength    = $HASH_LENGTH_BYTES bytes")

        // Warm-up (not measured).
        repeat(WARMUP_RUNS) {
            argon2.hash(
                mode = Argon2Mode.ARGON2_ID,
                password = DUMMY_PASSWORD,
                salt = DUMMY_SALT,
                tCostInIterations = candidate.iterations,
                mCostInKibibyte = candidate.memoryCostKiB,
                parallelism = candidate.parallelism,
                hashLengthInBytes = HASH_LENGTH_BYTES,
            )
        }

        val timingsNanos = LongArray(MEASURED_RUNS)
        for (i in 0 until MEASURED_RUNS) {
            val startNs = SystemClock.elapsedRealtimeNanos()
            argon2.hash(
                mode = Argon2Mode.ARGON2_ID,
                password = DUMMY_PASSWORD,
                salt = DUMMY_SALT,
                tCostInIterations = candidate.iterations,
                mCostInKibibyte = candidate.memoryCostKiB,
                parallelism = candidate.parallelism,
                hashLengthInBytes = HASH_LENGTH_BYTES,
            )
            val endNs = SystemClock.elapsedRealtimeNanos()
            timingsNanos[i] = endNs - startNs
        }

        val timingsMs = timingsNanos.map { it / 1_000_000.0 }
        val minMs = timingsMs.min()
        val maxMs = timingsMs.max()
        val meanMs = timingsMs.average()

        emit("  runs (ms): ${timingsMs.joinToString(", ") { "%.1f".format(it) }}")
        emit("  min  (ms): %.1f".format(minMs))
        emit("  max  (ms): %.1f".format(maxMs))
        emit("  mean (ms): %.1f".format(meanMs))
    }

    private fun logDeviceInfo() {
        emit("Device:")
        emit("  manufacturer    = ${Build.MANUFACTURER}")
        emit("  model           = ${Build.MODEL}")
        emit("  device          = ${Build.DEVICE}")
        emit("  Android version = ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
        emit("  ABIs            = ${Build.SUPPORTED_ABIS.joinToString(", ")}")
    }

    private fun logHeader() {
        emit("================================================================")
        emit("Vaultia Phase 22 — Argon2id benchmark")
        emit("Library: com.lambdapioneer.argon2kt:argon2kt:1.6.0")
        emit("Mode: Argon2id")
        emit("Fixed inputs (NOT real credentials): dummy password + 16-byte salt")
        emit("Warmup runs (discarded): $WARMUP_RUNS per candidate")
        emit("Measured runs:           $MEASURED_RUNS per candidate")
        emit("================================================================")
    }

    private fun logFooter() {
        emit("================================================================")
        emit("End of Argon2id benchmark.")
        emit("================================================================")
    }

    /**
     * Emit a line to both logcat (for live monitoring) and stdout (so the
     * Gradle test report and any --info console capture also keep a copy).
     */
    private fun emit(message: String) {
        Log.i(TAG, message)
        println("[$TAG] $message")
    }
}
