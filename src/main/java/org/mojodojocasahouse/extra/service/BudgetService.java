package org.mojodojocasahouse.extra.service;

import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.mojodojocasahouse.extra.dto.*;
import org.mojodojocasahouse.extra.exception.BudgetNotFoundException;
import org.mojodojocasahouse.extra.exception.ConflictingBudgetException;
import org.mojodojocasahouse.extra.model.Budget;
import org.mojodojocasahouse.extra.model.Expense;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.BudgetRepository;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;


    public ApiResponse addBudget(
            ExtraUser user,
            BudgetAddingRequest budgetAddingRequest) throws ConflictingBudgetException{

        //create budget entity from request data
        Budget newBudget = Budget.from(budgetAddingRequest, user);

        // Check if budget for that category already exists within dates
        List<Budget> existingBudget = budgetRepository
                .findBudgetByUserAndCategoryAndStartDateAndEndDate(user, budgetAddingRequest.getCategory(), budgetAddingRequest.getStartingDate(), budgetAddingRequest.getLimitDate());

        if (!existingBudget.isEmpty()) {
            throw new ConflictingBudgetException();
        }

        //Save new budget
        budgetRepository.save(newBudget);
        return new ApiResponse("Budget added successfully!");
    }

    public ApiResponse editBudget(
            ExtraUser user,
            Long budgetId,
            @Valid BudgetEditingRequest budgetEditingRequest) throws BudgetNotFoundException{

        // Check if the budget with the given ID exists
        Budget existingBudget = budgetRepository
                .findById(budgetId)
                .orElseThrow(BudgetNotFoundException::new);

        // Update the properties of the existing budget with the new data
        existingBudget.updateFrom(budgetEditingRequest, user);

        // Save the updated budget
        budgetRepository.save(existingBudget);

        return new ApiResponse("Budget edited successfully!");
    }

    public boolean existsById(Long id) {
        return budgetRepository.existsById(id);
    }

    public boolean isOwner(ExtraUser user, Long id) {
        return budgetRepository.existsByIdAndUser(id, user);
    }

    public void deleteById(Long id) {
        budgetRepository.deleteById(id);
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

    public void addToActiveBudget(ExtraUser user, BigDecimal amountOfExpense, String category, Date date) {
        List<Budget> activeBudget = budgetRepository.findActiveBudgetByUserAndCategoryAndDate(user, category, date);
        if (!activeBudget.isEmpty()){
            Budget foundBudget = activeBudget.stream().findFirst().get();
            foundBudget.addToCurrentAmount(amountOfExpense);
            budgetRepository.save(foundBudget);
        }
    }

    public BudgetDTO getActiveBudgetByCategoryAndDate(ExtraUser user, String category, Date date){
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

    public List<String> getAllCategories(ExtraUser user) {
        return budgetRepository.findAllDistinctCategoriesByUser(user);
    }

    public List<CategoryWithIconDTO> getAllCategoriesWithIcons(ExtraUser user) {
        return budgetRepository.findAllDistinctCategoriesByUserWithIcons(user);
    }

    public void removeFromActiveBudget(ExtraUser user, BigDecimal amountOfExpense, String category, Date date) {
        List<Budget> activeBudget = budgetRepository.findActiveBudgetByUserAndCategoryAndDate(user, category, date);
        if (!activeBudget.isEmpty()){
            Budget foundBudget = activeBudget.stream().findFirst().get();
            foundBudget.removeFromCurrentAmount(amountOfExpense);
            budgetRepository.save(foundBudget);
        }
    }
}
