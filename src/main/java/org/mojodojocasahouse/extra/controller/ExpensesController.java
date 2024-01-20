package org.mojodojocasahouse.extra.controller;
import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mojodojocasahouse.extra.dto.model.ExpenseDTO;
import org.mojodojocasahouse.extra.dto.requests.ExpenseAddingRequest;
import org.mojodojocasahouse.extra.dto.requests.ExpenseEditingRequest;
import org.mojodojocasahouse.extra.dto.requests.FilteringRequest;
import org.mojodojocasahouse.extra.dto.responses.ApiResponse;
import org.mojodojocasahouse.extra.exception.ExpenseAccessDeniedException;
import org.mojodojocasahouse.extra.exception.ExpenseNotFoundException;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.mojodojocasahouse.extra.service.ExpenseService;
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

        // Check if the expense with the given ID exists
        if (!expenseService.existsById(id)) {
            throw new ExpenseNotFoundException();
        }

        //Check that the user making the edition is the owner of the expense
        if (!expenseService.isOwner(user, id)) {
            throw new ExpenseAccessDeniedException();
        }

        log.debug("Editing expense of user: \"" + user.getEmail() + "\"");

        ApiResponse response = expenseService.editExpense(id, expenseEditingRequest);
        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<ApiResponse> deleteExpense(Principal principal, @PathVariable Long id) {
        ExtraUser user = userService.getUserByPrincipal(principal);

        // Check if the expense with the given ID exists
        if (!expenseService.existsById(id)) {
            throw new ExpenseNotFoundException();
        }

        //Check that the user making the edition is the owner of the expense
        if (!expenseService.isOwner(user, id)) {
            throw new ExpenseAccessDeniedException();
        }

        // Delete the expense by ID
        expenseService.deleteById(id);
        return new ResponseEntity<>(
                new ApiResponse("Expense deleted successfully"),
                HttpStatus.OK
        );
    }

    @PostMapping(path = "/getMyExpenses", produces = "application/json")
    public ResponseEntity<List<ExpenseDTO>> getMyExpenses(
            Principal principal,
            @Valid @RequestBody @Nullable FilteringRequest request){
        ExtraUser user = userService.getUserByPrincipal(principal);
        Date from = null, until = null;
        List<String> categories = new ArrayList<>();

        if(request != null) {
            from = request.getFrom();
            until = request.getUntil();
            categories = request.getCategories();
        }

        log.debug("Retrieving expenses of user: \"" + principal.getName() + "\", " +
                "for categories: " + categories + ", " +
                "from: " + from + ", " +
                "until: " + until + ".");

        List<ExpenseDTO> expenses = expenseService
                .getExpensesOfUserByCategoriesAndDateRanges(user, categories, from, until);

        return ResponseEntity.ok(expenses);
    }

    @PostMapping(path = "/getSumOfExpenses", produces = "application/json")
    public ResponseEntity<List<Map<String, String>>> getExpensesByDateAndCategory(
            Principal principal,
            @Valid @RequestBody @Nullable FilteringRequest request) {
        ExtraUser user = userService.getUserByPrincipal(principal);
        Date from = null, until = null;
        List<String> categories = new ArrayList<>();

        if(request != null) {
            from = request.getFrom();
            until = request.getUntil();
            categories = request.getCategories();
        }

        log.debug("Retrieving sum of expenses of user: \"" + principal.getName() + "\", " +
                "for categories: " + categories + ", " +
                "from: " + from + ", " +
                "until: " + until + ".");

        List<Map<String, String>> categoryAmounts = expenseService
                .getSumOfExpensesOfUserByCategoriesAndDateRanges(user, categories, from, until);

        return ResponseEntity.ok(categoryAmounts);
    }

    @PostMapping(path = "/getYearlySumOfExpenses", produces = "application/json")
    public ResponseEntity<List<Map<String, String>>> getSumOfExpensesByYear(
            Principal principal,
            @Valid @RequestBody @Nullable FilteringRequest request) {
        ExtraUser user = userService.getUserByPrincipal(principal);
        Date from = null, until = null;
        List<String> categories = new ArrayList<>();

        if(request != null) {
            from = request.getFrom();
            until = request.getUntil();
            categories = request.getCategories();
        }

        log.debug("Retrieving yearly sum of expenses of user: \"" + principal.getName() + "\", " +
                "for categories: " + categories + ", " +
                "from: " + from + ", " +
                "until: " + until + ".");

        List<Map<String, String>> categoryAmounts = expenseService
                .getYearlySumOfExpensesOfUserByCategoriesAndDateRanges(user, categories, from, until);

        return ResponseEntity.ok(categoryAmounts);
    }

}

