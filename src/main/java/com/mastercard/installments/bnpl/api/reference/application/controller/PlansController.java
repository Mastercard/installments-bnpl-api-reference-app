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
import com.mastercard.installments.bnpl.api.reference.application.service.PlansService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.openapitools.client.model.InstallmentPlan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlansController {

    private final PlansService plansService;

    @GetMapping("{plan_id}")
    public InstallmentPlan getPlan(
        @PathVariable("plan_id") UUID planId
    ) throws ServiceException {
        return plansService.getPlan(planId);
    }
}
