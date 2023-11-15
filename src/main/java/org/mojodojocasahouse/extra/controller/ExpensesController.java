package org.mojodojocasahouse.extra.controller;
import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mojodojocasahouse.extra.dto.*;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.mojodojocasahouse.extra.service.ExpenseService;
import org.mojodojocasahouse.extra.validation.constraint.ValidCategory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ExpensesController {

    private final AuthenticationService userService;

    private final ExpenseService expenseService;


    @PostMapping(value = "/addExpense", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> addExpense(Principal principal,
                                             @Valid @RequestBody ExpenseAddingRequest expenseAddingRequest){
        ExtraUser user = userService.getUserByPrincipal(principal);
        log.debug("Adding expense to user: \"" + user.getEmail() + "\"");

        //Add expense amount to current amount of budget if budget is active
        
        ApiResponse response = expenseService.addExpense(user, expenseAddingRequest);
        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @PostMapping(value = "/editExpense/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> editExpense(Principal principal,
                                              @Valid @RequestBody ExpenseEditingRequest expenseEditingRequest, @PathVariable Long id){
        ExtraUser user = userService.getUserByPrincipal(principal);
            //Check that the user making the edition is the owner of the expense
        if (!expenseService.isOwner(user, id)) {
            return new ResponseEntity<>(
                    new ApiResponse("Error. You are not the owner of the expense you are trying to edit"),
                    HttpStatus.FORBIDDEN
            );
        }
    // Check if the expense with the given ID exists
        if (!expenseService.existsById(id)) {
            return new ResponseEntity<>(
                    new ApiResponse("Error. Expense to edit not found"),
                    HttpStatus.NOT_FOUND
            );
        }

        log.debug("Editing expense of user: \"" + user.getEmail() + "\"");

        ApiResponse response = expenseService.editExpense(user, id,expenseEditingRequest);
        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<ApiResponse> deleteExpense(Principal principal, @PathVariable Long id) {
        ExtraUser user = userService.getUserByPrincipal(principal);
    //Check that the user making the deletion is the owner of the expense
        if (!expenseService.isOwner(user, id)) {
            return new ResponseEntity<>(
                    new ApiResponse("Error. You are not the owner of the expense you are trying to delete"),
                    HttpStatus.FORBIDDEN
            );
        }
    // Check if the expense with the given ID exists
        if (!expenseService.existsById(id)) {
            return new ResponseEntity<>(
                    new ApiResponse("Error. Expense to delete not found"),
                    HttpStatus.NOT_FOUND
            );
        }
    // Delete the expense by ID
        expenseService.deleteById(id);
        return new ResponseEntity<>(
                new ApiResponse("Expense deleted successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/getMyExpenses", produces = "application/json")
    public ResponseEntity<List<ExpenseDTO>> getMyExpenses(
            Principal principal,
            @RequestParam(required = false) List<@ValidCategory String> categories,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String until){
        ExtraUser user = userService.getUserByPrincipal(principal);
        Date min_date;
        Date max_date;

        if(from != null && !from.isEmpty()) {
            min_date = Date.valueOf(from);
        } else {
            min_date = null;
        }

        if(until != null && !until.isEmpty()) {
            max_date = Date.valueOf(until);
        } else {
            max_date = null;
        }

        log.debug("Retrieving expenses of user: \"" + principal.getName() + "\", " +
                "for categories: " + categories + ", " +
                "from: " + from + ", " +
                "until: " + until + ".");

        List<ExpenseDTO> expenses = expenseService
                .getExpensesOfUserByCategoriesAndDateRanges(user, categories, min_date, max_date);

        return ResponseEntity.ok(expenses);
    }


    @GetMapping(path = "/getAllCategories", produces = "application/json")
    public ResponseEntity<List<String>> getMyCategories (Principal principal){
        ExtraUser user = userService.getUserByPrincipal(principal);

        log.debug("Retrieving all expenses of user: \"" + principal.getName() + "\"");

        return ResponseEntity.ok(expenseService.getAllCategories(user));
    }

    @GetMapping(path = "/getAllCategoriesWithIcons", produces = "application/json")
    public ResponseEntity<List<Map<String, String>>> getMyCategoriesWithIcons (Principal principal){
        ExtraUser user = userService.getUserByPrincipal(principal);

        log.debug("Retrieving all expenses of user: \"" + principal.getName() + "\"");

        return ResponseEntity.ok(expenseService.getAllCategoriesWithIcons(user));
    }

    @GetMapping(path = "/getSumOfExpenses", produces = "application/json")
    public ResponseEntity<List<Map<String, BigDecimal>>> getExpensesByDateAndCategory(
            Principal principal,
            @RequestParam(required = false) List<@ValidCategory String> categories,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String until) {
        ExtraUser user = userService.getUserByPrincipal(principal);
        Date min_date;
        Date max_date;

        if(from != null && !from.isEmpty()) {
            min_date = Date.valueOf(from);
        } else {
            min_date = null;
        }

        if(until != null && !until.isEmpty()) {
            max_date = Date.valueOf(until);
        } else {
            max_date = null;
        }

        log.debug("Retrieving sum of expenses of user: \"" + principal.getName() + "\", " +
                "for categories: " + categories + ", " +
                "from: " + from + ", " +
                "until: " + until + ".");

        List<Map<String, BigDecimal>> categoryAmounts = expenseService
                .getSumOfExpensesOfUserByCategoriesAndDateRanges(user, categories, min_date, max_date);

        return ResponseEntity.ok(categoryAmounts);
    }

}

