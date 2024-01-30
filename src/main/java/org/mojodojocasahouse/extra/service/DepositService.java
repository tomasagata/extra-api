package org.mojodojocasahouse.extra.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mojodojocasahouse.extra.dto.model.InvestmentDTO;
import org.mojodojocasahouse.extra.dto.requests.InvestmentAddingRequest;
import org.mojodojocasahouse.extra.exception.CategoryNotFoundException;
import org.mojodojocasahouse.extra.model.*;
import org.mojodojocasahouse.extra.repository.BudgetRepository;
import org.mojodojocasahouse.extra.repository.CategoryRepository;
import org.mojodojocasahouse.extra.repository.DepositRepository;
import org.mojodojocasahouse.extra.repository.InvestmentRepository;
import org.mojodojocasahouse.extra.scheduling.InvestmentReturnJob;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class DepositService {

    private final CategoryService categoryService;

    private final DepositRepository depositRepository;
    private final InvestmentRepository investmentRepository;
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;

    private final Scheduler scheduler;


    public void depositInvestmentReturn(Investment investment) throws CategoryNotFoundException {

        Category reattachedCategory = categoryRepository
                .findById(investment.getCategory().getId())
                .orElseThrow(CategoryNotFoundException::new);

        // Create expense entity from request data
        Deposit savedDeposit = depositRepository.save(
                new Deposit(
                        investment.getName(),
                        investment.getDepositAmount(),
                        new Date(System.currentTimeMillis()),
                        investment.getUser(),
                        reattachedCategory,
                        null,
                        investment
                )
        );

        addDepositToActiveBudget(savedDeposit);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public InvestmentDTO createNewInvestment(ExtraUser user, InvestmentAddingRequest request) {
        Category category = getCategoryFromUserAndInvestmentRequest(user, request);
        Investment savedInvestment = createNewInvestmentFromRequestWithCategory(user, request, category);
        scheduleRecurrentDepositsOf(savedInvestment);
        return savedInvestment.asDto();
    }




    private Category getCategoryFromUserAndInvestmentRequest(ExtraUser user, InvestmentAddingRequest request) {
        return categoryService
                .fetchOrCreateCategoryFromUserAndNameAndIconId(user, request.getCategory(), request.getIconId());
    }

    private Investment createNewInvestmentFromRequestWithCategory(ExtraUser user,
                                                                  InvestmentAddingRequest request,
                                                                  Category category) throws CategoryNotFoundException {
        Category reattachedCategory = categoryRepository
                .findById(category.getId())
                .orElseThrow(CategoryNotFoundException::new);

        return investmentRepository.save(
                new Investment(
                        request.getName(),
                        request.getDownPaymentAmount(),
                        request.getDownPaymentTimestamp(),
                        request.getDepositAmount(),
                        request.getMaxNumberOfDeposits(),
                        request.getDepositIntervalInDays(),
                        user,
                        reattachedCategory
                )
        );
    }

    private void scheduleRecurrentDepositsOf(Investment investment) {
        JobDetail job = JobBuilder.newJob()
                .ofType(InvestmentReturnJob.class)
                .usingJobData("investmentId", investment.getId().toString())
                .withIdentity("JOB_INV_" + investment.getId().toString(), "INVESTMENTS")
                .withDescription("Job to regularly deposit income of investment")
                .storeDurably(true)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("TGR_INV_" + investment.getId().toString(), "INVESTMENTS")
                .withDescription("Trigger to deposit income of investment")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInHours(24 * investment.getDepositIntervalInDays())
                        .withMisfireHandlingInstructionNowWithRemainingCount()
                        .withRepeatCount(investment.getMaxNumberOfDeposits() - 1))
                .startAt(investment.getDownPaymentTimestamp())
                .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            log.error("There was an error scheduling a recurrentDepositJob: " + e.getMessage());
        }
    }

    private void addDepositToActiveBudget(Deposit deposit) {
        List<Budget> activeBudget = budgetRepository
                .findActiveBudgetByUserAndCategoryAndDate(deposit.getUser(), deposit.getCategory(), deposit.getDate());
        if (!activeBudget.isEmpty()){
            Budget foundBudget = activeBudget.stream().findFirst().get();
            deposit.setLinkedBudget(foundBudget);
        }
    }


    public List<InvestmentDTO> getInvestmentsOfUser(ExtraUser user) {
        return investmentRepository
                .findByUser(user)
                .stream()
                .map(Investment::asDto)
                .collect(Collectors.toList());
    }
}
