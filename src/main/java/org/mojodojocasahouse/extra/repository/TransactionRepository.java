package org.mojodojocasahouse.extra.repository;

import org.mojodojocasahouse.extra.model.Category;
import org.mojodojocasahouse.extra.model.Expense;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query( "SELECT " +
                "YEAR(t.date) AS year, " +
                "SUM(t.signedAmount) AS amount " +
            "FROM Transaction t " +
                "WHERE t.user = :user " +
                "AND t.category.name IN :categories " +
            "AND t.date BETWEEN :minDate AND :maxDate " +
            "GROUP BY YEAR(t.date) " +
            "ORDER BY YEAR(t.date) ASC")
    List<Map<String, String>> getYearlySumOfTransactionsOfUserByCategoryAndDateInterval(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("minDate") Date minDate,
            @Param("maxDate") Date maxDate);

    @Query( "SELECT YEAR(t.date) AS year, SUM(t.signedAmount) AS amount FROM Transaction t " +
            "WHERE t.user = :user " +
            "AND t.category.name IN :categories " +
            "GROUP BY YEAR(t.date) " +
            "ORDER BY YEAR(t.date) ASC")
    List<Map<String, String>> getYearlySumOfTransactionsByCategories(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories);

    @Query( "SELECT YEAR(t.date) AS year, SUM(t.signedAmount) AS amount FROM Transaction t " +
            "WHERE t.user = :user " +
            "AND t.category.name IN :categories " +
            "AND t.date >= :minDate " +
            "GROUP BY YEAR(t.date) " +
            "ORDER BY YEAR(t.date) ASC")
    List<Map<String, String>> getYearlySumOfTransactionsOfUserAfterGivenDate(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("minDate") Date minDate);

    @Query( "SELECT YEAR(t.date) AS year, SUM(t.signedAmount) AS amount FROM Transaction t " +
            "WHERE t.user = :user " +
            "AND t.category.name IN :categories " +
            "AND t.date <= :maxDate " +
            "GROUP BY YEAR(t.date) " +
            "ORDER BY YEAR(t.date) ASC")
    List<Map<String, String>> getYearlySumOfTransactionsOfUserBeforeGivenDate(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("maxDate") Date maxDate);

    @Query( "SELECT t FROM Transaction t " +
            "WHERE t.user = :user " +
            "AND t.category.name IN :categories " +
            "AND t.date BETWEEN :minDate AND :maxDate")
    List<Transaction> getTransactionsOfUserByCategoriesAndDateInterval(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("minDate") Date minDate,
            @Param("maxDate") Date maxDate);

    @Query( "SELECT t FROM Transaction t " +
            "WHERE t.user = :user " +
            "AND t.category = :category " +
            "AND t.date BETWEEN :minDate AND :maxDate")
    List<Transaction> getTransactionsByUserAndCategoryAndDateInterval(
            @Param("user") ExtraUser user,
            @Param("category") Category category,
            @Param("minDate") Date minDate,
            @Param("maxDate") Date maxDate
    );


    @Query( "SELECT t FROM Transaction t " +
            "WHERE t.user = :user " +
            "AND t.category.name IN :categories")
    List<Transaction> getTransactionsOfUserByCategory(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories);

    @Query( "SELECT t FROM Transaction t " +
            "WHERE t.user = :user " +
            "AND t.category.name IN :categories " +
            "AND t.date >= :minDate")
    List<Transaction> getTransactionsOfUserAfterGivenDate(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("minDate") Date minDate);

    @Query( "SELECT t FROM Expense t " +
            "WHERE t.user = :user " +
            "AND t.category.name IN :categories " +
            "AND t.date <= :maxDate")
    List<Transaction> getTransactionsOfUserBeforeGivenDate(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("maxDate") Date maxDate);

}
