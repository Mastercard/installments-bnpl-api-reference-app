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
package com.mastercard.installments.bnpl.api.reference.application.service;

import static com.mastercard.installments.bnpl.api.reference.application.util.JSON.deserializeErrors;

import com.mastercard.developer.interceptors.OkHttpOAuth1Interceptor;
import com.mastercard.installments.bnpl.api.reference.application.configuration.ApiConfiguration;
import com.mastercard.installments.bnpl.api.reference.application.exception.ServiceException;
import java.util.UUID;

import com.mastercard.installments.bnpl.api.reference.application.util.CryptoInterceptor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.PlansApi;
import org.openapitools.client.model.InstallmentPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PlansService {

    private final PlansApi plansApi;


    @Autowired
    public PlansService(ApiConfiguration apiConfiguration) throws ServiceException{
        log.info("Initializing Plans API");
        this.plansApi = new PlansApi(setup(apiConfiguration));
    }
    private ApiClient setup(ApiConfiguration apiConfiguration) throws ServiceException {
        log.info("inside setup");
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(
                new CryptoInterceptor(apiConfiguration.getJweConfig())).addInterceptor(
                new OkHttpOAuth1Interceptor(apiConfiguration.getConsumerKey(), apiConfiguration.getSigningKey()))
                .build();

        return new ApiClient().setHttpClient(client).setBasePath(apiConfiguration.getBasePath());
    }
    public InstallmentPlan getPlan(UUID planId) throws ServiceException {
        log.info("Calling Plans API getPlan");
        try {
            InstallmentPlan installmentPlan = plansApi.getPlan(planId);
            log.info("Plans API call successful, returning response");
            return installmentPlan;

        } catch (ApiException e) {
            log.info("Exception occurred while getting merchant participation {}", e.getResponseBody());
            throw new ServiceException(e.getMessage(), deserializeErrors(e.getResponseBody()));
        }
    }
}
