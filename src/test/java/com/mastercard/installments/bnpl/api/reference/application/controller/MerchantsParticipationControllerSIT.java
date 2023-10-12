package com.mastercard.installments.bnpl.api.reference.application.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.openapitools.client.JSON;
import org.openapitools.client.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
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
    @DisplayName("POST Merchant Participation API")
    @Order(1)
    void postMerchantsParticipations() throws Exception {
        String data = gson.toJson(postMerchantParticipationRequest());
        MvcResult mvcResult = this.mockMvc.perform(post("/demo/merchants/participations")
                .contentType(
                        MediaType.APPLICATION_JSON).content(data))
                        .andDo(print())
                        .andExpect(status().isAccepted())
                        .andReturn();

        String location = mvcResult.getResponse().getHeader("Location");

        Assertions.assertNotNull(location);
    }

    @Test
    @DisplayName("GET Merchant Participation API")
    @Order(2)
    void getMerchantsParticipations() throws Exception {
        MvcResult mvcResult = this.mockMvc
                .perform(get("/demo/merchants-participations")
                        .param("request_id", "234596622925824000")
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
        String data = gson.toJson(midSearchesParams());
        MvcResult mvcResult = this.mockMvc.perform(post("/demo/merchants-mids-searches")
                .contentType(
                        MediaType.APPLICATION_JSON).content(
                        data)).andDo(print()).andExpect(status().isOk()).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        Assertions.assertNotNull(json.deserialize(contentAsString, Object.class));
    }

    private PostMerchantMidSearchesRequest midSearchesParams() {
        PostMerchantMidSearchesRequest midSearchRequest = new PostMerchantMidSearchesRequest();
        MerchantMidSearchParameters merchantMidSearchParameters = new MerchantMidSearchParameters();
        merchantMidSearchParameters.setMerchantLegalName("Costco31");
        merchantMidSearchParameters.setCountryCode("GBR");
        merchantMidSearchParameters.setAcquirerICA("110099");

        midSearchRequest.setActualInstance(merchantMidSearchParameters);
        return midSearchRequest;
    }

    private List<MerchantParticipationInner> postMerchantParticipationRequest(){
        List<MerchantParticipationInner> list = new ArrayList<>();
        MerchantParticipationInner merchantParticipation = new MerchantParticipationInner();
        merchantParticipation.setMerchantLegalName("Costco");
        merchantParticipation.setDbaNames(List.of("ACME"));
        merchantParticipation.setAcquirerICA("110099");
        merchantParticipation.setNonMerchantNegotiatedParticipation("N");
        merchantParticipation.setCountryCode("GBR");
        merchantParticipation.setDuns("179847439");
        merchantParticipation.setWebsiteUrl("https://www.example.com");
        merchantParticipation.setMids(List.of("587233456781534"));
        List<Wallet> wallets = new ArrayList<>();
        Wallet w = new Wallet().walletAcceptance("Y").walletId(1).walletMerchantId("2353463562345 | 125346356236 | 125446356237");
        wallets.add(w);
        merchantParticipation.setWallets(wallets);
        Address address = new Address();
        address.setAddressLine1("line1");
        address.setAddressLine2("line2");
        address.setAddressLine3("line3");
        address.setCity("Bangalore");
        address.setState("Karnataka");
        address.setPostalCode("560068");
        merchantParticipation.setAddress(address);
        list.add(merchantParticipation);
        return list;
    }
}