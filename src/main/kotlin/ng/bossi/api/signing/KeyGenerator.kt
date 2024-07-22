package ng.bossi.api.signing

import java.security.KeyPair
import java.security.KeyPairGenerator

object KeyGenerator {

  private val generator = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }

  fun generateKey(): KeyPair = generator.genKeyPair()
}