> Still work-in-progress, expect the unexpected

# Technologies
* PostgresQL (Exposed)
* KTOR (REST)

## Signture Verification
```kotlin
      val response = version.sign(id)!!
      val sigBytes = response.sign.decodeBase64Bytes()

      val signature = Signature.getInstance("SHA256withRSA")

      signature.initVerify(publicKey)

      signature.update(json.encodeToString(version).encodeToByteArray())

      println(signature.verify(sigBytes))
```