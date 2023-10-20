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
import com.mastercard.installments.bnpl.api.reference.application.configuration.ApiConfiguration;
import com.mastercard.installments.bnpl.api.reference.application.exception.ServiceException;
import com.mastercard.installments.bnpl.api.reference.application.util.CryptoInterceptor;
import com.mastercard.developer.interceptors.OkHttpOAuth1Interceptor;
import org.openapitools.client.ApiResponse;
import org.openapitools.client.api.MerchantsParticipationApi;
import org.openapitools.client.model.MerchantParticipationsInner;
import org.openapitools.client.model.MerchantsInner;
import org.openapitools.client.model.PostMerchantMidSearches200Response;
import org.openapitools.client.model.PostMerchantMidSearchesRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MerchantParticipationService {

    private final MerchantsParticipationApi merchantsParticipationGetApi;
    private final MerchantsParticipationApi merchantsParticipationPostMidSearchesApi;
    private final MerchantsParticipationApi merchantsParticipationPostApi;


    @Autowired
    public MerchantParticipationService(ApiConfiguration apiConfiguration) {
        log.info("Initializing Merchants Participation Service");
        this.merchantsParticipationPostApi = new MerchantsParticipationApi(setupPostApi(apiConfiguration));
        this.merchantsParticipationGetApi = new MerchantsParticipationApi(setupGetMP(apiConfiguration));
        this.merchantsParticipationPostMidSearchesApi = new MerchantsParticipationApi(setupPostMidSearches(apiConfiguration));
    }

    private ApiClient setupPostApi(ApiConfiguration apiConfiguration) {
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(
                        new CryptoInterceptor(apiConfiguration.getJweConfigForPostMerchantParticipations(), apiConfiguration.getPiiClassifiedAlpha3CountryCodes())
                ).addInterceptor(
                        new OkHttpOAuth1Interceptor(apiConfiguration.getConsumerKey(), apiConfiguration.getSigningKey()))
                .build();
        return new ApiClient().setHttpClient(client).setBasePath(apiConfiguration.getBasePath());
    }

    private ApiClient setupPostMidSearches(ApiConfiguration apiConfiguration) {
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(
                        new CryptoInterceptor(apiConfiguration.getJweConfigPostMidSearches(), apiConfiguration.getPiiClassifiedAlpha3CountryCodes()))
                .addInterceptor(
                        new OkHttpOAuth1Interceptor(apiConfiguration.getConsumerKey(), apiConfiguration.getSigningKey()))
                .build();

        return new ApiClient().setHttpClient(client).setBasePath(apiConfiguration.getBasePath());
    }

    private ApiClient setupGetMP(ApiConfiguration apiConfiguration) {
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(
                        new CryptoInterceptor(apiConfiguration.getJweConfigGetMerchantParticipations())).addInterceptor(
                        new OkHttpOAuth1Interceptor(apiConfiguration.getConsumerKey(), apiConfiguration.getSigningKey()))
                .build();

        return new ApiClient().setHttpClient(client).setBasePath(apiConfiguration.getBasePath());
    }

    public ResponseEntity<Void> postMerchantParticipations(List<MerchantParticipationsInner> merchantParticipations) throws ServiceException {
        log.info("Calling Post Merchants Participation API");
        try {
            ApiResponse<Void> voidApiResponse = merchantsParticipationPostApi.postMerchantParticipationsWithHttpInfo(merchantParticipations);
            Map<String, List<String>> headers = voidApiResponse.getHeaders();
            HttpHeaders responseHeaders = new HttpHeaders();
            headers.keySet().forEach(key -> responseHeaders.add(key, headers.get(key).stream().collect(Collectors.joining(","))));
            return ResponseEntity.status(voidApiResponse.getStatusCode()).headers(responseHeaders).build();
        } catch (ApiException e) {
            log.info("Exception occurred while posting merchant participation {}", e.getResponseBody());
            throw new ServiceException(e.getMessage(), deserializeErrors(e.getResponseBody()));
        }
    }

    public List<MerchantsInner> getMerchantParticipations(Long requestId, Integer offset, Integer limit) throws ServiceException {
        log.info("Calling Get Merchants Participation API");
        try {
            List<MerchantsInner> merchants = merchantsParticipationGetApi.getMerchantParticipations(requestId, offset, limit);
            log.info("Merchants APiResponse : {} ", merchants);
            return merchants;
        } catch (ApiException e) {
            log.info("Exception occurred while getting merchant participation {}", e.getResponseBody());
            throw new ServiceException(e.getMessage(), deserializeErrors(e.getResponseBody()));
        }

    }

    public PostMerchantMidSearches200Response postMerchantMidSearches(PostMerchantMidSearchesRequest postMerchantMidSearchesRequest, Integer offset, Integer limit) throws Exception {
        log.info("Calling Post Merchant mid searches");
        return merchantsParticipationPostMidSearchesApi.postMerchantMidSearches(postMerchantMidSearchesRequest, offset, limit);
    }

}