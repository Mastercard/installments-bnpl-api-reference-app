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
import org.openapitools.client.JSON;
import org.openapitools.client.api.MerchantsParticipationApi;
import org.openapitools.client.model.Error;
import org.openapitools.client.model.ErrorWrapper;
import org.openapitools.client.model.*;
import org.openapitools.client.model.MerchantsInner;
import org.openapitools.client.model.Merchant;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
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

    private final JSON json = new JSON();

    @BeforeEach
    void setUp() {
        merchantParticipationService = new MerchantParticipationService(apiConfiguration);
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

        MerchantMidSearchParameters merchantMidSearchParameters = new MerchantMidSearchParameters();
        merchantMidSearchParameters.setMerchantLegalName("merchant legal name");
        merchantMidSearchParameters.setCountryCode("GBR");
        merchantMidSearchParameters.setAcquirerICA("242666");
        PostMerchantMidSearchesRequest postMidSearchesRequest = new PostMerchantMidSearchesRequest();
        postMidSearchesRequest.setActualInstance(merchantMidSearchParameters);

        when(merchantsParticipationPostMidSearchesApi.postMerchantMidSearches(any(), anyInt(),
                anyInt())).thenReturn(getPostMidSearchesResponse());

        PostMerchantMidSearches200Response postMidSearchesResponse = merchantParticipationService.postMerchantMidSearches(postMidSearchesRequest, 0, 500);
        assertNotNull(postMidSearchesResponse);
    }

    private PostMerchantMidSearches200Response getPostMidSearchesResponse() {
        PostMerchantMidSearches200Response postMidSearchesResponse = new PostMerchantMidSearches200Response();
        postMidSearchesResponse.setActualInstance(new MidDetailsPage());
        return postMidSearchesResponse;
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

        Merchant merchant = new Merchant();
        merchant.setMerchantLegalName("merchant legal name");
        merchant.setCountryCode("USA");
        merchant.setAcquirerICA("242666");
        merchant.setSubmitterICA("15643290801");
        merchant.setDbaNames(List.of("ACME"));
        merchant.setNonMerchantNegotiatedParticipation("N");
        merchant.setStatus("PENDING");
        merchant.setDuns("879847426");
        merchant.setWebsiteUrl("https://www.example.com");
        Wallet wallet = new Wallet();
        wallet.setWalletId(1);
        wallet.setWalletAcceptance("Y");
        wallet.setWalletMerchantId("235346356234 | 125346356236 | 125446356237");
        merchant.setWallets(List.of(wallet));
        merchant.setWallets(Collections.emptyList());
        Address address = new Address();
        address.setAddressLine1("600 WEST");
        address.setAddressLine2("Street ABC");
        address.setAddressLine3("Suite 619");
        address.setCity("Ballwin");
        address.setState("MO");
        address.setPostalCode("63367");
        merchant.setAddress(address);
        MerchantsInner merchantsInnerFirst = new MerchantsInner(merchant);

        merchantList.add(merchantsInnerFirst);

        Merchant secondMerchant = new Merchant();
        secondMerchant.setMerchantLegalName("second merchant legal name");
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
        secondMerchant.setWallets(List.of(wallet));
        secondMerchant.setWallets(Collections.emptyList());
        Address secondMerchantAddress = new Address();
        secondMerchantAddress.setAddressLine1("900 Highland");
        secondMerchantAddress.setAddressLine2("Street XYZ");
        secondMerchantAddress.setAddressLine3("Suite 709");
        secondMerchantAddress.setCity("St Peters");
        secondMerchantAddress.setState("MA");
        secondMerchantAddress.setPostalCode("73367");
        secondMerchant.setAddress(secondMerchantAddress);
        MerchantsInner merchantsInnerSecond = new MerchantsInner(secondMerchant);

        merchantList.add(merchantsInnerSecond);

        return merchantList;
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