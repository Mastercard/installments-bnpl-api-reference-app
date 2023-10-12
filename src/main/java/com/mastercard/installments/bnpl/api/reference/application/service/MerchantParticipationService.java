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

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.mastercard.developer.encryption.EncryptionException;
import com.mastercard.developer.encryption.JweEncryption;
import com.mastercard.developer.interceptors.OkHttpOAuth1Interceptor;
import com.mastercard.installments.bnpl.api.reference.application.configuration.ApiConfiguration;
import com.mastercard.installments.bnpl.api.reference.application.exception.ServiceException;
import com.mastercard.installments.bnpl.api.reference.application.util.CryptoInterceptor;
import com.mastercard.installments.bnpl.api.reference.application.util.EncryptMerchant;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.ApiResponse;
import org.openapitools.client.api.MerchantsParticipationApi;
import org.openapitools.client.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mastercard.installments.bnpl.api.reference.application.util.JSON.deserializeErrors;

@Service
@Slf4j
public class MerchantParticipationService {

    private final MerchantsParticipationApi merchantsParticipationGetApi;
    private final MerchantsParticipationApi merchantsParticipationPostMidSearchesApi;

    private final MerchantsParticipationApi merchantsParticipationPostApi;

    @Value("#{'${pii-classified-country}'.split('\\|')}")
    private List<String> piiClassifiedAlpha3CountryCode;

    private ApiConfiguration apiConfiguration;

    @Autowired
    public MerchantParticipationService(ApiConfiguration apiConfiguration) {
        log.info("Initializing Merchants Participation Service");
        this.merchantsParticipationGetApi = new MerchantsParticipationApi(setupGetMP(apiConfiguration));
        this.merchantsParticipationPostMidSearchesApi = new MerchantsParticipationApi(setupPostMidSearches(apiConfiguration));
        this.merchantsParticipationPostApi = new MerchantsParticipationApi(setupPostApi(apiConfiguration));
        this.apiConfiguration = apiConfiguration;
    }

    private ApiClient setupPostApi(ApiConfiguration apiConfiguration) {
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(
                        new OkHttpOAuth1Interceptor(apiConfiguration.getConsumerKey(), apiConfiguration.getSigningKey()))
                .build();
        return new ApiClient().setHttpClient(client).setBasePath(apiConfiguration.getBasePath());
    }

    private ApiClient setupPostMidSearches(ApiConfiguration apiConfiguration) {
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(
                        new CryptoInterceptor(apiConfiguration.getJweConfigPostMidSearches())).addInterceptor(
                        new OkHttpOAuth1Interceptor(apiConfiguration.getConsumerKey(), apiConfiguration.getSigningKey()))
                .build();
        return new ApiClient().setHttpClient(client).setBasePath(apiConfiguration.getBasePath());
    }

    private ApiClient setupGetMP(ApiConfiguration apiConfiguration) {
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(
                        new CryptoInterceptor(apiConfiguration.getJweConfigGetMP())).addInterceptor(
                        new OkHttpOAuth1Interceptor(apiConfiguration.getConsumerKey(), apiConfiguration.getSigningKey()))
                .build();

        return new ApiClient().setHttpClient(client).setBasePath(apiConfiguration.getBasePath());
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
        if(!(postMerchantMidSearchesRequest.getActualInstance() instanceof MerchantMidSearchParameters)) {
            setActualInstanceIfNotInitialized(postMerchantMidSearchesRequest);
        }
        return merchantsParticipationPostMidSearchesApi.postMerchantMidSearches(postMerchantMidSearchesRequest, offset, limit);
    }

    private void setActualInstanceIfNotInitialized(PostMerchantMidSearchesRequest postMerchantMidSearchesRequest) {
        LinkedTreeMap postMerchantMidSearchesMap = (LinkedTreeMap) postMerchantMidSearchesRequest.getActualInstance();
        Object mid = postMerchantMidSearchesMap.get("mid");
        Object status = postMerchantMidSearchesMap.get("status");

        MerchantMidSearchParameters merchantMidSearchParameters = new MerchantMidSearchParameters();
        merchantMidSearchParameters.setMerchantLegalName(postMerchantMidSearchesMap.get("merchantLegalName") != null ? postMerchantMidSearchesMap.get("merchantLegalName").toString() : null);
        merchantMidSearchParameters.setCountryCode(postMerchantMidSearchesMap.get("countryCode").toString());
        merchantMidSearchParameters.setAcquirerICA(postMerchantMidSearchesMap.get("acquirerICA").toString());
        merchantMidSearchParameters.setMid(mid != null ? mid.toString() : null);
        merchantMidSearchParameters.setStatus(status != null ? status.toString() : null);
        postMerchantMidSearchesRequest.setActualInstance(merchantMidSearchParameters);
    }

    public ResponseEntity<Void> postMerchantParticipations(List<MerchantParticipationInner> merchantParticipations) throws ServiceException {
        log.info("Calling Post Merchants Participation API");
        prepareMerchantParticipationPostRequest(merchantParticipations);
        try {
            ApiResponse<Void> voidApiResponse = merchantsParticipationPostApi.postMerchantParticipationWithHttpInfo(merchantParticipations);
            Map<String, List<String>> headers = voidApiResponse.getHeaders();
            HttpHeaders responseHeaders = new HttpHeaders();
            headers.keySet().forEach(key -> responseHeaders.add(key, headers.get(key).stream().collect(Collectors.joining(","))));
            return ResponseEntity.status(voidApiResponse.getStatusCode()).headers(responseHeaders).build();
        } catch (ApiException e) {
            log.info("Exception occurred while posting merchant participation {}", e.getResponseBody());
            throw new ServiceException(e.getMessage(), deserializeErrors(e.getResponseBody()));
        }
    }

    private void prepareMerchantParticipationPostRequest(List<MerchantParticipationInner> merchantParticipations) {
        merchantParticipations.forEach(mer -> {
            try {
                encryptPostBodyIfNeeded(mer);
            } catch (EncryptionException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void encryptPostBodyIfNeeded(MerchantParticipationInner merchant) throws EncryptionException {
        if(piiClassifiedAlpha3CountryCode.contains(merchant.getCountryCode())){
            EncryptMerchant encryptMerchant = new EncryptMerchant(merchant.getMerchantLegalName(), merchant.getDbaNames(), merchant.getAddress());
            String encryptPayload = JweEncryption.encryptPayload(new Gson().toJson(encryptMerchant), apiConfiguration.getJweConfigForPostMP());
            EncryptMerchant encryptMerchant1 = new Gson().fromJson(encryptPayload, EncryptMerchant.class);
            merchant.setMerchantLegalName(null);
            merchant.setDbaNames(null);
            merchant.setAddress(null);
            merchant.setEncryptedValues(encryptMerchant1.getEncryptedMerchantLegalName());
        }
    }
}
