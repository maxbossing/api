package ng.bossi.api.signing

import com.sksamuel.aedile.core.cacheBuilder
import kotlinx.serialization.Serializable
import kotlinx.serialization.StringFormat
import kotlinx.serialization.encodeToString
import ng.bossi.api.database.DatabaseController
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Duration.Companion.hours

object ResponseSigning {

  val keyCache = cacheBuilder<Long, PrivateKey> {
    maximumSize = 10000
    expireAfterWrite = 6.hours
  }.build {
    loadPrivateKey(it)
  }

  @OptIn(ExperimentalEncodingApi::class)
  private suspend fun loadPrivateKey(application: Long): PrivateKey {
    val pkcs8EncodedBytes: ByteArray = DatabaseController.applicationService.read(application)!!.key
    val keySpec = PKCS8EncodedKeySpec(pkcs8EncodedBytes)
    val kf = KeyFactory.getInstance("RSA")
    return kf.generatePrivate(keySpec)
  }

  @Serializable
  abstract class SignableResponse {
    abstract var sign: String
  }

  @OptIn(ExperimentalEncodingApi::class)
  suspend inline fun <reified I : @Serializable Any, O : @Serializable SignableResponse, E : StringFormat> signAsResponse(
    application: Long,
    input: I,
    output: O,
    stringFormat: E
  ): O? {
    val signature: Signature = Signature.getInstance("SHA256withRSA")
    signature.initSign(keyCache.get(application))
    signature.update(stringFormat.encodeToString(input).encodeToByteArray())
    return try {
      output.apply { sign = Base64.encode(signature.sign()) }
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }
}
