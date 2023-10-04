package org.mojodojocasahouse.extra.controller;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.mojodojocasahouse.extra.dto.ApiResponse;
import org.mojodojocasahouse.extra.dto.ExpenseAddingRequest;
import org.mojodojocasahouse.extra.dto.ExpenseDTO;
import org.mojodojocasahouse.extra.model.ExtraExpense;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.service.AuthenticationService;
import org.mojodojocasahouse.extra.service.ExpenseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class ExpensesController {

    private final AuthenticationService userService;
    private final ExpenseService expenseService;

    public ExpensesController(AuthenticationService userService, ExpenseService expenseService) {
        this.userService = userService;
        this.expenseService = expenseService;
    }


    @PostMapping(value = "/addExpense" , consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> addExpense(@CookieValue("JSESSIONID") UUID cookie, @Valid @RequestBody ExpenseAddingRequest expenseAddingRequest){
        userService.validateAuthentication(cookie);
        ExtraUser idUser = userService.getUserBySessionToken(cookie);
        ApiResponse response = expenseService.addExpense(idUser, expenseAddingRequest);
        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }
    
    @GetMapping(path = "/getMyExpenses", produces = "application/json")
    public ResponseEntity<List<ExpenseDTO>> getMyExpenses(@CookieValue("JSESSIONID") UUID cookie){
        userService.validateAuthentication(cookie);
        ExtraUser user = userService.getUserBySessionToken(cookie);

        List <ExtraExpense> listOfExpenses = expenseService.getAllExpensesByUserId(user);
        // Convert ExtraExpense entities to ExpenseDTO
        List<ExpenseDTO> expenseDTOs = new ArrayList<>();
        for (ExtraExpense expense : listOfExpenses) {
            ExpenseDTO expenseDTO = new ExpenseDTO(null, null, null, null, null);
            expenseDTO.setId(expense.getId());
            expenseDTO.setUserId(expense.getUserId().getId());
            expenseDTO.setConcept(expense.getConcept());
            expenseDTO.setAmount(expense.getAmount());
            expenseDTO.setDate(expense.getDate());
            expenseDTOs.add(expenseDTO);
        }
    
        return ResponseEntity.ok(expenseDTOs);
    }
}

