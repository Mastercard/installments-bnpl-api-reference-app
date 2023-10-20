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
import org.openapitools.client.model.MerchantParticipationsInner;
import org.openapitools.client.model.MerchantsInner;
import org.openapitools.client.model.PostMerchantMidSearches200Response;
import org.openapitools.client.model.PostMerchantMidSearchesRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/demo")
class MerchantsParticipationController {

    private final MerchantParticipationService merchantParticipationService;

    MerchantsParticipationController(MerchantParticipationService merchantParticipationService) {
        this.merchantParticipationService = merchantParticipationService;
    }

    @PostMapping("/merchants/participations")
    public ResponseEntity<Void> postMerchantsParticipations(@Valid @RequestBody List<MerchantParticipationsInner> merchantParticipation) throws ServiceException {
        return merchantParticipationService.postMerchantParticipations(merchantParticipation);
    }

    @GetMapping("/merchants/participations")
    public List<MerchantsInner> getMerchantParticipations(@RequestParam(value = "request_id", required = false) Long requestId, @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset, @RequestParam(value = "limit", required = false, defaultValue = "500") Integer limit) throws ServiceException {
        return merchantParticipationService.getMerchantParticipations(requestId, offset, limit);
    }

    @PostMapping("/merchants/mids/searches")
    public PostMerchantMidSearches200Response postMerchantMidSearches(@Valid @RequestBody PostMerchantMidSearchesRequest postMerchantMidSearchesRequest, @Valid @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset, @Valid @RequestParam(value = "limit", required = false, defaultValue = "500") Integer limit ) throws Exception {
        return merchantParticipationService.postMerchantMidSearches(postMerchantMidSearchesRequest, offset, limit);
    }

}