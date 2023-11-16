package org.mojodojocasahouse.extra.service;

import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.mojodojocasahouse.extra.dto.*;
import org.mojodojocasahouse.extra.model.ExtraBudget;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.BudgetRepository;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;


    public ApiResponse addBudget(ExtraUser user, BudgetAddingRequest budgetAddingRequest) {
        //create budget entity from request data
        ExtraBudget newBudget = ExtraBudget.from(budgetAddingRequest, user);
        //check if a budget for that category already exists within dates
        List<ExtraBudget> existingBudget = budgetRepository.findBudgetByUserAndCategoryAndStartDateAndEndDate(user, budgetAddingRequest.getCategory(), budgetAddingRequest.getStartingDate(), budgetAddingRequest.getLimitDate());
        if (!existingBudget.isEmpty()) {
            return new ApiResponse("Error. A budget for that category already exists within the given dates");
        }
        //Save new budget
        budgetRepository.save(newBudget);
        return new ApiResponse("Budget added succesfully!");
    }
    public ApiResponse editBudget(ExtraUser user, Long budgetId, @Valid BudgetEditingRequest budgetEditingRequest) {
        // Check if the budget with the given ID exists

        Optional<ExtraBudget> budgetOptional = budgetRepository.findById(budgetId);
    
        if (budgetOptional.isPresent()) {
            // Get the existing budget
            ExtraBudget existingBudget = budgetOptional.get();
    
            // Update the properties of the existing budget with the new data
            existingBudget.updateFrom(budgetEditingRequest, user);
    
            // Save the updated budget
            budgetRepository.save(existingBudget);
    
            return new ApiResponse("Budget edited successfully!");
        } else {
            // Return an error response if the budget with the given ID is not found
            return new ApiResponse("Budget not found");
        }
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
        List<ExtraBudget> budgetObjects = budgetRepository.findAllBudgetsByUser(user);
        return budgetObjects.stream().map(ExtraBudget::asDto).collect(Collectors.toList());
    }
    public BudgetDTO getBudgetById(Long id) {
        return budgetRepository.findById(id).get().asDto();
    }
    public void addToActiveBudget(ExtraUser user, BigDecimal amountOfExpense, String category) {
        ExtraBudget activeBudget = budgetRepository.findActiveBudgetByUserAndCategory(user, category);
        if (activeBudget != null){
            activeBudget.addToCurrentAmount(amountOfExpense);
            budgetRepository.save(activeBudget);
        }
    }

    public BudgetDTO getActiveBudgetByCategoryAndDate(ExtraUser user, String category, Date date){
        ExtraBudget activeBudget = budgetRepository.findActiveBudgetByUserAndCategoryAndDate(user, category, date)
                .stream()
                .findFirst()
                .orElse(null);
        log.debug("Found active budgets: " + activeBudget );
        if(activeBudget == null){
            return null;
        }
        return activeBudget.asDto();
    }
}
