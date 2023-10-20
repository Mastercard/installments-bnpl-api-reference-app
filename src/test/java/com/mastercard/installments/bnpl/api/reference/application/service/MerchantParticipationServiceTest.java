package com.mastercard.installments.bnpl.api.reference.application.service;

import com.mastercard.installments.bnpl.api.reference.application.configuration.ApiConfiguration;
import com.mastercard.installments.bnpl.api.reference.application.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.client.ApiException;
import org.openapitools.client.ApiResponse;
import org.openapitools.client.JSON;
import org.openapitools.client.api.MerchantsParticipationApi;
import org.openapitools.client.model.Error;
import org.openapitools.client.model.ErrorWrapper;
import org.openapitools.client.model.*;
import org.openapitools.client.model.MerchantsInner;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class MerchantParticipationServiceTest {

    private MerchantParticipationService merchantParticipationService;

    @Mock
    private MerchantsParticipationApi merchantsParticipationGetApi;
    @Mock
    private MerchantsParticipationApi merchantsParticipationPostMidSearchesApi;

    @Mock
    private ApiConfiguration apiConfiguration;

    @Mock
    private MerchantsParticipationApi merchantsParticipationPostApi;


    private final JSON json = new JSON();

    @BeforeEach
    void setUp() {
        merchantParticipationService = new MerchantParticipationService(apiConfiguration);
        merchantsParticipationPostApi = mock(MerchantsParticipationApi.class);
        ReflectionTestUtils.setField(merchantParticipationService, "merchantsParticipationPostApi", merchantsParticipationPostApi);
        merchantsParticipationGetApi = mock(MerchantsParticipationApi.class);
        ReflectionTestUtils.setField(merchantParticipationService, "merchantsParticipationGetApi",
                merchantsParticipationGetApi);
        merchantsParticipationPostMidSearchesApi = mock(MerchantsParticipationApi.class);
        ReflectionTestUtils.setField(merchantParticipationService, "merchantsParticipationPostMidSearchesApi", merchantsParticipationPostMidSearchesApi);
    }

    @Test
    @DisplayName("getMerchantsParticipation - Success")
    void getMerchantsParticipations() throws ApiException, ServiceException {

        when(merchantsParticipationGetApi.getMerchantParticipations(anyLong(), anyInt(),
                anyInt())).thenReturn(getMerchants());

        List<MerchantsInner> merchants = merchantParticipationService.getMerchantParticipations(12345L, 0, 500);
        assertNotNull(merchants);
    }

    @Test
    @DisplayName("POST mid searches - Success")
    void postMidSearchesRequest() throws Exception {
        PostMerchantMidSearchesRequest postMidSearchesRequest = new PostMerchantMidSearchesRequest();

        when(merchantsParticipationPostMidSearchesApi.postMerchantMidSearches(any(), anyInt(),
                anyInt())).thenReturn(new PostMerchantMidSearches200Response());

        PostMerchantMidSearches200Response postMidSearchesResponse = merchantParticipationService.postMerchantMidSearches(postMidSearchesRequest, 0, 500);
        assertNotNull(postMidSearchesResponse);
    }

    @Test
    @DisplayName("getMerchantsParticipation throws exception")
    void getMerchantsParticipationsThrowsException() throws Exception {

        ApiException apiException = new ApiException("ApiException", null, 400, null, json.serialize(getErrorDetail()));
        when(merchantsParticipationGetApi.getMerchantParticipations(anyLong(), anyInt(),
                anyInt())).thenThrow(apiException);

        Assertions.assertThrows(ServiceException.class,
                () -> merchantParticipationService.getMerchantParticipations(12345L, 0, 500));

    }

    private List<MerchantsInner> getMerchants() {
        List<MerchantsInner> merchantList = new ArrayList<>();

        MerchantsInner firstMerchant = new MerchantsInner();
        firstMerchant.setMerchantLegalName("firstMerchant legal name");
        firstMerchant.setCountryCode("USA");
        firstMerchant.setAcquirerICA("242666");
        firstMerchant.setSubmitterICA("15643290801");
        firstMerchant.setDbaNames(List.of("ACME"));
        firstMerchant.setNonMerchantNegotiatedParticipation("N");
        firstMerchant.setStatus("PENDING");
        firstMerchant.setDuns("879847426");
        firstMerchant.setWebsiteUrl("https://www.example.com");
        Wallet wallet = new Wallet();
        wallet.setWalletId(1);
        wallet.setWalletAcceptance("Y");
        wallet.setWalletMerchantId("235346356234 | 125346356236 | 125446356237");
        firstMerchant.setWallets(List.of(wallet));
        firstMerchant.setWallets(Collections.emptyList());
        Address address = new Address();
        address.setAddressLine1("600 WEST");
        address.setAddressLine2("Street ABC");
        address.setAddressLine3("Suite 619");
        address.setCity("Ballwin");
        address.setState("MO");
        address.setPostalCode("63367");
        firstMerchant.setAddress(address);

        merchantList.add(firstMerchant);

        MerchantsInner secondMerchant = new MerchantsInner();
        secondMerchant.setMerchantLegalName("second firstMerchant legal name");
        secondMerchant.setCountryCode("GBR");
        secondMerchant.setAcquirerICA("123666");
        secondMerchant.setSubmitterICA("90801");
        secondMerchant.setDbaNames(List.of("ACME2"));
        secondMerchant.setNonMerchantNegotiatedParticipation("Y");
        secondMerchant.setStatus("PENDING");
        secondMerchant.setDuns("7865847426");
        secondMerchant.setWebsiteUrl("https://www.gbrexample.com");
        Wallet secondMerchantWallet = new Wallet();
        secondMerchantWallet.setWalletId(1);
        secondMerchantWallet.setWalletAcceptance("Y");
        secondMerchantWallet.setWalletMerchantId("235346356234 | 125346356236 | 125446356237");
        secondMerchant.setWallets(List.of(secondMerchantWallet));
        secondMerchant.setWallets(Collections.emptyList());
        Address secondMerchantAddress = new Address();
        secondMerchantAddress.setAddressLine1("900 Highland");
        secondMerchantAddress.setAddressLine2("Street XYZ");
        secondMerchantAddress.setAddressLine3("Suite 709");
        secondMerchantAddress.setCity("St Peters");
        secondMerchantAddress.setState("MA");
        secondMerchantAddress.setPostalCode("73367");
        secondMerchant.setAddress(secondMerchantAddress);

        merchantList.add(secondMerchant);

        return merchantList;
    }

    @Test
    @DisplayName("POST Merchants Participations API - Success")
    void postMerchantsRequest() throws Exception {

        List<MerchantParticipationsInner> merchantParticipationInners = postMerchantParticipationRequest();

        when(merchantsParticipationPostApi.postMerchantParticipationsWithHttpInfo(anyList())).thenReturn(getApiResponse());
        ResponseEntity<Void> voidResponseEntity = merchantParticipationService.postMerchantParticipations(merchantParticipationInners);
        assertEquals(202, voidResponseEntity.getStatusCode().value());
        assertNotNull(voidResponseEntity.getHeaders().get("Location"));
    }

    private ApiResponse<Void> getApiResponse(){
        ApiResponse<Void> response = new ApiResponse<>(202, Map.of("Location", List.of("343224523")));
        return response;
    }

    @Test
    @DisplayName("POST Merchants Participations API throws exception")
    void postMerchantsParticipationsThrowsException() throws Exception {

        ApiException apiException = new ApiException("ApiException", null, 400, null, json.serialize(getErrorDetail()));
        when(merchantsParticipationPostApi.postMerchantParticipationsWithHttpInfo(anyList())).thenThrow(apiException);

        Assertions.assertThrows(ServiceException.class,
                () -> merchantParticipationService.postMerchantParticipations(anyList()));

    }

    private List<MerchantParticipationsInner> postMerchantParticipationRequest(){
        List<MerchantParticipationsInner> list = new ArrayList<>();
        MerchantParticipationsInner merchantParticipation = new MerchantParticipationsInner();
        merchantParticipation.setMerchantLegalName("Costco");
        merchantParticipation.setDbaNames(List.of("ACME"));
        merchantParticipation.setAcquirerICA("110099");
        merchantParticipation.setNonMerchantNegotiatedParticipation("N");
        merchantParticipation.setCountryCode("USA");
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

    private ErrorWrapper getErrorDetail() {
        List<Error> errors = new ArrayList<>();
        Error error = new Error();
        error.setSource("customer-data-api");
        error.setReasonCode("invalid data: cardProductCode");
        error.setDescription("must match \"^(?:ETA|ETB|ETC|ETD|ETE|ETF|ETG|SPP|SPS)$\"");
        error.setRecoverable(false);
        errors.add(error);
        Errors errs = new Errors();
        errs.setError(errors);
        ErrorWrapper errorWrapper = new ErrorWrapper();
        errorWrapper.setErrors(errs);
        return errorWrapper;
    }


}