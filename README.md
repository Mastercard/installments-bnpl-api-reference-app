# Installment BNPL API Reference App
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
This is a reference application to demonstrate how Merchants Participation API can be used.
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
Check [Generating and Configuring a Mastercard API Client](https://developer.mastercard.com/platform/documentation/security-and-authentication/generating-and-configuring-a-mastercard-api-client/) to know more about how to generate a simple API client for consuming APIs.


## Configuration <a name="configuration"></a>
1. Create your account on [Mastercard Developers](https://developer.mastercard.com/) if you don't have it already.
2. Create a new project here and add ***Mastercard Installments API*** to it and click continue.
3. Download Sandbox Signing Key, a ```.p12``` file will be downloaded.
4. Copy the downloaded ```.p12``` file to ```src/main/resources``` folder in your code.
5. Open ```src/main/resources/application.yml``` and configure:
    - ```mastercard.api.environment.key-file ```- Path to keystore (.p12) file, just change the name as per the downloaded file in step 5. 
    - ```mastercard.api.authentication.consumer-key``` - Copy the Consumer key from "Sandbox/Production Keys" section on your project page
    - ```mastercard.api.authentication.keystore-alias``` - Alias of your key. Default key alias for sandbox is ```keyalias```.
    - ```mastercard.api.authentication.keystore-password``` -  Password of your Keystore. Default keystore password for sandbox project is ```keystorepassword```.
    

## Use-Cases <a name="use-cases"></a>
1. **Merchant Participation**   
Provides information on partner merchants with product code and IPP relationship.


More details can be found [here](https://stage.developer.mastercard.com/drafts/installments-for-bnpl/staging/documentation/use-cases/).    


## Execute the Use-Cases   <a name="execute-the-use-cases"></a>
1. Run ```mvn clean install``` from the root of the project directory.
2. There are two ways to execute the use-cases:
    1. Execute the use-cases(test cases):  
        - Go to ```src/test/java/com/mastercard/installments/bnpl/reference/application/controller/``` folder.  
        - Execute the test case.
    
    2. Use REST API based Client( such as [Insomnia](https://insomnia.rest/download/core/) or [Postman](https://www.postman.com/downloads/))  
        - Run ```mvn spring-boot:run``` command to run the application.  
        - Use any REST API based Client to test the functionality. Below is the API exposed by this application:       
                - GET <Host>/demo/merchants-participations  
            
                                                                               
## Service Documentation <a name="documentation"></a>

Mastercard Installments documentation can be found [here](https://stage.developer.mastercard.com/drafts/installments-for-bnpl/staging/documentation/).  


## API Reference <a name="api-reference"></a>
The Swagger API specification can be found [here](https://stage.developer.mastercard.com/drafts/installments-for-bnpl/staging/documentation/api-reference/).  

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
