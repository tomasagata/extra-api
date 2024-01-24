package org.mojodojocasahouse.extra.service;

import lombok.RequiredArgsConstructor;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.mojodojocasahouse.extra.dto.model.BudgetDTO;
import org.mojodojocasahouse.extra.dto.requests.BudgetAddingRequest;
import org.mojodojocasahouse.extra.dto.responses.ApiResponse;
import org.mojodojocasahouse.extra.exception.BudgetNotFoundException;
import org.mojodojocasahouse.extra.exception.ConflictingBudgetException;
import org.mojodojocasahouse.extra.model.*;
import org.mojodojocasahouse.extra.repository.BudgetRepository;
import org.mojodojocasahouse.extra.repository.ExpenseRepository;
import org.mojodojocasahouse.extra.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    private final CategoryService categoryService;

    public ApiResponse addBudget(
            ExtraUser user,
            BudgetAddingRequest budgetAddingRequest) throws ConflictingBudgetException {

        Category category = categoryService.fetchOrCreateCategoryFromUserAndNameAndIconId(
                user,
                budgetAddingRequest.getCategory(),
                budgetAddingRequest.getIconId()
        );

        // Create budget entity from request data
        Budget newBudget = Budget.from(budgetAddingRequest, category, user);

        this.verifyNoOverlappingBudgets(newBudget);

        // Save new budget
        budgetRepository.save(newBudget);

        this.updateTransactionsWithBudget(newBudget);

        return new ApiResponse("Budget added successfully!");
    }

    public List<BudgetDTO> getAllBudgetsByUserId(ExtraUser user) {
        List<Budget> budgetObjects = budgetRepository.findAllBudgetsByUser(user);
        return budgetObjects
                .stream()
                .map(Budget::asDto)
                .collect(Collectors.toList());
    }

    public BudgetDTO getBudgetById(Long id) throws BudgetNotFoundException {
        return budgetRepository
                .findById(id)
                .orElseThrow(BudgetNotFoundException::new)
                .asDto();
    }

    public BudgetDTO getActiveBudgetByCategoryAndDate(ExtraUser user, Category category, Date date){
        Budget activeBudget = budgetRepository
                .findActiveBudgetByUserAndCategoryAndDate(user, category, date)
                .stream()
                .findFirst()
                .orElse(null);

        log.debug("Found active budgets: " + activeBudget );
        if(activeBudget == null){
            return null;
        }
        return activeBudget.asDto();
    }

    public void verifyNoOverlappingBudgets(Budget budget) {
        List<Budget> overlaps = budgetRepository.findOverlappingBudgetsByUserAndCategory(
                budget.getUser(),
                budget.getCategory(),
                budget.getStartingDate(),
                budget.getLimitDate()
        );
        if(!overlaps.isEmpty()){
            throw new ConflictingBudgetException();
        }
    }

    public void updateTransactionsWithBudget(Budget budget) {
        List<Transaction> unlinkedTransactions = transactionRepository.getTransactionsByUserAndCategoryAndDateInterval(
                budget.getUser(),
                budget.getCategory(),
                budget.getStartingDate(),
                budget.getLimitDate()
        );

        for( Transaction transaction: unlinkedTransactions){
            transaction.setLinkedBudget(budget);
        }
    }
}
