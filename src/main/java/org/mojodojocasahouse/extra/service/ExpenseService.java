package org.mojodojocasahouse.extra.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.mojodojocasahouse.extra.dto.*;
import org.mojodojocasahouse.extra.exception.ExpenseNotFoundException;
import org.mojodojocasahouse.extra.model.Expense;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final BudgetService budgetService;

    public ApiResponse addExpense(ExtraUser user, ExpenseAddingRequest expenseAddingRequest) {
        //create expense entity from request data
        Expense newExpense = Expense.from(expenseAddingRequest, user);

        //handle if expense should add to a budget
        budgetService.addToActiveBudget(user, newExpense.getAmount(), newExpense.getCategory(), newExpense.getDate());

        //Save new expense
        expenseRepository.save(newExpense);
        return new ApiResponse("Expense added successfully!");
    }

    public List<ExpenseDTO> getAllExpensesByUserId(ExtraUser user) {
        List<Expense> expenseObjects = expenseRepository.findAllExpensesByUser(user);
        return expenseObjects
                .stream()
                .map(Expense::asDto)
                .collect(Collectors.toList());
    }

    public List<ExpenseDTO> getAllExpensesByCategoryByUserId(ExtraUser user, String category) {
        List<Expense> expenseObjects = expenseRepository.findAllExpensesByUserAndCategory(user, category);
        return expenseObjects
                .stream()
                .map(Expense::asDto)
                .collect(Collectors.toList());
    }

    public List<String> getAllCategories(ExtraUser user) {
        // Get categories from both expenses and budgets
        List<String> expenseCategories = expenseRepository.findAllDistinctCategoriesByUser(user);
        List<String> budgetCategories = budgetService.getAllCategories(user);

        // Unify them
        List<String> unifiedCategories = new ArrayList<>(expenseCategories);
        unifiedCategories.addAll(budgetCategories);

        // Remove duplicates
        return unifiedCategories
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    public ApiResponse editExpense(ExtraUser user,
                                   Long expenseId,
                                   @Valid ExpenseEditingRequest expenseEditingRequest) throws ExpenseNotFoundException {

        // Get the existing expense
        Expense existingExpense = expenseRepository
                .findById(expenseId)
                .orElseThrow(ExpenseNotFoundException::new);

        budgetService.removeFromActiveBudget(
                existingExpense.getUser(),
                existingExpense.getAmount(),
                existingExpense.getCategory(),
                existingExpense.getDate()
        );

        // Update the properties of the existing expense with the new data
        existingExpense.updateFrom(expenseEditingRequest, user);

        budgetService.addToActiveBudget(
                existingExpense.getUser(),
                existingExpense.getAmount(),
                existingExpense.getCategory(),
                existingExpense.getDate()
        );

        // Save the updated expense
        expenseRepository.save(existingExpense);

        return new ApiResponse("Expense edited successfully!");
    }

    public boolean existsById(Long id) {
        return expenseRepository.existsById(id);
    }

    public void deleteById(Long id) throws ExpenseNotFoundException{
        Expense expense = expenseRepository.findById(id).orElseThrow(ExpenseNotFoundException::new);

        budgetService.removeFromActiveBudget(expense.getUser(), expense.getAmount(), expense.getCategory(), expense.getDate());
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
            filteringCategories = this.getAllCategories(user);
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
            filteringCategories = this.getAllCategories(user);
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

    public List<CategoryWithIconDTO> getAllCategoriesWithIcons(ExtraUser user) {
        List<CategoryWithIconDTO> expenseCategories = expenseRepository.findAllDistinctCategoriesByUserWithIcons(user);
        List<CategoryWithIconDTO> budgetCategories = budgetService.getAllCategoriesWithIcons(user);

        // Unify them
        List<CategoryWithIconDTO> unifiedCategories = new ArrayList<>(expenseCategories);
        unifiedCategories.addAll(budgetCategories);

        ObjectMapper ob = new ObjectMapper();

        try {
            for (CategoryWithIconDTO catWithIcon : unifiedCategories) {
                log.debug(ob.writeValueAsString(catWithIcon));
            }
        } catch (Exception ignored) {

        }

        // Remove duplicates
        return unifiedCategories
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Map<String, String>> getYearlySumOfExpensesOfUserByCategoriesAndDateRanges(ExtraUser user,
                                                                                               List<String> categories,
                                                                                               Date from,
                                                                                               Date until) {
        List<String> filteringCategories = categories;

        if(filteringCategories == null || filteringCategories.isEmpty()){
            filteringCategories = this.getAllCategories(user);
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
}
