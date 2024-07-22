package ng.bossi.api.signing

import com.sksamuel.aedile.core.cacheBuilder
import io.ktor.util.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import ng.bossi.api.database.DatabaseController
import ng.bossi.api.utils.json
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import kotlin.time.Duration.Companion.hours

object ResponseSigning {

  val keyCache = cacheBuilder<Long, PrivateKey> {
    maximumSize = 10000
    expireAfterWrite = 6.hours
  }.build {
    loadPrivateKey(it)
  }

  private suspend fun loadPrivateKey(application: Long): PrivateKey {
    val pkcs8EncodedBytes: ByteArray = DatabaseController.applicationService.readById(application)!!.key
    val keySpec = PKCS8EncodedKeySpec(pkcs8EncodedBytes)
    val kf = KeyFactory.getInstance("RSA")
    return kf.generatePrivate(keySpec)
  }


  suspend inline fun <reified I: @Serializable Any> signature(
    applicationId: Long,
    input: I,
    serializer: KSerializer<I>,
    knowOnce: String,
  ): String? {
    val signature: Signature = Signature.getInstance("SHA256withRSA")
    signature.initSign(keyCache.get(applicationId))
    signature.update(json.encodeToString(serializer, input).encodeToByteArray() + knowOnce.encodeToByteArray())
    return try {
      signature.sign().encodeBase64()
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }
}

suspend inline fun <reified T: @Serializable Any> T.signature(
  application: Long,
  serializer: KSerializer<T>,
  knowOnce: String
): String? = ResponseSigning.signature(application, this, serializer, knowOnce)
