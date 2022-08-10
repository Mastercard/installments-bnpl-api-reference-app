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

import com.mastercard.installments.bnpl.api.reference.application.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.MerchantsParticipationApi;
import org.openapitools.client.model.MerchantParticipation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.mastercard.installments.bnpl.api.reference.application.util.JSON.deserializeErrors;

@Service
@Slf4j
public class MerchantParticipationService {

    private final MerchantsParticipationApi merchantsParticipationApi;

    @Autowired
    public MerchantParticipationService(ApiClient apiClient) {
        log.info("Initializing Merchants Participation API");
        this.merchantsParticipationApi = new MerchantsParticipationApi(apiClient);
    }


    public MerchantParticipation getMerchantsParticipation(String cardProductCode, String countryCode, Integer offset, Integer limit) throws ServiceException {

        log.info("Calling Merchants Participation API");

        try {
            MerchantParticipation merchantsParticipations = merchantsParticipationApi.getMerchantsParticipations(
                    cardProductCode, countryCode, offset, limit);

            log.info("Merchants Participation API call successful, returning response");

            return merchantsParticipations;

        } catch (ApiException e) {
            log.info("Exception occurred while getting merchant participation {}", e.getResponseBody());
            throw new ServiceException(e.getMessage(), deserializeErrors(e.getResponseBody()));
        }
    }
}
