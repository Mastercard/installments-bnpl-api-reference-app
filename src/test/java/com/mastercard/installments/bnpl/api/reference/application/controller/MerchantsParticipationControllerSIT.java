package com.mastercard.installments.bnpl.api.reference.application.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.openapitools.client.JSON;
import org.openapitools.client.model.MerchantMidSearchParameters;
import org.openapitools.client.model.MerchantsInner;
import org.openapitools.client.model.PostMerchantMidSearchesRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class MerchantsParticipationControllerSIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    private JSON json = new JSON();

    @Test
    @DisplayName("GET Merchant Participation API")
    @Order(2)
    void getMerchantsParticipations() throws Exception {

        MvcResult mvcResult = this.mockMvc
                .perform(get("/demo/merchants-participations")
                        .param("request_id", "233235362636365824")
                        .param("offset", "0")
                        .param("limit", "500"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        log.info("contentAsString {}", contentAsString);
        List<MerchantsInner> merchants = json.deserialize(contentAsString, List.class);

        Assertions.assertNotNull(merchants);
    }

    @Test
    @DisplayName("POST Mid Searches API")
    @Order(3)
    void postMidSearchesMerchantsParticipations() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(post("/demo/merchants-mids-searches")
                .contentType(
                        MediaType.APPLICATION_JSON).content(
                        gson.toJson(midSearchesParams()))).andDo(print()).andExpect(status().isOk()).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        Assertions.assertNotNull(json.deserialize(contentAsString, Object.class));
    }

    private PostMerchantMidSearchesRequest midSearchesParams() {
        PostMerchantMidSearchesRequest midSearchRequest = new PostMerchantMidSearchesRequest();
        MerchantMidSearchParameters merchantMidSearchParameters = new MerchantMidSearchParameters();
        merchantMidSearchParameters.setMerchantLegalName("Merchant 123 in GBR");
        merchantMidSearchParameters.setCountryCode("GBR");
        merchantMidSearchParameters.setAcquirerICA("110099");

        midSearchRequest.setActualInstance(merchantMidSearchParameters);
        return midSearchRequest;
    }
}