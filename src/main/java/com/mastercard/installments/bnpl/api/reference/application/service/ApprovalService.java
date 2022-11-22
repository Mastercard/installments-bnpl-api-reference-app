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


import com.mastercard.developer.interceptors.OkHttpOAuth1Interceptor;
import com.mastercard.installments.bnpl.api.reference.application.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.ApiResponse;
import org.openapitools.client.api.ApprovalsApi;
import org.openapitools.client.model.CompletionPSPData;
import org.openapitools.client.model.PSPData;
import org.openapitools.client.model.PlanApprovalParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mastercard.installments.bnpl.api.reference.application.configuration.ApiConfiguration;
import com.mastercard.installments.bnpl.api.reference.application.util.CryptoInterceptor;


import java.util.UUID;

import static com.mastercard.installments.bnpl.api.reference.application.util.JSON.deserializeErrors;

@Service
@Slf4j
public class ApprovalService {

    private ApprovalsApi approvalsApi;

    @Autowired
    public ApprovalService(ApiConfiguration apiConfiguration){
        log.info("Initializing Approvals API");
        approvalsApi = new ApprovalsApi(setup(apiConfiguration));
    }
    private ApiClient setup(ApiConfiguration apiConfiguration) {
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(
                new CryptoInterceptor(apiConfiguration.getJweConfig())).addInterceptor(
                new OkHttpOAuth1Interceptor(apiConfiguration.getConsumerKey(), apiConfiguration.getSigningKey()))
                .build();

        return new ApiClient().setHttpClient(client).setBasePath(apiConfiguration.getBasePath());
    }

    public ApiResponse<CompletionPSPData> getPlanApprovalStatus(UUID planId) throws ServiceException {
        log.info("Calling Approvals API getApproval");
        try {
            ApiResponse<CompletionPSPData> completionPSPData = approvalsApi.getPlanApprovalStatusWithHttpInfo(planId);
            log.info("Approvals API call successful, returning response");
            return completionPSPData;
        } catch (ApiException e) {
            log.info("Exception occurred while getting approval status {}", e.getResponseBody());
            throw new ServiceException(e.getMessage(), deserializeErrors(e.getResponseBody()));
        }

    }

    public ApiResponse<PSPData> approvePlan(PlanApprovalParameters planApprovalParameters, Boolean sync) throws ServiceException {
        log.info("Calling Approvals API PostApproval");
        try {
            ApiResponse<PSPData> planApproval = approvalsApi.planApprovalWithHttpInfo(planApprovalParameters, sync);
            log.info("Approvals API call successful, returning response");
            return planApproval;
        } catch (ApiException e) {
            log.info("Exception occurred while getting approval status {}", e.getResponseBody());
            throw new ServiceException(e.getMessage(), deserializeErrors(e.getResponseBody()));
        }

    }
}
