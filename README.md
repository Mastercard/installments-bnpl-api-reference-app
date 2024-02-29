# Mastercard Installments Reference App
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Mastercard_installments-bnpl-api-reference-app&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Mastercard_installments-bnpl-api-reference-app)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Mastercard_installments-bnpl-api-reference-app&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Mastercard_installments-bnpl-api-reference-app)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=Mastercard_installments-bnpl-api-reference-app&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=Mastercard_installments-bnpl-api-reference-app)
[![](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/Mastercard/installments-bnpl-api-reference-app/blob/main/LICENSE)
## Table of Contents
- [Overview](#overview)
- [Requirements](#requirements)
- [Frameworks/Libraries](#frameworks)
- [Integrating with OpenAPI Generator](#OpenAPI_Generator)
- [Configuration](#configuration)
- [Use-Cases](#use-cases)
- [Execute the Use-Cases](#execute-the-use-cases)
- [Service Documentation](#documentation)
- [API Reference](#api-reference)
- [Support](#support)
- [License](#license)

## Overview  <a name="overview"></a>
This is a reference application to demonstrate how Mastercard Installment APIs can be used.
To call the API, consumer key and .p12 file are required from your project on Mastercard Developers.

## Requirements  <a name="requirements"></a>

- Java 11
- IntelliJ IDEA (or any other IDE)

## Frameworks/Libraries <a name="frameworks"></a>
- Spring Boot
- Apache Maven
- OpenAPI Generator

## Integrating with OpenAPI Generator <a name="OpenAPI_Generator"></a>

OpenAPI Generator generates API client libraries from OpenAPI Specs. It provides generators and library templates for supporting multiple languages and frameworks.  
Check [Generating and Configuring a Mastercard API Client](https://developer.mastercard.com/platform/documentation/security-and-authentication/generating-and-configuring-a-mastercard-api-client/) to know more about how to generate a simple API client for consuming APIs.</a>

## Encryption and Decryption</a>
The [Mastercard Encryption Library](https://github.com/Mastercard/client-encryption-java) provides interceptor class that you can use when configuring your API client. This class will take care of encrypting payload before sending the request and decrypting payload after receiving the response.

#### Loading Encryption Certificate <a name="loading-encryption-certificate"></a>

A `Certificate` object can be created from a file by calling `EncryptionUtils.loadEncryptionCertificate`:
```java
Certificate encryptionCertificate = EncryptionUtils.loadEncryptionCertificate("<insert certificate file path>");
```
Supported certificate formats: PEM

#### Loading Decryption Key <a name="loading-decryption-key"></a>

##### From a PKCS#12 Key Store

A `PrivateKey` object can be created from a PKCS#12 key store by calling `EncryptionUtils.loadDecryptionKey` the following way:
```java
PrivateKey decryptionKey = EncryptionUtils.loadDecryptionKey(
                                    "<insert PKCS#12 key file path>", 
                                    "<insert key alias>", 
                                    "<insert key password>");
```
## Configuring JWE Instance </a>
```java
JweConfig config = JweConfigBuilder.aJweEncryptionConfig()
                                       .withEncryptionCertificate(encryptionCertificate)
                                       .withDecryptionKey(decryptionKey)
                                       .withEncryptionPath("$", "$")
                                       .withDecryptionPath("$.consumer.encryptedData", "$.consumer")
                                       .withEncryptedValueFieldName("cipher")
                                       .build();
```
## Configuration <a name="configuration"></a>
1. Create Your Account On [Mastercard Developers](https://developer.mastercard.com/) If You Don't Have Account Click On Create New Account.
2. Create A New Project Here & Add ***Mastercard Installments API*** To It & Click Continue.
3. Download Sandbox Signing Key, A ```.zip``` File Will Get Downloaded Click On Continue.
4. Create Encryption Keys - Mastercard Installments API - Sandbox Enter Keyalias & Password Of Your Choice, Note Down This Details You Will Need Them To Configure In Further Steps
5. Confirm Your Keystore Credentials Are Saved : Click on Confirm & Continue ```key-encryption-mc.p12``` File Will Get Downloaded
6. Extract The ```.zip``` File Downloaded In Step 3 & Copy The ```.p12``` From Extracted Folder Into ```src/main/resource```
7. Copy The Downloaded ```keyalias-encryption-mc.p12``` File To ```src/main/resource``` Folder
8. Go to My Project Section Click On Your Newly Created Project Click On ```Actions``` Under ```Client Encryption``` Section Click on Download Encryption Key ```.pem``` File Will Be Downloaded.
9. Copy The Downloaded ```.pem``` File To ```src/main/resources```.
10. Open ```src/main/resources/application.yml``` & Configure:
       - ```mastercard.api.environment.base-path ``` - Refers To Sandbox Endpoint, It's A Static Field, Will Be Used As A Host To Make API Calls.
    
    **Below properties will be required for authentication of API calls.**
    
    - ```mastercard.api.authentication.key-file ``` - path to keystore (.p12) file, just change the name as per the downloaded file in step 3. 
    - ```mastercard.api.authentication.consumer-key``` - copy the Consumer key from "Sandbox/Production Keys" section on your project page.
    - ```mastercard.api.authentication.keystore-alias``` - alias of your key. Default key alias for a sandbox is ```keyalias```.
    - ```mastercard.api.authentication.keystore-password``` - password of your Keystore. Default keystore password for sandbox project is ```keystorepassword```.
    
    **Below properties will be required to encrypt and decrypt the request and response payloads**
    
    - ```mastercard.api.encryption.key-file``` - Client Encryption File just change the name downloaded in step 9 ```client-encryption.pem```(this step will be used for approvals api).
    - ```mastercard.api.decryption.key-file``` - Mastercard encryption key File name downloaded in step 7 ```keyalias-encryption-mc.p12```(this step will be used for plans api).
    - ```mastercard.api.decryption.keystore-alias``` - alias of your mastercard encryption key set up by you in step 4 . ```keyalias```.
    - ```mastercard.api.decryption.keystore-password``` - password of your mastercard encryption key set up by you in step 4.```keystorepassword```.

## Use-Cases <a name="use-cases"></a>
1. **Merchant Participation**   
Provides information on partner merchants with product code and IPP relationship.

**Note**: For PII countries(like GBR) where we need to pass the JWE encrypted payload to Merchant Participation APIs, this reference app has capability to do JWE encryption before sending the request. If we are directly calling API, one may need to explicitly encrypt and send the request(Refer MerchantParticipationInterceptor class).  

2. **Approvals API**   
The approvals should be completed within 24 hours of initiation.

3. **Plans API**   
Provides "get plan" capability to retrieve information about the consumer and merchant. 
This operation returns a BNPL installment plan based on selected plan id provided in the request. 
The API response contains an encrypted value for the Consumer object, 
and the issuer needs to decrypt the response using Mastercard client encryption key.

More details can be found [here](https://developer.mastercard.com/drafts/installments-for-bnpl/staging/documentation/use-cases/).    

## Execute the Use-Cases   <a name="execute-the-use-cases"></a>
1. Run ```mvn clean install``` from the root of the project directory.
2. There are two ways to execute the use-cases:
    1. Execute the use-cases (test cases):  
        - Go to ```src/test/java/com/mastercard/installments/bnpl/reference/application/controller/``` folder.  
        - Execute the test case.
    
    2. Use REST API based Client (such as [Insomnia](https://insomnia.rest/download/core/) or [Postman](https://www.postman.com/downloads/))  
        - Run ```mvn spring-boot:run``` command to run the application.  
        - Use any REST API based Client to test the functionality. Below is the API exposed by this application:       

                - GET <Host>/demo/merchants-participations
                - POST <Host>/demo/approvals?sync=true
                - POST <Host>/demo/approvals?sync=false
                - GET <Host>/demo/approvals/{plan_id}
                - GET <Host>/plans/{plan_id}

                                                      
## Service Documentation <a name="documentation"></a>

Mastercard Installments documentation can be found [here](https://developer.mastercard.com/drafts/installments-for-bnpl/staging/documentation/).  


## API Reference <a name="api-reference"></a>
The Swagger API specification can be found [here](https://developer.mastercard.com/drafts/installments-for-bnpl/staging/documentation/api-reference/).  

## Support <a name="support"></a>
Please send an email to **apisupport@mastercard.com** with any questions or feedback you may have.  


## License <a name="license"></a>
<p>Copyright 2022 Mastercard</p>
<p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
the License. You may obtain a copy of the License at:</p>
<pre><code>   http://www.apache.org/licenses/LICENSE-2.0
</code></pre>
<p>Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.</p>