package org.mojodojocasahouse.extra.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.mojodojocasahouse.extra.dto.*;
import org.mojodojocasahouse.extra.model.ExtraExpense;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.ExtraExpenseRepository;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExtraExpenseRepository expenseRepository;
    private final BudgetService budgetService;

    public ApiResponse addExpense(ExtraUser user, ExpenseAddingRequest expenseAddingRequest) {
        //create expense entity from request data
        ExtraExpense newExpense = ExtraExpense.from(expenseAddingRequest, user);
        //handle if expense should add to a budget
        budgetService.addToActiveBudget(user, newExpense.getAmount(), newExpense.getCategory());
        //Save new expense
        expenseRepository.save(newExpense);
        return new ApiResponse("Expense added succesfully!");
    }

    public List<ExpenseDTO> getAllExpensesByUserId(ExtraUser user) {
        List<ExtraExpense> expenseObjects = expenseRepository.findAllExpensesByUser(user);
        return expenseObjects.stream().map(ExtraExpense::asDto).collect(Collectors.toList());
    }

    public List<ExpenseDTO> getAllExpensesByCategoryByUserId(ExtraUser user, String category) {
        List<ExtraExpense> expenseObjects = expenseRepository.findAllExpensesByUserAndCategory(user, category);
        return expenseObjects.stream().map(ExtraExpense::asDto).collect(Collectors.toList());
    }

    public List<String> getAllCategories(ExtraUser user) {
        return expenseRepository.findAllDistinctCategoriesByUser(user);
    }

    public ApiResponse editExpense(ExtraUser user, Long expenseId, @Valid ExpenseEditingRequest expenseEditingRequest) {
        // Check if the expense with the given ID exists

        Optional<ExtraExpense> expenseOptional = expenseRepository.findById(expenseId);
    
        if (expenseOptional.isPresent()) {
            // Get the existing expense
            ExtraExpense existingExpense = expenseOptional.get();
    
            // Update the properties of the existing expense with the new data
            existingExpense.updateFrom(expenseEditingRequest, user);
    
            // Save the updated expense
            expenseRepository.save(existingExpense);
    
            return new ApiResponse("Expense edited successfully!");
        } else {
            // Return an error response if the expense with the given ID is not found
            return new ApiResponse("Expense not found");
        }
    }

    public boolean existsById(Long id) {
        return expenseRepository.existsById(id);
    }

    public void deleteById(Long id) {
        expenseRepository.deleteById(id);
    }

    public boolean isOwner(ExtraUser user, Long id) {
        return expenseRepository.existsByIdAndUser(id, user);
    }

    public List<Map<String, BigDecimal>> getSumOfExpensesOfUserByCategoriesAndDateRanges(ExtraUser user,
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
                    .map(ExtraExpense::asDto)
                    .collect(Collectors.toList());
        } else if (from == null) {
            return expenseRepository
                    .getExpensesOfUserBeforeGivenDate(user, filteringCategories, until)
                    .stream()
                    .map(ExtraExpense::asDto)
                    .collect(Collectors.toList());
        } else if (until == null) {
            return expenseRepository
                    .getExpensesOfUserAfterGivenDate(user, filteringCategories, from)
                    .stream()
                    .map(ExtraExpense::asDto)
                    .collect(Collectors.toList());
        }

        return expenseRepository
                .getExpensesOfUserByCategoriesAndDateInterval(user, filteringCategories, from, until)
                .stream()
                .map(ExtraExpense::asDto)
                .collect(Collectors.toList());
    }

    public List<Map<String, String>> getAllCategoriesWithIcons(ExtraUser user) {
        return expenseRepository.findAllDistinctCategoriesByUserWithIcons(user);
    }
}
