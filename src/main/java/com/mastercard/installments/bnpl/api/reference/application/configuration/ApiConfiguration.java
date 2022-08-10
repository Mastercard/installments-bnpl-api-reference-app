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
import org.openapitools.client.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.security.PrivateKey;

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

    private PrivateKey getSigningKey() {
        try {
            return AuthenticationUtils.loadSigningKey(
                    p12File.getFile().getAbsolutePath(),
                    keyAlias,
                    keyPassword);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

}
