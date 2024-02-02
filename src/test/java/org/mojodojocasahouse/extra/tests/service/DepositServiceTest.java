package org.mojodojocasahouse.extra.tests.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mojodojocasahouse.extra.dto.model.InvestmentDTO;
import org.mojodojocasahouse.extra.dto.requests.InvestmentAddingRequest;
import org.mojodojocasahouse.extra.model.*;
import org.mojodojocasahouse.extra.repository.BudgetRepository;
import org.mojodojocasahouse.extra.repository.CategoryRepository;
import org.mojodojocasahouse.extra.repository.DepositRepository;
import org.mojodojocasahouse.extra.repository.InvestmentRepository;
import org.mojodojocasahouse.extra.service.CategoryService;
import org.mojodojocasahouse.extra.service.DepositService;
import org.mojodojocasahouse.extra.service.ExpenseService;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class DepositServiceTest {

    @Mock
    private CategoryService categoryService;
    @Mock
    private ExpenseService expenseService;
    @Mock
    private DepositRepository depositRepository;
    @Mock
    private InvestmentRepository investmentRepository;
    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private Scheduler scheduler;
    @InjectMocks
    private DepositService depositService;


    @Test
    public void testDepositingAnInvestmentReturnWhereNoBudgetsAreActive_DoesNotLinkTheDepositToAnything() {
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "Mj@me.com",
                "Somepass1!"
        );
        Category category = new Category("test category", (short) 1, user);
        Investment investment = new Investment(
                "test investment",
                BigDecimal.TEN,
                Timestamp.valueOf("2023-10-10 00:00:00"),
                BigDecimal.ONE,
                10,
                1,
                user,
                category
        );
        Deposit savedDeposit = new Deposit(
                investment.getName(),
                investment.getDepositAmount(),
                new Date(System.currentTimeMillis()),
                investment.getUser(),
                investment.getCategory(),
                null,
                investment
        );

        given(depositRepository.save(any()))
                .willReturn(savedDeposit);
        given(budgetRepository.findActiveBudgetByUserAndCategoryAndDate(any(), any(), any()))
                .willReturn(List.of());

        depositService.depositInvestmentReturn(investment);

        Assertions.assertThat(savedDeposit.getLinkedBudget()).isNull();
    }

    @Test
    public void testDepositingAnInvestmentReturnWhereABudgetIsActive_LinkTheDepositToTheBudget() {
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "Mj@me.com",
                "Somepass1!"
        );
        Category category = new Category("test category", (short) 1, user);
        Investment investment = new Investment(
                "test investment",
                BigDecimal.TEN,
                Timestamp.valueOf("2023-10-10 00:00:00"),
                BigDecimal.ONE,
                10,
                1,
                user,
                category
        );
        Deposit savedDeposit = new Deposit(
                investment.getName(),
                investment.getDepositAmount(),
                Date.valueOf("2023-10-10"),
                investment.getUser(),
                investment.getCategory(),
                null,
                investment
        );
        Budget activeBudget = new Budget(
                user,
                "test budget",
                BigDecimal.TEN,
                Date.valueOf("2023-01-01"),
                Date.valueOf("2024-01-01"),
                category
        );

        given(depositRepository.save(any()))
                .willReturn(savedDeposit);
        given(budgetRepository.findActiveBudgetByUserAndCategoryAndDate(any(), any(), any()))
                .willReturn(List.of(activeBudget));

        depositService.depositInvestmentReturn(investment);

        Assertions.assertThat(savedDeposit.getLinkedBudget()).isEqualTo(activeBudget);
    }

    @Test
    public void testCreatingNewInvestmentWithSuccessfulScheduling() throws SchedulerException {
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "Mj@me.com",
                "Somepass1!"
        );
        Category category = new Category("test category", (short) 1, user);
        InvestmentAddingRequest request = new InvestmentAddingRequest(
                "test investment",
                BigDecimal.TEN,
                Timestamp.valueOf("2023-10-10 00:00:00"),
                BigDecimal.ONE,
                10,
                1,
                category.getName(),
                category.getIconId()
        );
        Investment savedInvestment = new Investment(
                request.getName(),
                request.getDownPaymentAmount(),
                request.getDepositStartTimestamp(),
                request.getDepositAmount(),
                request.getMaxNumberOfDeposits(),
                request.getDepositIntervalInDays(),
                user,
                category
        );
        Investment mockInvestment = mock(Investment.class);

        given(categoryService.fetchOrCreateCategoryFromUserAndNameAndIconId(any(), any(), any()))
                .willReturn(category);
        given(investmentRepository.save(any()))
                .willReturn(mockInvestment);
        given(scheduler.scheduleJob(any(), any())).willReturn(null);

        doAnswer(invocation -> 1L).when(mockInvestment).getId();
        doAnswer(invocation -> savedInvestment.getDepositStartTimestamp()).when(mockInvestment).getDepositStartTimestamp();
        doAnswer(invocation -> savedInvestment.getMaxNumberOfDeposits()).when(mockInvestment).getMaxNumberOfDeposits();
        doAnswer(invocation -> savedInvestment.getDepositIntervalInDays()).when(mockInvestment).getDepositIntervalInDays();
        doAnswer(invocation -> savedInvestment.asDto()).when(mockInvestment).asDto();

        InvestmentDTO response = depositService.createNewInvestment(user, request);

        Assertions.assertThat(response).isEqualTo(savedInvestment.asDto());
    }

    @Test
    public void testCreatingNewInvestmentWithUnsuccessfulSchedulingWillJustNotScheduleJob() throws SchedulerException {
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "Mj@me.com",
                "Somepass1!"
        );
        Category category = new Category("test category", (short) 1, user);
        InvestmentAddingRequest request = new InvestmentAddingRequest(
                "test investment",
                BigDecimal.TEN,
                Timestamp.valueOf("2023-10-10 00:00:00"),
                BigDecimal.ONE,
                10,
                1,
                category.getName(),
                category.getIconId()
        );
        Investment savedInvestment = new Investment(
                request.getName(),
                request.getDownPaymentAmount(),
                request.getDepositStartTimestamp(),
                request.getDepositAmount(),
                request.getMaxNumberOfDeposits(),
                request.getDepositIntervalInDays(),
                user,
                category
        );
        Investment mockInvestment = mock(Investment.class);

        given(categoryService.fetchOrCreateCategoryFromUserAndNameAndIconId(any(), any(), any()))
                .willReturn(category);
        given(investmentRepository.save(any()))
                .willReturn(mockInvestment);
        given(scheduler.scheduleJob(any(), any())).willThrow(SchedulerException.class);

        doAnswer(invocation -> 1L).when(mockInvestment).getId();
        doAnswer(invocation -> savedInvestment.getDepositStartTimestamp()).when(mockInvestment).getDepositStartTimestamp();
        doAnswer(invocation -> savedInvestment.getMaxNumberOfDeposits()).when(mockInvestment).getMaxNumberOfDeposits();
        doAnswer(invocation -> savedInvestment.getDepositIntervalInDays()).when(mockInvestment).getDepositIntervalInDays();
        doAnswer(invocation -> savedInvestment.asDto()).when(mockInvestment).asDto();

        InvestmentDTO response = depositService.createNewInvestment(user, request);

        Assertions.assertThat(response).isEqualTo(savedInvestment.asDto());
    }

    @Test
    public void testGettingInvestmentsOfUser() {
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "Mj@me.com",
                "Somepass1!"
        );
        Category category = new Category("test category", (short) 1, user);
        List<Investment> investments = List.of( new Investment(
                "test investment",
                BigDecimal.TEN,
                Timestamp.valueOf("2023-10-10 00:00:00"),
                BigDecimal.ONE,
                10,
                1,
                user,
                category
        ));
        InvestmentDTO[] expectedInvestments = investments
                .stream()
                .map(Investment::asDto)
                .toArray(InvestmentDTO[]::new);

        given(investmentRepository.findByUser(any())).willReturn(investments);

        List<InvestmentDTO> results = depositService.getInvestmentsOfUser(user);

        Assertions.assertThat(results).containsExactlyInAnyOrder(expectedInvestments);

    }

}
