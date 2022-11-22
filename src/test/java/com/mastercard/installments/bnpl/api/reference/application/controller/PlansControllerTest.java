package com.mastercard.installments.bnpl.api.reference.application.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.client.JSON;
import org.openapitools.client.model.InstallmentPlan;
import org.openapitools.client.model.MerchantParticipation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PlansControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final JSON json = new JSON();

    @Test
    @DisplayName("Plans API")
    void getPlan() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(get("/plans/{plan_id}".replace("{plan_id}", "2fd24d3a-1f9e-4faf-97bf-caa1f7a3813f")))
                .andExpect(status().isOk()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        InstallmentPlan plan = json.deserialize(contentAsString, InstallmentPlan.class);
        Assertions.assertNotNull(plan);

    }
}