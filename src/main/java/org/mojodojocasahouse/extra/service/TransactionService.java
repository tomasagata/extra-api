package org.mojodojocasahouse.extra.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mojodojocasahouse.extra.dto.model.ExpenseDTO;
import org.mojodojocasahouse.extra.dto.model.TransactionDTO;
import org.mojodojocasahouse.extra.model.Expense;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.model.Transaction;
import org.mojodojocasahouse.extra.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final CategoryService categoryService;

    private final TransactionRepository transactionRepository;

    public List<TransactionDTO> getTransactionsOfUserByCategoriesAndDateRanges(ExtraUser user,
                                                                               List<String> categories,
                                                                               Date from, Date until) {
        List<String> filteringCategories = categories;

        if(filteringCategories == null || filteringCategories.isEmpty()){
            filteringCategories = categoryService.getAllCategoryNamesOfUser(user);
        }

        if (from == null && until == null){
            return transactionRepository
                    .getTransactionsOfUserByCategory(user, filteringCategories)
                    .stream()
                    .map(Transaction::asDto)
                    .collect(Collectors.toList());
        } else if (from == null) {
            return transactionRepository
                    .getTransactionsOfUserBeforeGivenDate(user, filteringCategories, until)
                    .stream()
                    .map(Transaction::asDto)
                    .collect(Collectors.toList());
        } else if (until == null) {
            return transactionRepository
                    .getTransactionsOfUserAfterGivenDate(user, filteringCategories, from)
                    .stream()
                    .map(Transaction::asDto)
                    .collect(Collectors.toList());
        }

        return transactionRepository
                .getTransactionsOfUserByCategoriesAndDateInterval(user, filteringCategories, from, until)
                .stream()
                .map(Transaction::asDto)
                .collect(Collectors.toList());
    }

    public List<Map<String, String>> getYearlySumOfTransactionsOfUserByCategoriesAndDateRanges(ExtraUser user,
                                                                                           List<String> categories,
                                                                                           Date from,
                                                                                           Date until) {
        List<String> filteringCategories = categories;

        if(filteringCategories == null || filteringCategories.isEmpty()){
            filteringCategories = categoryService.getAllCategoryNamesOfUser(user);
        }

        if (from == null && until == null){
            return transactionRepository
                    .getYearlySumOfTransactionsByCategories(user, filteringCategories);
        } else if (from == null) {
            return transactionRepository
                    .getYearlySumOfTransactionsOfUserBeforeGivenDate(user, filteringCategories, until);
        } else if (until == null) {
            return transactionRepository
                    .getYearlySumOfTransactionsOfUserAfterGivenDate(user, filteringCategories, from);
        }

        return transactionRepository
                .getYearlySumOfTransactionsOfUserByCategoryAndDateInterval(user, filteringCategories, from, until);

    }

}
