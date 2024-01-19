package org.mojodojocasahouse.extra.controller;
import java.security.Principal;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mojodojocasahouse.extra.dto.model.ExpenseDTO;
import org.mojodojocasahouse.extra.dto.requests.ExpenseAddingRequest;
import org.mojodojocasahouse.extra.dto.requests.ExpenseEditingRequest;
import org.mojodojocasahouse.extra.dto.responses.ApiResponse;
import org.mojodojocasahouse.extra.exception.ExpenseAccessDeniedException;
import org.mojodojocasahouse.extra.exception.ExpenseNotFoundException;
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

    @GetMapping(path = "/getSumOfExpenses", produces = "application/json")
    public ResponseEntity<List<Map<String, String>>> getExpensesByDateAndCategory(
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

        List<Map<String, String>> categoryAmounts = expenseService
                .getSumOfExpensesOfUserByCategoriesAndDateRanges(user, categories, min_date, max_date);

        return ResponseEntity.ok(categoryAmounts);
    }

    @GetMapping(path = "/getYearlySumOfExpenses", produces = "application/json")
    public ResponseEntity<List<Map<String, String>>> getSumOfExpensesByYear(
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

        log.debug("Retrieving yearly sum of expenses of user: \"" + principal.getName() + "\", " +
                "for categories: " + categories + ", " +
                "from: " + from + ", " +
                "until: " + until + ".");

        List<Map<String, String>> categoryAmounts = expenseService
                .getYearlySumOfExpensesOfUserByCategoriesAndDateRanges(user, categories, min_date, max_date);

        return ResponseEntity.ok(categoryAmounts);
    }

}

