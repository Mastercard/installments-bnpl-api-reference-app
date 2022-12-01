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
import org.openapitools.client.api.ApprovalsApi;

import org.openapitools.client.model.ErrorWrapper;
import org.openapitools.client.model.Errors;
import org.openapitools.client.model.PlanApprovalParameters;
import org.openapitools.client.model.PSPData;
import org.openapitools.client.model.CompletionPSPData;
import org.openapitools.client.model.Error;
import org.openapitools.client.model.PlanApprovalParametersPaymentAuthorization;
import org.openapitools.client.model.PlanApprovalParametersPaymentAuthorizationBillingAddress;

import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ApprovalServiceTest {

    private ApprovalService approvalService;

    @Mock
    private ApprovalsApi approvalsApi;

    @Mock
    private ApiConfiguration apiConfiguration;

    private final JSON json = new JSON();

    @BeforeEach
    void setUp() throws ServiceException {
        approvalService = new ApprovalService(apiConfiguration);
        approvalsApi = mock(ApprovalsApi.class);
        ReflectionTestUtils.setField(approvalService, "approvalsApi",
                approvalsApi);
    }

    @Test
    @DisplayName("getApprovals - Success")
    void getApprovals() throws ApiException {
        UUID planId = UUID.fromString("b73a1377-e916-4496-80aa-5487a43a49df");
        when(approvalsApi.getPlanApprovalStatus(planId)).thenReturn(completionPSPData());
        CompletionPSPData completionPSPData = approvalsApi.getPlanApprovalStatus(planId);
        assertNotNull(completionPSPData);
    }

    @Test
    @DisplayName("getApprovals throws exception")
    void getApprovalsThrowsException() throws Exception {
        ApiException apiException = new ApiException("ApiException", null, 404, null, json.serialize(getErrorDetail()));
        when(approvalsApi.getPlanApprovalStatusWithHttpInfo(UUID.fromString("c73a1377-e916-4496-80aa-5487a43a49df"))).thenThrow(apiException);
        Assertions.assertThrows(ServiceException.class,
                () -> approvalService.getPlanApprovalStatus(UUID.fromString("c73a1377-e916-4496-80aa-5487a43a49df")));
    }

    @Test
    @DisplayName("POSTApprovals - Success")
    void postApprovals() throws ApiException {
        when(approvalsApi.planApproval(planApprovalParams(), true)).thenReturn(pspData());
        PSPData pspData = approvalsApi.planApproval(planApprovalParams(), true);
        assertNotNull(pspData);
    }

    @Test
    @DisplayName("POSTApprovals throws exception")
    void postApprovalsThrowsException() throws Exception {
        ApiException apiException = new ApiException("ApiException", null, 404, null, json.serialize(getErrorDetail()));
        when(approvalsApi.planApprovalWithHttpInfo(planApproval(), true)).thenThrow(apiException);
        Assertions.assertThrows(ServiceException.class,
                () -> approvalService.approvePlan(planApproval(), true));
    }


    public static CompletionPSPData completionPSPData() {
        CompletionPSPData completionPSPData = new CompletionPSPData();
        completionPSPData.setCallbackUrl("https://src.mastercard.com#%7B%22checkoutResponse%22%3A%22eyJpc3MiOiJodHRwczpcL1wvbWFzdGVyY2FyZC5jb20iLCJpYXQiOjE2NjYxMTQxNTgsImFsZyI6IlJTMjU2IiwianRpIjoiOTcxMmI2ZmYtNGFmMS00M2U2LTlmNDktNTg1ZGI3M2JmYTIzIiwia2lkIjoiMTQ5MTI2LXNyYy1wYXlsb2FkLXZlcmlmaWNhdGlvbiJ9.eyJzcmNDb3JyZWxhdGlvbklkIjoiZWNjYmYwODctZjE1OS00ZDY5LWIzMWQtMDBiZWM3NWMwNDc0Iiwic3JjaVRyYW5zYWN0aW9uSWQiOiI1MjZlMWQzMS0xZmViLTQwMjYtYWJjZi1jYTZjZTg1ZWU4Y2IiLCJtYXNrZWRDYXJkIjp7InNyY0RpZ2l0YWxDYXJkSWQiOiJiMGMyNDY2Mi03N2RiLTQ0ZGQtOGMzYy0zMDhhOTMxYWYzNzMiLCJwYW5CaW4iOiI1MTIwMzQiLCJwYW5MYXN0Rm91ciI6IjY4NjIiLCJkaWdpdGFsQ2FyZERhdGEiOnsic3RhdHVzIjoiQUNUSVZFIiwicHJlc2VudGF0aW9uTmFtZSI6IlRlc3QgSXNzdWVywq4iLCJkZXNjcmlwdG9yTmFtZSI6Im1hc3RlcmNhcmQiLCJhcnRVcmkiOiJodHRwczovL3NieC5hc3NldHMubWFzdGVyY2FyZC5jb20vY2FyZC1hcnQvY29tYmluZWQtaW1hZ2UtYXNzZXQvYmYxNTQ4NWYtODNlZS00ZGVlLWEzOGUtYTVlNjdiMzczMDA2LnBuZyIsImlzQ29CcmFuZGVkIjpmYWxzZX0sInBhbkV4cGlyYXRpb25Nb250aCI6IjExIiwicGFuRXhwaXJhdGlvblllYXIiOiIyMDIzIiwicGF5bWVudENhcmRUeXBlIjoiQ1JFRElUIiwibWFza2VkQmlsbGluZ0FkZHJlc3MiOnsibGluZTEiOiIxKioqKioqKioqKioqIiwibGluZTIiOiIyKiIsImNpdHkiOiJKZXJzZXlDaXR5Iiwic3RhdGUiOiJOSiIsImNvdW50cnlDb2RlIjoiVVMiLCJ6aXAiOiIwNzMwNiJ9LCJzZXJ2aWNlSWQiOiJTUkNfQzJQI01DSSMwMSIsImRhdGVPZkNhcmRDcmVhdGVkIjoiMjAyMi0wOS0yOVQyMDoyNzoxMS4zMzRaIiwiZGF0ZU9mQ2FyZExhc3RVc2VkIjoiMjAyMi0xMC0xOFQxNzoxODozMy41MzJaIn0sImN1c3RvbU91dHB1dERhdGEiOnt9LCJhc3N1cmFuY2VEYXRhIjp7ImNhcmRWZXJpZmljYXRpb25FbnRpdHkiOiIwMiIsImNhcmRWZXJpZmljYXRpb25NZXRob2QiOiIwMyIsImNhcmRWZXJpZmljYXRpb25SZXN1bHRzIjoiMDMifX0.yiH4VkoaNRcTjs1gUtGRTkFgRdywm66_Ipw9NMnMav0GN2gkzUMGq29Eh8e1gZfqgjq1Tisf2K7fO0IA33LXfxV5vmQIuDKb2Qz4u6OPiA8-IKqEv95vlA4aK3V-IwtHmO2zNiSvKHYT34ABeAyXVJTTaH1-hOokKT3i_V77etH4f6y7JWBJ_w6Z3cnoEVLGl1LYWq9g1XskHJ0fYy2u0rJr4f_1xiOiqejVWvxtFS7v-Z-mltk1iM-6A_T7LkGGDplBaGJKEVgBfmeP6bQwGc6nt2qKyX6RyB1mb0ry7bFLJ3yofzfDmOTiDIDvFi4KfpUJ2KUWQZcBlVy6IkUjCQ%22%2C%22action%22%3A%22COMPLETE%22%7D");
        completionPSPData.setStatus("COMPLETED");
        return completionPSPData;
    }

    private ErrorWrapper getErrorDetail() {
        List<Error> errors = new ArrayList<>();
        Error error = new Error();
        error.setSource("mci-installment-approvals-api");
        error.setReasonCode("MCIAPR0006E");
        error.setDescription("Requested entity not found");
        error.setDetails("No initiationPSP result found in cache for planId: c73a1377-e916-4496-80aa-005487a43a49");
        error.setRecoverable(false);
        errors.add(error);
        Errors errs = new Errors();
        errs.setError(errors);
        ErrorWrapper errorWrapper = new ErrorWrapper();
        errorWrapper.setErrors(errs);
        return errorWrapper;
    }

    public static PlanApprovalParameters planApprovalParams() {
        PlanApprovalParameters planApprovalParameters = new PlanApprovalParameters();
        planApprovalParameters.setPlanId(UUID.fromString("b73a1377-e916-4496-80aa-5487a43a49df"));
        planApprovalParameters.setStatus(PlanApprovalParameters.StatusEnum.valueOf("APPROVED"));
        PlanApprovalParametersPaymentAuthorization planApprovalParametersPaymentAuthorization = new PlanApprovalParametersPaymentAuthorization();
        planApprovalParametersPaymentAuthorization.setPrimaryAccountNumber("5455031500000371");
        planApprovalParametersPaymentAuthorization.setPanExpirationMonth("11");
        planApprovalParametersPaymentAuthorization.setPanExpirationYear("2022");
        planApprovalParametersPaymentAuthorization.setCardSecurityCode("123");
        planApprovalParametersPaymentAuthorization.setCardholderFullName("Matthew Jacobs");
        planApprovalParametersPaymentAuthorization.setCardholderFirstName("Matthew");
        planApprovalParametersPaymentAuthorization.setCardholderLastName("Jacobs");
        PlanApprovalParametersPaymentAuthorizationBillingAddress billingAddress = new PlanApprovalParametersPaymentAuthorizationBillingAddress();
        billingAddress.setCountryCode("US");
        billingAddress.setLine1("100 Main Street");
        billingAddress.setCity("New York");
        billingAddress.setState("NY");
        billingAddress.setZip("11111");
        planApprovalParametersPaymentAuthorization.setBillingAddress(billingAddress);
        planApprovalParameters.setPaymentAuthorization(planApprovalParametersPaymentAuthorization);
        planApprovalParameters.setApprovedAmount(300.00);
        planApprovalParameters.setApprovedCurrency("USD");
        return planApprovalParameters;
    }
    public static PlanApprovalParameters planApproval() {
        PlanApprovalParameters planApprovalParameters = new PlanApprovalParameters();
        planApprovalParameters.setPlanId(UUID.fromString("c73a1377-e916-4496-80aa-5487a43a49df"));
        planApprovalParameters.setStatus(PlanApprovalParameters.StatusEnum.valueOf("APPROVED"));
        PlanApprovalParametersPaymentAuthorization planApprovalParametersPaymentAuthorization = new PlanApprovalParametersPaymentAuthorization();
        planApprovalParametersPaymentAuthorization.setPrimaryAccountNumber("5455031500000371");
        planApprovalParametersPaymentAuthorization.setPanExpirationMonth("11");
        planApprovalParametersPaymentAuthorization.setPanExpirationYear("2022");
        planApprovalParametersPaymentAuthorization.setCardSecurityCode("123");
        planApprovalParametersPaymentAuthorization.setCardholderFullName("Matthew Jacobs");
        planApprovalParametersPaymentAuthorization.setCardholderFirstName("Matthew");
        planApprovalParametersPaymentAuthorization.setCardholderLastName("Jacobs");
        PlanApprovalParametersPaymentAuthorizationBillingAddress billingAddress = new PlanApprovalParametersPaymentAuthorizationBillingAddress();
        billingAddress.setCountryCode("US");
        billingAddress.setLine1("100 Main Street");
        billingAddress.setCity("New York");
        billingAddress.setState("NY");
        billingAddress.setZip("11111");
        planApprovalParametersPaymentAuthorization.setBillingAddress(billingAddress);
        planApprovalParameters.setPaymentAuthorization(planApprovalParametersPaymentAuthorization);
        planApprovalParameters.setApprovedAmount(300.00);
        planApprovalParameters.setApprovedCurrency("USD");
        return planApprovalParameters;
    }

    public static PSPData pspData() {
        PSPData pspData = new PSPData();
        pspData.setCallbackUrl("https://src.mastercard.com#%7B%22checkoutResponse%22%3A%22eyJpc3MiOiJodHRwczpcL1wvbWFzdGVyY2FyZC5jb20iLCJpYXQiOjE2NjYxMTQxNTgsImFsZyI6IlJTMjU2IiwianRpIjoiOTcxMmI2ZmYtNGFmMS00M2U2LTlmNDktNTg1ZGI3M2JmYTIzIiwia2lkIjoiMTQ5MTI2LXNyYy1wYXlsb2FkLXZlcmlmaWNhdGlvbiJ9.eyJzcmNDb3JyZWxhdGlvbklkIjoiZWNjYmYwODctZjE1OS00ZDY5LWIzMWQtMDBiZWM3NWMwNDc0Iiwic3JjaVRyYW5zYWN0aW9uSWQiOiI1MjZlMWQzMS0xZmViLTQwMjYtYWJjZi1jYTZjZTg1ZWU4Y2IiLCJtYXNrZWRDYXJkIjp7InNyY0RpZ2l0YWxDYXJkSWQiOiJiMGMyNDY2Mi03N2RiLTQ0ZGQtOGMzYy0zMDhhOTMxYWYzNzMiLCJwYW5CaW4iOiI1MTIwMzQiLCJwYW5MYXN0Rm91ciI6IjY4NjIiLCJkaWdpdGFsQ2FyZERhdGEiOnsic3RhdHVzIjoiQUNUSVZFIiwicHJlc2VudGF0aW9uTmFtZSI6IlRlc3QgSXNzdWVywq4iLCJkZXNjcmlwdG9yTmFtZSI6Im1hc3RlcmNhcmQiLCJhcnRVcmkiOiJodHRwczovL3NieC5hc3NldHMubWFzdGVyY2FyZC5jb20vY2FyZC1hcnQvY29tYmluZWQtaW1hZ2UtYXNzZXQvYmYxNTQ4NWYtODNlZS00ZGVlLWEzOGUtYTVlNjdiMzczMDA2LnBuZyIsImlzQ29CcmFuZGVkIjpmYWxzZX0sInBhbkV4cGlyYXRpb25Nb250aCI6IjExIiwicGFuRXhwaXJhdGlvblllYXIiOiIyMDIzIiwicGF5bWVudENhcmRUeXBlIjoiQ1JFRElUIiwibWFza2VkQmlsbGluZ0FkZHJlc3MiOnsibGluZTEiOiIxKioqKioqKioqKioqIiwibGluZTIiOiIyKiIsImNpdHkiOiJKZXJzZXlDaXR5Iiwic3RhdGUiOiJOSiIsImNvdW50cnlDb2RlIjoiVVMiLCJ6aXAiOiIwNzMwNiJ9LCJzZXJ2aWNlSWQiOiJTUkNfQzJQI01DSSMwMSIsImRhdGVPZkNhcmRDcmVhdGVkIjoiMjAyMi0wOS0yOVQyMDoyNzoxMS4zMzRaIiwiZGF0ZU9mQ2FyZExhc3RVc2VkIjoiMjAyMi0xMC0xOFQxNzoxODozMy41MzJaIn0sImN1c3RvbU91dHB1dERhdGEiOnt9LCJhc3N1cmFuY2VEYXRhIjp7ImNhcmRWZXJpZmljYXRpb25FbnRpdHkiOiIwMiIsImNhcmRWZXJpZmljYXRpb25NZXRob2QiOiIwMyIsImNhcmRWZXJpZmljYXRpb25SZXN1bHRzIjoiMDMifX0.yiH4VkoaNRcTjs1gUtGRTkFgRdywm66_Ipw9NMnMav0GN2gkzUMGq29Eh8e1gZfqgjq1Tisf2K7fO0IA33LXfxV5vmQIuDKb2Qz4u6OPiA8-IKqEv95vlA4aK3V-IwtHmO2zNiSvKHYT34ABeAyXVJTTaH1-hOokKT3i_V77etH4f6y7JWBJ_w6Z3cnoEVLGl1LYWq9g1XskHJ0fYy2u0rJr4f_1xiOiqejVWvxtFS7v-Z-mltk1iM-6A_T7LkGGDplBaGJKEVgBfmeP6bQwGc6nt2qKyX6RyB1mb0ry7bFLJ3yofzfDmOTiDIDvFi4KfpUJ2KUWQZcBlVy6IkUjCQ%22%2C%22action%22%3A%22COMPLETE%22%7D");
        return pspData;
    }
}
