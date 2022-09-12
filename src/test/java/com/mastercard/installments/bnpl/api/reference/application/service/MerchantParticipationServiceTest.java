package com.mastercard.installments.bnpl.api.reference.application.service;

import com.mastercard.installments.bnpl.api.reference.application.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.JSON;
import org.openapitools.client.api.MerchantsParticipationApi;
import org.openapitools.client.model.Error;
import org.openapitools.client.model.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class MerchantParticipationServiceTest {

    private MerchantParticipationService merchantParticipationService;

    @Mock
    private MerchantsParticipationApi merchantsParticipationApi;

    @Mock
    private ApiClient apiClient;

    private final JSON json = new JSON();

    @BeforeEach
    void setUp() {
        merchantParticipationService = new MerchantParticipationService(apiClient);
        merchantsParticipationApi = mock(MerchantsParticipationApi.class);
        ReflectionTestUtils.setField(merchantParticipationService, "merchantsParticipationApi",
                                     merchantsParticipationApi);
    }

    @Test
    @DisplayName("getMerchantsParticipation - Success")
    void getMerchantsParticipations() throws ApiException, ServiceException {

        when(merchantsParticipationApi.getMerchantsParticipations(anyString(), anyInt(),
                                                                  anyInt())).thenReturn(merchantParticipation());

        MerchantParticipation merchantsParticipation = merchantParticipationService.getMerchantsParticipation("ETA",
                                                                                                              0, 500);
        assertNotNull(merchantsParticipation);
    }

    @Test
    @DisplayName("getMerchantsParticipation throws exception")
    void getMerchantsParticipationsThrowsException() throws Exception {

        ApiException apiException = new ApiException("ApiException", null, 400, null, json.serialize(getErrorDetail()));
        when(merchantsParticipationApi.getMerchantsParticipations(anyString(), anyInt(),
                                                                  anyInt())).thenThrow(apiException);

        Assertions.assertThrows(ServiceException.class,
                                () -> merchantParticipationService.getMerchantsParticipation("XYZ", 0, 500));

    }

    private MerchantParticipation merchantParticipation() {
        List<Merchant> merchantList = new ArrayList<>();

        Merchant merchant1 = new Merchant();
        merchant1.setCustomerId(9999999990L);
        merchant1.setCompanyName("Walmart");
        merchant1.setCountryCode("USA");
        merchant1.setProducts(Collections.emptyList());
        merchant1.setWallets(Collections.emptyList());
        merchantList.add(merchant1);

        Merchant merchant2 = new Merchant();
        merchant2.setCustomerId(9999999991L);
        merchant2.setCompanyName("Google");
        merchant2.setCountryCode("USA");
        merchant2.setProducts(Collections.emptyList());
        merchant2.setWallets(Collections.emptyList());
        merchantList.add(merchant2);

        MerchantParticipation merchantParticipation = new MerchantParticipation();
        merchantParticipation.setMerchants(merchantList);
        merchantParticipation.setOffset(0);
        merchantParticipation.setLimit(500);
        merchantParticipation.setCount(2);
        merchantParticipation.setTotal(2);

        return merchantParticipation;
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