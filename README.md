> Still work-in-progress, expect the unexpected

# Technologies
* PostgresQL (Exposed)
* KTOR (REST)

## ToDo
* [ ] Move KeyPairs/API Keys to KeyStore
* [o] Application Calls id -> name (name is unique) 
* [o] Application Call helpers for query/parameters
* [ ] Tree structure for routes
* [ ] Ratelimit
* [ ] Real authentication (bearer) for real men
* [ ] Message (Webhook) on Error
* [o] Error Handling
* [o] Better Service architecture
* [o] Licenses Long -> UUID (maybe something custom :thonk:)
* [o] Refactor Signature into Header
* [o] Refactor ApplicationID -> ApplicationName
* [o] Implement Version tree (/v1/ | /v2/ | ...)
* [o] Join authenticated and unauthenticated routes
* [o] Add Random Value to requests for signage
* [o] Delete unsuccessfull Server Error -> Not Found
* [ ] Implement authenticated version calls
* [ ] Implement authenticated resource calls

# Functions
* [o] Single License 
  * [o] Private/Public Keypair signing for authoritative answers
* [ ] Multi License
  * [ ] License Types
  * [ ] Time based
  * [ ] Limited Uses
  * [ ] Ip/MacID/HWID restriction/limits
  * [ ] Private/Public Keypair signing for authoritative answers
* [o] Feature Flag
* [o] Version Checks
* [o] Resource Download
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
  - returns resource object
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

## Keypair Authentication

- Authenticated requests are sent with a Timestamp and signed by a Private Key 
- The server checks integrity/authenticity of the request using a Public Key
- All Allowed Public Keys are stored in a File on the server (auth.toml) (name = key)
- The Server also discards all requests with a timestamp older than 30 seconds (timestamp also signed)
- the sign consists of the Requests URI + The Unix Timestamp of sending the request








