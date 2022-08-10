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
package com.mastercard.installments.bnpl.api.reference.application.controller;


import com.mastercard.installments.bnpl.api.reference.application.exception.ServiceException;
import com.mastercard.installments.bnpl.api.reference.application.service.MerchantParticipationService;
import org.openapitools.client.model.MerchantParticipation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
class MerchantsParticipationController {

    @Autowired
    private MerchantParticipationService merchantParticipationService;


    @GetMapping("/merchants-participations")
    public MerchantParticipation getMerchantsParticipations(@RequestParam(value = "card_product_code") String cardProductCode, @RequestParam(value = "country_code") String countryCode, @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset, @RequestParam(value = "limit", required = false, defaultValue = "500") Integer limit) throws ServiceException {
        return merchantParticipationService.getMerchantsParticipation(cardProductCode, countryCode, offset, limit);
    }


}
