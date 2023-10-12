package org.mojodojocasahouse.extra.controller;
import java.security.Principal;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mojodojocasahouse.extra.dto.ApiResponse;
import org.mojodojocasahouse.extra.dto.ExpenseAddingRequest;
import org.mojodojocasahouse.extra.dto.ExpenseDTO;
import org.mojodojocasahouse.extra.dto.ExpenseFilteringRequest;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.mojodojocasahouse.extra.service.ExpenseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
        ApiResponse response = expenseService.addExpense(user, expenseAddingRequest);
        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }
    
    @GetMapping(path = "/getMyExpenses", produces = "application/json")
    public ResponseEntity<List<ExpenseDTO>> getMyExpenses(Principal principal){
        ExtraUser user = userService.getUserByPrincipal(principal);

        log.debug("Retrieving all expenses of user: \"" + principal.getName() + "\"");

        List <ExpenseDTO> listOfExpenses = expenseService.getAllExpensesByUserId(user);
    
        return ResponseEntity.ok(listOfExpenses);
    }

    @PostMapping(path = "/getMyExpensesByCategory", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<ExpenseDTO>> getMyExpensesByCategory(Principal principal,
                                                                    @Valid @RequestBody ExpenseFilteringRequest expenseFilteringRequest){
        ExtraUser user = userService.getUserByPrincipal(principal);
        String category = expenseFilteringRequest.getCategory();

        log.debug("Retrieving expenses of user: \"" + principal.getName() + "\" by category: \"" + category + "\"");

        List<ExpenseDTO> listOfExpenses = expenseService.getAllExpensesByCategoryByUserId(user, category);

        return ResponseEntity.ok(listOfExpenses);
    }


    @GetMapping(path = "/getAllCategories", produces = "application/json")
    public ResponseEntity<List<String>> getMyCategories (Principal principal){
        ExtraUser user = userService.getUserByPrincipal(principal);
        return ResponseEntity.ok(expenseService.getAllCategories(user));
    }

}

