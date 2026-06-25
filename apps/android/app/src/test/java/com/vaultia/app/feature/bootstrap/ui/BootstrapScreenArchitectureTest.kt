package com.vaultia.app.feature.bootstrap.ui

import org.junit.Assert.assertFalse
import org.junit.Test
import java.nio.file.Paths

class BootstrapScreenArchitectureTest {

    @Test
    fun bootstrapScreen_doesNotContainSensitiveInputOrPersistenceTerms() {
        val path = Paths.get(
            "src/main/java/com/vaultia/app/feature/bootstrap/ui/BootstrapScreen.kt",
        )

        val source = path.toFile().readText().lowercase()

        val forbiddenTerms = listOf(
            "edittext",
            "password",
            "masterpassword",
            "secretkey",
            "cipher",
            "keystore",
            "sharedpreferences",
            "sqlite",
            "room",
            "encrypt",
            "decrypt",
            "internet",
            "http",
            "sync",
        )

        forbiddenTerms.forEach { forbidden ->
            assertFalse(
                "Forbidden bootstrap UI term detected: $forbidden",
                source.contains(forbidden),
            )
        }
    }
}
