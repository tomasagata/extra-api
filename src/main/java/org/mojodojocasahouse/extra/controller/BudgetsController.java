package org.mojodojocasahouse.extra.controller;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mojodojocasahouse.extra.dto.*;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.mojodojocasahouse.extra.service.BudgetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BudgetsController {

    private final AuthenticationService userService;

    private final BudgetService budgetService;

    //Create or post new budget
    @PostMapping(value = "/addBudget", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> addBudget(Principal principal,
                                             @Valid @RequestBody BudgetAddingRequest budgetAddingRequest){
        ExtraUser user = userService.getUserByPrincipal(principal);

        log.debug("Adding budget to user: \"" + user.getEmail() + "\"");
        
        
        ApiResponse response = budgetService.addBudget(user, budgetAddingRequest);
        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }
    //Edit Budget
    @PostMapping(value = "/editBudget/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> editBudget(Principal principal,
                                             @Valid @RequestBody BudgetEditingRequest budgetEditingRequest,
                                             @PathVariable Long id){
        ExtraUser user = userService.getUserByPrincipal(principal);
        //Check that the user making the edition is the owner of the budget
        if (!budgetService.isOwner(user, id)) {
            return new ResponseEntity<>(
                    new ApiResponse("Error. You are not the owner of the budget you are trying to delete"),
                    HttpStatus.FORBIDDEN
            );
        }
    // Check if the budget with the given ID exists
        if (!budgetService.existsById(id)) {
            return new ResponseEntity<>(
                    new ApiResponse("Error. Budget to edit not found"),
                    HttpStatus.NOT_FOUND
            );
        }
        log.debug("Editing Budget of user: \"" + user.getEmail() + "\"");

        ApiResponse response = budgetService.editBudget(user,id,budgetEditingRequest);
        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }
    //Delete budget
    @DeleteMapping("/deleteBudget/{id}")
    public ResponseEntity<ApiResponse> deleteBudget(Principal principal, @PathVariable Long id) {
        ExtraUser user = userService.getUserByPrincipal(principal);
    //Check that the user making the deletion is the owner of the budget
        if (!budgetService.isOwner(user, id)) {
            return new ResponseEntity<>(
                    new ApiResponse("Error. You are not the owner of the budget you are trying to delete"),
                    HttpStatus.FORBIDDEN
            );
        }
    // Check if the budget with the given ID exists
        if (!budgetService.existsById(id)) {
            return new ResponseEntity<>(
                    new ApiResponse("Error. Budget to delete not found"),
                    HttpStatus.NOT_FOUND
            );
        }
    // Delete the budget by ID
        budgetService.deleteById(id);
        return new ResponseEntity<>(
                new ApiResponse("Budget deleted successfully"),
                HttpStatus.OK
        );
    }
    //Get ALL budgets of a user
    @GetMapping("/allBudgets")
    public ResponseEntity<Object> getAllBudgets(Principal principal) {
        ExtraUser user = userService.getUserByPrincipal(principal);
        return new ResponseEntity<>(
                budgetService.getAllBudgetsByUserId(user),
                HttpStatus.OK
        );
    }
    //Get a single budget by id
    @GetMapping("/budget/{id}")
    public ResponseEntity<Object> getBudgetById(Principal principal, @PathVariable Long id) {
        ExtraUser user = userService.getUserByPrincipal(principal);
        // Check if the budget with the given ID exists
        if (!budgetService.existsById(id)) {
            return new ResponseEntity<>(
                    new ApiResponse("Error. Budget not found"),
                    HttpStatus.NOT_FOUND
            );
        }
        //Check if the budget belongs to the user
        if (!budgetService.isOwner(user, id)) {
            return new ResponseEntity<>(
                    new ApiResponse("Error. You are not the owner of the budget you are trying to get"),
                    HttpStatus.FORBIDDEN
            );
        }
        return new ResponseEntity<>(
                budgetService.getBudgetById(id),
                HttpStatus.OK
        );
    }

    @PostMapping("/getActiveBudgets")
    public ResponseEntity<Object> getActiveBudgets(Principal principal, @Valid @RequestBody ActiveBudgetRequest request){
        ExtraUser user = userService.getUserByPrincipal(principal);
        BudgetDTO foundBudget = budgetService.getActiveBudgetByCategoryAndDate(user, request.getCategory(), request.getDate());
        return new ResponseEntity<>(
                new ApiResponse("", foundBudget),
                HttpStatus.OK
        );
    }

}
