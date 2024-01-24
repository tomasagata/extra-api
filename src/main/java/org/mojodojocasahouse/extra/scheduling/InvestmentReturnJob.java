package org.mojodojocasahouse.extra.scheduling;

import lombok.Setter;
import org.mojodojocasahouse.extra.model.Investment;
import org.mojodojocasahouse.extra.repository.InvestmentRepository;
import org.mojodojocasahouse.extra.service.DepositService;
import org.mojodojocasahouse.extra.service.MessagingService;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Optional;

public class InvestmentReturnJob extends QuartzJobBean {

    @Setter
    private InvestmentRepository investmentRepository;

    @Setter
    private MessagingService messagingService;

    @Setter
    private DepositService depositService;

    @Setter
    private Long investmentId;


    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Investment investment = getInvestment();
        depositInvestmentReturn(investment);
        sendPushNotificationToUserDevices(investment);
    }

    private Investment getInvestment() throws JobExecutionException {
        Optional<Investment> expectedInvestment = investmentRepository.findById(investmentId);
        if (expectedInvestment.isPresent()) {
            return expectedInvestment.get();
        }

        JobExecutionException executionException = new JobExecutionException("Investment not found");
        executionException.setRefireImmediately(false);
        executionException.setUnscheduleFiringTrigger(true);
        throw executionException;
    }

    private void depositInvestmentReturn(Investment investment) {
        depositService.depositInvestmentReturn(investment);
    }

    private void sendPushNotificationToUserDevices(Investment investment) {
        messagingService.sendPushNotificationToDevicesOfUser(investment);
    }

}
