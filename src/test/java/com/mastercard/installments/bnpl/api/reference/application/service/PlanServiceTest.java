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
import org.openapitools.client.api.PlansApi;
import org.openapitools.client.model.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class PlanServiceTest {

    private PlanService planService;

    @Mock
    private PlansApi plansApi;

    @Mock
    private ApiConfiguration apiConfiguration;

    private final JSON json = new JSON();

    @BeforeEach
    void setUp() throws ServiceException{
        planService = new PlanService(apiConfiguration);
        plansApi = mock(PlansApi.class);
        ReflectionTestUtils.setField(planService, "plansApi",
                                     plansApi);
    }

    @Test
    @DisplayName("getPlans - Success")
    void getPlans() throws ApiException, ServiceException {
        UUID planId = UUID.fromString("b73a1377-e916-4496-80aa-5487a43a49df");
        when(plansApi.getPlan(planId)).thenReturn(plan());
        InstallmentPlan plan = planService.getPlan(planId);
        assertNotNull(plan);
    }

    @Test
    @DisplayName("getPlans throws exception")
    void getPlansThrowsException() throws Exception {
        UUID planId = UUID.fromString("c73a1377-e916-4496-80aa-5487a43a49df");
        ApiException apiException = new ApiException("ApiException", null, 500, null, "{\"Errors\":{\"Error\":[{\"ReasonCode\":\"not.found\",\"Source\":\"mci-installment-bnpl-plans-api\",\"Description\":\"Resource is not found.\",\"Recoverable\":false}]}}\n");
        when(plansApi.getPlan(planId)).thenThrow(apiException);
        Assertions.assertThrows(ServiceException.class,
                                () -> planService.getPlan(planId));

    }

    public static InstallmentPlan plan() {
        InstallmentPlan installmentPlan = new InstallmentPlan();

        Consumer consumer = new Consumer();
        consumer.email("john.doe@test.com");
        consumer.isdCode("1");
        consumer.mobile("6360101010");
        consumer.providerConsumerId(null);

        MerchantPlan merchant = new MerchantPlan();
        merchant.acceptor("413275");
        merchant.country("USA");
        merchant.mcc("1711");
        merchant.name("Decor shop");

        String offerId = "21345735";
        UUID planId = UUID.fromString("b73a1377-e916-4496-80aa-5487a43a49df");

        PlanInstallmentInformation planInstallmentInformation = new PlanInstallmentInformation();
        planInstallmentInformation.apr(17.12);
        planInstallmentInformation.currency("USD");
        planInstallmentInformation.frequency("MONTHLY");
        planInstallmentInformation.installmentAmount(1014.2);
        planInstallmentInformation.installmentFee(0.0);
        planInstallmentInformation.tenure(3);
        planInstallmentInformation.totalAmount(100.0);

        UUID providerId = UUID.fromString("29694e7a-b4a8-11ec-b909-0242ac120002");
        String rejectReasonCode = null;
        String rejectReasonDetail = null;

        List<ScheduledRepayment> scheduledRepayments = new ArrayList<>();
        ScheduledRepayment scheduledRepayment1 = new ScheduledRepayment();
        scheduledRepayment1.amount(1014.2);
        scheduledRepayment1.dueDate("2023-09-07");
        scheduledRepayment1.installmentNumber(1);
        ScheduledRepayment scheduledRepayment2 = new ScheduledRepayment();
        scheduledRepayment2.amount(1014.2);
        scheduledRepayment2.dueDate("2023-10-07");
        scheduledRepayment2.installmentNumber(2);
        ScheduledRepayment scheduledRepayment3 = new ScheduledRepayment();
        scheduledRepayment3.amount(1014.2);
        scheduledRepayment3.dueDate("2023-11-07");
        scheduledRepayment3.installmentNumber(3);
        scheduledRepayments.add(scheduledRepayment1);
        scheduledRepayments.add(scheduledRepayment2);
        scheduledRepayments.add(scheduledRepayment3);

        String status = "APPROVED";
        Transaction transaction = new Transaction();
        transaction.amount(3000.0);
        transaction.currency("USD");

        installmentPlan.setConsumer(consumer);
        installmentPlan.setMerchant(merchant);
        installmentPlan.setOfferId(offerId);
        installmentPlan.setPlanId(planId);
        installmentPlan.setPlanInstallmentInformation(planInstallmentInformation);
        installmentPlan.setProviderId(providerId);
        installmentPlan.setRejectReasonCode(rejectReasonCode);
        installmentPlan.setRejectReasonDetail(rejectReasonDetail);
        installmentPlan.setScheduledRepayments(scheduledRepayments);
        installmentPlan.setStatus(status);
        installmentPlan.setTransaction(transaction);
        return installmentPlan;
    }
}