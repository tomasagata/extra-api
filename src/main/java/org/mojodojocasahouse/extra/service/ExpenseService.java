package org.mojodojocasahouse.extra.service;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.mojodojocasahouse.extra.dto.model.ExpenseDTO;
import org.mojodojocasahouse.extra.dto.requests.ExpenseAddingRequest;
import org.mojodojocasahouse.extra.dto.requests.ExpenseEditingRequest;
import org.mojodojocasahouse.extra.dto.responses.ApiResponse;
import org.mojodojocasahouse.extra.exception.ExpenseNotFoundException;
import org.mojodojocasahouse.extra.model.*;
import org.mojodojocasahouse.extra.repository.BudgetRepository;
import org.mojodojocasahouse.extra.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;

    private final CategoryService categoryService;

    @Transactional(Transactional.TxType.REQUIRED)
    public ApiResponse addExpense(ExtraUser user, ExpenseAddingRequest request) {

        // Get existing Category or create new one if it doesn't exist
        Category category = categoryService
                .fetchOrCreateCategoryFromUserAndNameAndIconId(user, request.getCategory(), request.getIconId());

        // Create expense entity from request data
        Expense savedExpense = expenseRepository.save(
                new Expense(
                        user,
                        request.getConcept(),
                        request.getAmount(),
                        request.getDate(),
                        category
                )
        );

        this.addExpenseToActiveBudget(savedExpense);

        return new ApiResponse("Expense added successfully!");
    }

    public List<ExpenseDTO> getAllExpensesByUserId(ExtraUser user) {
        List<Expense> expenseObjects = expenseRepository.findAllExpensesByUser(user);
        return expenseObjects
                .stream()
                .map(Expense::asDto)
                .collect(Collectors.toList());
    }

    public ApiResponse editExpense(Long expenseId,
                                   @Valid ExpenseEditingRequest request) throws ExpenseNotFoundException {

        // Get the existing expense.
        Expense existingExpense = expenseRepository
                .findById(expenseId)
                .orElseThrow(ExpenseNotFoundException::new);

        // Get the new (or existing) category. Does not matter if category changes or not.
        Category category = categoryService
                .fetchOrCreateCategoryFromUserAndNameAndIconId(
                        existingExpense.getUser(), request.getCategory(), request.getIconId());

        this.removeExpenseFromActiveBudget(existingExpense);

        // Update the properties of the existing expense with the new data.
        existingExpense.update(
                request.getConcept(),
                request.getAmount(),
                request.getDate(),
                category
        );

        this.addExpenseToActiveBudget(existingExpense);

        return new ApiResponse("Expense edited successfully!");
    }

    public boolean existsById(Long id) {
        return expenseRepository.existsById(id);
    }

    public void deleteById(Long id) throws ExpenseNotFoundException{
        Expense existingExpense = expenseRepository.findById(id).orElseThrow(ExpenseNotFoundException::new);
        this.removeExpenseFromActiveBudget(existingExpense);
        expenseRepository.deleteById(id);
    }

    public boolean isOwner(ExtraUser user, Long id) {
        return expenseRepository.existsByIdAndUser(id, user);
    }

    public List<Map<String, String>> getSumOfExpensesOfUserByCategoriesAndDateRanges(ExtraUser user,
                                                                                         List<String> categories,
                                                                                         Date from, Date until) {
        List<String> filteringCategories = categories;

        if(filteringCategories == null || filteringCategories.isEmpty()){
            filteringCategories = categoryService.getAllCategoryNamesOfUser(user);
        }

        if (from == null && until == null){
            return expenseRepository
                    .getSumOfExpensesByCategories(user, filteringCategories);
        } else if (from == null) {
            return expenseRepository
                    .getSumOfExpensesOfUserBeforeGivenDate(user, filteringCategories, until);
        } else if (until == null) {
            return expenseRepository
                    .getSumOfExpensesOfUserAfterGivenDate(user, filteringCategories, from);
        }

        return expenseRepository
                .getSumOfExpensesOfUserByCategoryAndDateInterval(user, filteringCategories, from, until);
    }

    public List<ExpenseDTO> getExpensesOfUserByCategoriesAndDateRanges(ExtraUser user,
                                                                       List<String> categories,
                                                                       Date from, Date until) {
        List<String> filteringCategories = categories;

        if(filteringCategories == null || filteringCategories.isEmpty()){
            filteringCategories = categoryService.getAllCategoryNamesOfUser(user);
        }

        if (from == null && until == null){
            return expenseRepository
                    .getExpensesOfUserByCategory(user, filteringCategories)
                    .stream()
                    .map(Expense::asDto)
                    .collect(Collectors.toList());
        } else if (from == null) {
            return expenseRepository
                    .getExpensesOfUserBeforeGivenDate(user, filteringCategories, until)
                    .stream()
                    .map(Expense::asDto)
                    .collect(Collectors.toList());
        } else if (until == null) {
            return expenseRepository
                    .getExpensesOfUserAfterGivenDate(user, filteringCategories, from)
                    .stream()
                    .map(Expense::asDto)
                    .collect(Collectors.toList());
        }

        return expenseRepository
                .getExpensesOfUserByCategoriesAndDateInterval(user, filteringCategories, from, until)
                .stream()
                .map(Expense::asDto)
                .collect(Collectors.toList());
    }

    public List<Map<String, String>> getYearlySumOfExpensesOfUserByCategoriesAndDateRanges(ExtraUser user,
                                                                                               List<String> categories,
                                                                                               Date from,
                                                                                               Date until) {
        List<String> filteringCategories = categories;

        if(filteringCategories == null || filteringCategories.isEmpty()){
            filteringCategories = categoryService.getAllCategoryNamesOfUser(user);
        }

        if (from == null && until == null){
            return expenseRepository
                    .getYearlySumOfExpensesByCategories(user, filteringCategories);
        } else if (from == null) {
            return expenseRepository
                    .getYearlySumOfExpensesOfUserBeforeGivenDate(user, filteringCategories, until);
        } else if (until == null) {
            return expenseRepository
                    .getYearlySumOfExpensesOfUserAfterGivenDate(user, filteringCategories, from);
        }

        return expenseRepository
                .getYearlySumOfExpensesOfUserByCategoryAndDateInterval(user, filteringCategories, from, until);

    }

    public void addExpenseToActiveBudget(Expense expense) {
        List<Budget> activeBudget = budgetRepository
                .findActiveBudgetByUserAndCategoryAndDate(expense.getUser(), expense.getCategory(),expense.getDate());
        if (!activeBudget.isEmpty()){
            log.debug("Found an active budget");
            Budget foundBudget = activeBudget.stream().findFirst().get();
            expense.setLinkedBudget(foundBudget);
        }
        log.debug("No active budget found");
    }

    public void removeExpenseFromActiveBudget(Expense expense) {
        expense.setLinkedBudget(null);
    }

    public void createDownPaymentExpense(Investment savedInvestment) {

        // Create expense entity from request data
        Expense savedExpense = expenseRepository.save(
                new Expense(
                        savedInvestment.getUser(),
                        savedInvestment.getName(),
                        savedInvestment.getDownPaymentAmount(),
                        new Date(System.currentTimeMillis()),
                        savedInvestment.getCategory()
                )
        );

        this.addExpenseToActiveBudget(savedExpense);
    }
}
