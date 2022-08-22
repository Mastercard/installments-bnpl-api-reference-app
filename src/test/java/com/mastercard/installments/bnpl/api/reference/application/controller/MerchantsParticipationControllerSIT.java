package com.mastercard.installments.bnpl.api.reference.application.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.client.JSON;
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
class MerchantsParticipationControllerSIT {

    @Autowired
    private MockMvc mockMvc;

    private final JSON json = new JSON();

    @Test
    @DisplayName("Merchant Participation API")
    void getMerchantsParticipations() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(get("/demo/merchants-participations").param("card_product_code", "ETA").param("country_code", "USA").param("offset","0").param("limit","500"))
                .andDo(print()).andExpect(status().isOk()).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        MerchantParticipation merchantParticipation = json.deserialize(contentAsString, MerchantParticipation.class);

        Assertions.assertNotNull(merchantParticipation);

    }
}
