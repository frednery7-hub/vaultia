package com.vaultia.app.core.crypto.kdf

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class KdfArchitectureTest {
    @Test
    fun kdfMainSourcesDoNotImportAndroidUiOrStorageFrameworks() {
        val kdfSourceFiles = kdfMainSourceFiles()

        val forbiddenPatterns = listOf(
            "import android.",
            "import androidx.",
            "import com.vaultia.app.feature.",
            "import com.vaultia.app.core.storage",
            "import androidx.room",
            "import androidx.datastore",
            "SQLite",
            "Room",
            "DataStore",
        )

        for (file in kdfSourceFiles) {
            val text = file.readText()
            forbiddenPatterns.forEach { forbiddenPattern ->
                assertFalse(
                    "Forbidden dependency $forbiddenPattern found in ${file.path}",
                    text.contains(forbiddenPattern),
                )
            }
        }
    }

    @Test
    fun onlyArgon2idImplementationImportsArgon2kt() {
        val filesImportingArgon2kt = kdfMainSourceFiles()
            .filter { file ->
                val text = file.readText()
                text.contains("com.lambdapioneer.argon2kt") ||
                    text.contains("Argon2Kt") ||
                    text.contains("Argon2Mode")
            }
            .map { file -> file.name }
            .sorted()

        assertEquals(listOf("Argon2idKdfDeriver.kt"), filesImportingArgon2kt)
    }

    private fun kdfMainSourceFiles(): List<File> = File("src/main/java/com/vaultia/app/core/crypto/kdf")
        .walkTopDown()
        .filter { file -> file.isFile && file.extension == "kt" }
        .toList()
}
