# Technologies
* PostgresQL (Exposed)
* http4k (REST)

## ToDo
* [ ] Switch to Embedded Server from EngineMain


# Functions
* [ ] Single License 
  * [ ] Private/Public Keypair signing for authoritative answers
* [ ] Multi License
  * [ ] License Types
  * [ ] Time based
  * [ ] Limited Uses
  * [ ] Ip/MacID/HWID restriction/limits
  * [ ] Private/Public Keypair signing for authoritative answers
* [ ] Feature Flag
* [ ] Version Checks
* [ ] Resource Download
* [ ] Auto Updates (based on resources)
* [ ] Discord Manager Bot


# API Schema

## Client API
```txt
/application/{appId}
  - /versions
    - returns newest version id, signed
    - /{versionId}
      - returns version object, signed
  - /featureflags
    - /{flagId}
      - returns featureflag object, signed
  - /license
    - /single
      - /{licenseId}
        - returns license object, signed
/resources/resourceId
  - returns resource object, signed
  - /download
    - rewrites to resource objects download url 
```

# Model
## Application
- name
- id
- key [RSA]

## FeatureFlags
- name
- application [reference (application)]
- status (true/false)
- key [join (application/key)]

## Version
- version
- codename [nullable]
- application [reference (application)]
- status (current/supported/unsupported)
- key [join (application/key)]

## Resources
- name
- hash [md_5]
- size
- S3 URL 

## Single License
- id
- application [join (application/id)]
- status (enabled/disabled/paused)
- key [join (application/key)]



## Signture Verification
```kotlin
      val response = version.sign(id)!!
      val sigBytes = response.sign.decodeBase64Bytes()

      val signature = Signature.getInstance("SHA256withRSA")

      signature.initVerify(publicKey)

      signature.update(json.encodeToString(version).encodeToByteArray())

      println(signature.verify(sigBytes))

```