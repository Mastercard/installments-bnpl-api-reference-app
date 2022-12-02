package com.mastercard.installments.bnpl.api.reference.application.controller;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.openapitools.client.JSON;
import org.openapitools.client.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApprovalControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    private JSON json = new JSON();

    @Test
    void planApprovalStatus() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/demo/approvals").param("sync", "true").contentType(
                MediaType.APPLICATION_JSON).content(
                gson.toJson(planApprovalParams()))).andDo(print()).andExpect(status().isOk()).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        PSPData pspData = json.deserialize(contentAsString, PSPData.class);
        Assertions.assertNotNull(pspData);
    }

    @Test
    @Order(1)
    void test202AcceptedForAsync() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/demo/approvals").param("sync", "false").contentType(
                MediaType.APPLICATION_JSON).content(
                gson.toJson(planApprovalParams()))).andDo(print()).andExpect(status().isAccepted()).andReturn();
        String locationHeader = mvcResult.getResponse().getHeader("Location");
        assertNotNull(locationHeader);
    }
    public static PlanApprovalParameters planApprovalParams() {
        PlanApprovalParameters planApprovalParameters= new PlanApprovalParameters();
        planApprovalParameters.setPlanId(UUID.fromString("16fa4f03-15cb-46da-bb96-1e3f672688fe"));
        planApprovalParameters.setStatus(PlanApprovalParameters.StatusEnum.valueOf("APPROVED"));
        planApprovalParameters.setApprovedAmount(100.00);
        planApprovalParameters.setApprovedCurrency("USD");
        PlanApprovalParametersPaymentAuthorization planApprovalParametersPaymentAuthorization = new PlanApprovalParametersPaymentAuthorization();
        planApprovalParametersPaymentAuthorization.setPrimaryAccountNumber("5120340101976862");
        planApprovalParametersPaymentAuthorization.setPanExpirationMonth("11");
        planApprovalParametersPaymentAuthorization.setPanExpirationYear("2023");
        planApprovalParametersPaymentAuthorization.setCardSecurityCode("000");
        planApprovalParametersPaymentAuthorization.setCardholderFullName("John Doe");
        planApprovalParametersPaymentAuthorization.setCardholderFirstName("John");
        planApprovalParametersPaymentAuthorization.setCardholderLastName("Doe");
        PlanApprovalParametersPaymentAuthorizationBillingAddress billingAddress = new PlanApprovalParametersPaymentAuthorizationBillingAddress();
        billingAddress.setCountryCode("US");
        billingAddress.setLine1("123MAINSTREET");
        billingAddress.setLine2("2a");
        billingAddress.setCity("New York");
        billingAddress.setState("NY");
        billingAddress.setZip("07306");
        planApprovalParametersPaymentAuthorization.setBillingAddress(billingAddress);
        planApprovalParameters.setPaymentAuthorization(planApprovalParametersPaymentAuthorization);
        return planApprovalParameters;
    }

    @Test
    @Order(2)
    void getApprovalStatus() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/demo/approvals/{plan_id}".replace("{plan_id}", "16fa4f03-15cb-46da-bb96-1e3f672688fe")))
                .andDo(print()).andExpect(status().isOk()).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        CompletionPSPData completionPSPData = json.deserialize(contentAsString, CompletionPSPData.class);
        Assertions.assertNotNull(completionPSPData);
    }
}

