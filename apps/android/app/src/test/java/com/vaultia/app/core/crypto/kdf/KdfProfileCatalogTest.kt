package com.vaultia.app.core.crypto.kdf

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KdfProfileCatalogTest {
    @Test
    fun containsFastProfile() {
        val profile = KdfProfileCatalog.FAST

        assertEquals("argon2id-v1-profile-a", profile.id)
        assertEquals(KdfAlgorithm.ARGON2ID, profile.algorithm)
        assertEquals(KdfVersion.ARGON2_VERSION_13, profile.kdfVersion)
        assertEquals(32 * 1024, profile.memoryCostKiB)
        assertEquals(2, profile.iterations)
        assertEquals(1, profile.parallelism)
        assertEquals(32, profile.outputLengthBytes)
        assertEquals(32, profile.recommendedSaltLengthBytes)
    }

    @Test
    fun containsConservativeProfile() {
        val profile = KdfProfileCatalog.CONSERVATIVE

        assertEquals("argon2id-v1-profile-b", profile.id)
        assertEquals(KdfAlgorithm.ARGON2ID, profile.algorithm)
        assertEquals(KdfVersion.ARGON2_VERSION_13, profile.kdfVersion)
        assertEquals(64 * 1024, profile.memoryCostKiB)
        assertEquals(2, profile.iterations)
        assertEquals(1, profile.parallelism)
        assertEquals(32, profile.outputLengthBytes)
        assertEquals(32, profile.recommendedSaltLengthBytes)
    }

    @Test
    fun catalogContainsUniqueProfiles() {
        val profiles = KdfProfileCatalog.all()
        val ids = profiles.map { profile -> profile.id }

        assertEquals(ids.size, ids.toSet().size)
        assertTrue(profiles.contains(KdfProfileCatalog.FAST))
        assertTrue(profiles.contains(KdfProfileCatalog.CONSERVATIVE))
    }

    @Test
    fun defaultProfileIsConservative() {
        assertEquals(KdfProfileCatalog.CONSERVATIVE, KdfProfileCatalog.defaultProfile())
    }
}
