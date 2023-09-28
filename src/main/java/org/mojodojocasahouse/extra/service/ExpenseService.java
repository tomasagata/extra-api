package org.mojodojocasahouse.extra.service;

import java.util.List;

import org.mojodojocasahouse.extra.dto.ApiResponse;
import org.mojodojocasahouse.extra.dto.ExpenseAddingRequest;
import org.mojodojocasahouse.extra.model.ExtraExpense;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.repository.ExtraExpenseRepository;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;

@Service
public class ExpenseService {
    private final ExtraExpenseRepository expenseRepository;
    public ExpenseService(ExtraExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public ApiResponse addExpense(ExtraUser user, ExpenseAddingRequest expenseAddingRequest) {
        //create expense entity from request data
        ExtraExpense newExpense = ExtraExpense.from(expenseAddingRequest, user);

        //Save new expense
        expenseRepository.save(newExpense);
        return new ApiResponse("Expense added succesfully!");
    }
    //get all expenses of the user that is making the request
    public List<ExtraExpense> getAllExpensesByUserId(ExtraUser user) {
        return expenseRepository.findAllExpensesByUserId(user);
    }
}
