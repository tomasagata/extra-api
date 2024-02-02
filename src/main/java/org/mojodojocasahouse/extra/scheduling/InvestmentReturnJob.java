package org.mojodojocasahouse.extra.scheduling;

import jakarta.transaction.Transactional;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.mojodojocasahouse.extra.model.Investment;
import org.mojodojocasahouse.extra.repository.InvestmentRepository;
import org.mojodojocasahouse.extra.service.DepositService;
import org.mojodojocasahouse.extra.service.MessagingService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class InvestmentReturnJob extends QuartzJobBean {

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private MessagingService messagingService;

    @Autowired
    private DepositService depositService;

    @Setter
    private Long investmentId;

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Investment investment = getInvestment();
        depositInvestmentReturn(investment);
        sendPushNotificationToUserDevices(investment);
    }

    private Investment getInvestment() throws JobExecutionException {
        Optional<Investment> expectedInvestment = investmentRepository.findById(investmentId);
        if (expectedInvestment.isPresent()) {
            log.debug("Found investment with id " + investmentId + "." );
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
