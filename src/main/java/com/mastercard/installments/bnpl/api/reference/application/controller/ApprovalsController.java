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
import com.mastercard.installments.bnpl.api.reference.application.service.ApprovalService;
import io.swagger.annotations.ApiParam;
import org.openapitools.client.ApiResponse;
import org.openapitools.client.model.CompletionPSPData;
import org.openapitools.client.model.PSPData;
import org.openapitools.client.model.PlanApprovalParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/demo")
public class ApprovalsController {
    @Autowired
    private ApprovalService approvalService;

    @GetMapping("/approvals/{plan_id}")
    public ResponseEntity<CompletionPSPData> getPlanApprovalStatus(@ApiParam(value = "Plan ID.", required = true) @PathVariable("plan_id") UUID planId) throws ServiceException {
        ApiResponse<CompletionPSPData> planApprovalStatus = approvalService.getPlanApprovalStatus(planId);
        return new ResponseEntity<>(planApprovalStatus.getData(),
                CollectionUtils.toMultiValueMap(planApprovalStatus.getHeaders()), planApprovalStatus.getStatusCode());
    }

    @PostMapping("/approvals")
    public ResponseEntity<PSPData> planApproval(@ApiParam(value = "", required = true) @Valid @RequestBody PlanApprovalParameters planApprovalParameters, @ApiParam(value = "Indicates whether the request is synchronous(the default value is false) or asynchronous") @Valid @RequestParam(value = "sync", required = false) Boolean sync) throws ServiceException {
        ApiResponse<PSPData> planApproval = approvalService.approvePlan(planApprovalParameters, sync);
        return new ResponseEntity<>(planApproval.getData(),
                CollectionUtils.toMultiValueMap(planApproval.getHeaders()),
                planApproval.getStatusCode());
    }
}