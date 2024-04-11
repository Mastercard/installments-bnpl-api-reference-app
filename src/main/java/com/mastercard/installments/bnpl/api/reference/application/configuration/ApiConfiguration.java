/*
 *  Copyright (c) 2022 Mastercard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mastercard.installments.bnpl.api.reference.application.configuration;

import com.mastercard.developer.interceptors.OkHttpOAuth1Interceptor;
import com.mastercard.developer.utils.AuthenticationUtils;
import com.mastercard.developer.encryption.EncryptionException;
import com.mastercard.developer.encryption.JweConfig;
import com.mastercard.developer.encryption.JweConfigBuilder;
import com.mastercard.developer.utils.EncryptionUtils;
import org.openapitools.client.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ApiConfiguration {

    @Value("${mastercard.api.authentication.consumer-key}")
    private String consumerKey;

    @Value("${mastercard.api.authentication.keystore-alias}")
    private String keyAlias;

    @Value("${mastercard.api.authentication.keystore-password}")
    private String keyPassword;

    @Value("${mastercard.api.authentication.key-file}")
    private Resource p12File;

    @Value("${mastercard.api.environment.base-path}")
    private String basePath;

    @Value("${mastercard.api.encryption.key-file}")
    private Resource encryptionKeyFile;

    @Value("${mastercard.api.decryption.key-file}")
    private Resource decryptionKeyFile;

    @Value("${mastercard.api.decryption.keystore-alias}")
    private String decryptionKeyAlias;

    @Value("${mastercard.api.decryption.keystore-password}")
    private String decryptionKeyPassword;

    @Value("#{'${pii-classified-country}'.split('\\|')}")
    private List<String> piiClassifiedAlpha3CountryCode;

    public List<String> getPiiClassifiedAlpha3CountryCode(){
        return piiClassifiedAlpha3CountryCode;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getBasePath() {
        return basePath;
    }


    @Bean
    public ApiClient setupApiClient() {
        var apiClient = new ApiClient();

        apiClient.setBasePath(basePath);
        apiClient.setHttpClient(
                apiClient.getHttpClient()
                        .newBuilder()
                        .addInterceptor(new OkHttpOAuth1Interceptor(consumerKey, getSigningKey()))
                        .build()
        );
        apiClient.setDebugging(false);

        return apiClient;
    }

    public PrivateKey getSigningKey() {
        try {
            return AuthenticationUtils.loadSigningKey(
                    p12File.getFile().getAbsolutePath(),
                    keyAlias,
                    keyPassword);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public JweConfig getJweConfig() {
        JweConfig config = null;
    	try {
            Certificate encryptionCertificate = EncryptionUtils.loadEncryptionCertificate(encryptionKeyFile.getFile().getAbsolutePath());
            PrivateKey decryptionKey = EncryptionUtils.loadDecryptionKey(decryptionKeyFile.getFile().getAbsolutePath(), decryptionKeyAlias, decryptionKeyPassword);
            config = JweConfigBuilder.aJweEncryptionConfig()
                    .withEncryptionCertificate(encryptionCertificate)
                    .withDecryptionKey(decryptionKey)
                    .withEncryptionPath("$", "$")
                    .withDecryptionPath("$.consumer.encryptedData", "$.consumer")
                    .withEncryptedValueFieldName("cipher")
                    .build();
            return config;
        } catch (GeneralSecurityException | IOException | EncryptionException e) {
            log.error("Exception occurred while configuration ",e);
        }
    	return config;
    }

    public JweConfig getJweConfigGetMP() {
        JweConfig config = null;
        try {
            PrivateKey decryptionKey = EncryptionUtils.loadDecryptionKey(decryptionKeyFile.getFile().getAbsolutePath(), decryptionKeyAlias, decryptionKeyPassword);
            config = JweConfigBuilder.aJweEncryptionConfig()
                    .withDecryptionKey(decryptionKey)
                    .withDecryptionPath("$[*]encryptedValues", "$[*]")
                    .withEncryptedValueFieldName("encryptedValues")
                    .build();
            return config;
        } catch (GeneralSecurityException | IOException | EncryptionException e) {
            log.error("Exception occurred while configuration ",e);
        }
        return config;
    }

    public JweConfig getJweConfigPostMidSearches() {
        JweConfig config = null;
        try {
            Certificate encryptionCertificate = EncryptionUtils.loadEncryptionCertificate(encryptionKeyFile.getFile().getAbsolutePath());
            PrivateKey decryptionKey = EncryptionUtils.loadDecryptionKey(decryptionKeyFile.getFile().getAbsolutePath(), decryptionKeyAlias, decryptionKeyPassword);
            config = JweConfigBuilder.aJweEncryptionConfig()
                    .withEncryptionCertificate(encryptionCertificate)
                    .withEncryptionPath("$.merchantLegalName", "$")
                    .withEncryptedValueFieldName("encryptedMerchantLegalName")
                    .withDecryptionKey(decryptionKey)
                    .withDecryptionPath("$.encryptedMerchantLegalName", "$.merchantLegalName")
                    .build();
            return config;
        } catch (GeneralSecurityException | IOException | EncryptionException e) {
            log.error("Exception occurred while configuration ",e);
        }
        return config;
    }

    public JweConfig getJweConfigForPostMP() {
        JweConfig config = null;
        try {
            Certificate encryptionCertificate = EncryptionUtils.loadEncryptionCertificate(encryptionKeyFile.getFile().getAbsolutePath());
            config = JweConfigBuilder.aJweEncryptionConfig()
                    .withEncryptionCertificate(encryptionCertificate)
                    .withEncryptionPath("$", "$")
                    .withEncryptedValueFieldName("encryptedValues")
                    .build();
            return config;
        } catch (GeneralSecurityException | IOException | EncryptionException e) {
            log.error("Exception occurred while configuration ",e);
        }
        return config;
    }
}
