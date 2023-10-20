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

    private List<MerchantParticipationsInner> postMerchantParticipationRequest(){
        List<MerchantParticipationsInner> list = new ArrayList<>();
        MerchantParticipationsInner merchantParticipation2 = new MerchantParticipationsInner();
        merchantParticipation2.setMerchantLegalName("Costco");
        merchantParticipation2.setDbaNames(List.of("ACME"));
        merchantParticipation2.setAcquirerICA("110099");
        merchantParticipation2.setNonMerchantNegotiatedParticipation("N");
        merchantParticipation2.setCountryCode("GBR");
        merchantParticipation2.setDuns("179847439");
        merchantParticipation2.setWebsiteUrl("https://www.example.com");
        merchantParticipation2.setMids(List.of("587233456781534"));
        List<Wallet> wallets = new ArrayList<>();
        Wallet w = new Wallet().walletAcceptance("Y").walletId(1).walletMerchantId("2353463562345 | 125346356236 | 125446356237");
        wallets.add(w);
        merchantParticipation2.setWallets(wallets);
        Address address = new Address();
        address.setAddressLine1("line1");
        address.setAddressLine2("line2");
        address.setAddressLine3("line3");
        address.setCity("Bangalore");
        address.setState("Karnataka");
        address.setPostalCode("560068");
        merchantParticipation2.setAddress(address);

        MerchantParticipationsInner merchantParticipation1 = new MerchantParticipationsInner();
        merchantParticipation1.setMerchantLegalName("Costco");
        merchantParticipation1.setDbaNames(List.of("ACME"));
        merchantParticipation1.setAcquirerICA("110099");
        merchantParticipation1.setNonMerchantNegotiatedParticipation("N");
        merchantParticipation1.setCountryCode("GBR");
        merchantParticipation1.setDuns("179847439");
        merchantParticipation1.setWebsiteUrl("https://www.example.com");
        merchantParticipation1.setMids(List.of("587233456781534"));
        List<Wallet> wallets1 = new ArrayList<>();
        Wallet w1 = new Wallet().walletAcceptance("Y").walletId(1).walletMerchantId("2353463562345 | 125346356236 | 125446356237");
        wallets1.add(w1);
        merchantParticipation1.setWallets(wallets1);
        Address address1 = new Address();
        address1.setAddressLine1("line1");
        address1.setAddressLine2("line2");
        address1.setAddressLine3("line3");
        address1.setCity("Bangalore");
        address1.setState("Karnataka");
        address1.setPostalCode("560068");
        merchantParticipation1.setAddress(address1);
        list.add(merchantParticipation2);
        list.add(merchantParticipation1);
        return list;
    }

    @Test
    @DisplayName("GET Merchant Participation API")
    @Order(2)
    void getMerchantsParticipations() throws Exception {

        MvcResult mvcResult = this.mockMvc
                .perform(get("/demo/merchants/participations")
                        .param("request_id", "237977967819493376")
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

        MvcResult mvcResult = this.mockMvc.perform(post("/demo/merchants/mids/searches")
                .contentType(
                        MediaType.APPLICATION_JSON).content(
                        gson.toJson(midSearchesParams()))).andDo(print()).andExpect(status().isOk()).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        Assertions.assertNotNull(json.deserialize(contentAsString, Object.class));
    }

    private PostMerchantMidSearchesRequest midSearchesParams() {
        PostMerchantMidSearchesRequest midSearchRequest = new PostMerchantMidSearchesRequest();
        midSearchRequest.setMerchantLegalName("Test Merchant 10192023");
        midSearchRequest.setCountryCode("GBR");
        midSearchRequest.setAcquirerICA("242666");

        return midSearchRequest;
    }
}