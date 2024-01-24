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

    @Query( "SELECT e.category AS category, SUM(e.amount) AS amount FROM Expense e " +
            "WHERE e.user = :user " +
            "AND e.category IN :categories " +
            "AND e.date BETWEEN :minDate AND :maxDate " +
            "GROUP BY e.category " +
            "ORDER BY SUM(e.amount) DESC")
    List<Map<String, String>> getSumOfTransactionsOfUserByCategoryAndDateInterval(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("minDate") Date minDate,
            @Param("maxDate") Date maxDate);

    @Query( "SELECT e.category AS category, SUM(e.amount) AS amount FROM Expense e " +
            "WHERE e.user = :user " +
            "AND e.category IN :categories " +
            "GROUP BY e.category " +
            "ORDER BY SUM(e.amount) DESC")
    List<Map<String, String>> getSumOfTransactionsByCategories(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories);

    @Query( "SELECT e.category AS category, SUM(e.amount) AS amount FROM Expense e " +
            "WHERE e.user = :user " +
            "AND e.category IN :categories " +
            "AND e.date >= :minDate " +
            "GROUP BY e.category " +
            "ORDER BY SUM(e.amount) DESC")
    List<Map<String, String>> getSumOfTransactionsOfUserAfterGivenDate(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("minDate") Date minDate);

    @Query( "SELECT e.category AS category, SUM(e.amount) AS amount FROM Expense e " +
            "WHERE e.user = :user " +
            "AND e.category IN :categories " +
            "AND e.date <= :maxDate " +
            "GROUP BY e.category " +
            "ORDER BY SUM(e.amount) DESC")
    List<Map<String, String>> getSumOfTransactionsOfUserBeforeGivenDate(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("maxDate") Date maxDate);

    @Query( "SELECT YEAR(e.date) AS year, SUM(e.amount) AS amount FROM Expense e " +
            "WHERE e.user = :user " +
            "AND e.category IN :categories " +
            "AND e.date BETWEEN :minDate AND :maxDate " +
            "GROUP BY YEAR(e.date) " +
            "ORDER BY YEAR(e.date) ASC")
    List<Map<String, String>> getYearlySumOfTransactionsOfUserByCategoryAndDateInterval(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("minDate") Date minDate,
            @Param("maxDate") Date maxDate);

    @Query( "SELECT YEAR(e.date) AS year, SUM(e.amount) AS amount FROM Expense e " +
            "WHERE e.user = :user " +
            "AND e.category IN :categories " +
            "GROUP BY YEAR(e.date) " +
            "ORDER BY YEAR(e.date) ASC")
    List<Map<String, String>> getYearlySumOfTransactionsByCategories(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories);

    @Query( "SELECT YEAR(e.date) AS year, SUM(e.amount) AS amount FROM Expense e " +
            "WHERE e.user = :user " +
            "AND e.category IN :categories " +
            "AND e.date >= :minDate " +
            "GROUP BY YEAR(e.date) " +
            "ORDER BY YEAR(e.date) ASC")
    List<Map<String, String>> getYearlySumOfTransactionsOfUserAfterGivenDate(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("minDate") Date minDate);

    @Query( "SELECT YEAR(e.date) AS year, SUM(e.amount) AS amount FROM Expense e " +
            "WHERE e.user = :user " +
            "AND e.category IN :categories " +
            "AND e.date <= :maxDate " +
            "GROUP BY YEAR(e.date) " +
            "ORDER BY YEAR(e.date) ASC")
    List<Map<String, String>> getYearlySumOfTransactionsOfUserBeforeGivenDate(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("maxDate") Date maxDate);

    @Query( "SELECT e FROM Expense e " +
            "WHERE e.user = :user " +
            "AND e.category IN :categories " +
            "AND e.date BETWEEN :minDate AND :maxDate")
    List<Expense> getTransactionsOfUserByCategoriesAndDateInterval(
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


    @Query( "SELECT e FROM Expense e " +
            "WHERE e.user = :user " +
            "AND e.category IN :categories")
    List<Expense> getTransactionsOfUserByCategory(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories);

    @Query( "SELECT e FROM Expense e " +
            "WHERE e.user = :user " +
            "AND e.category IN :categories " +
            "AND e.date >= :minDate")
    List<Expense> getTransactionsOfUserAfterGivenDate(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("minDate") Date minDate);

    @Query( "SELECT e FROM Expense e " +
            "WHERE e.user = :user " +
            "AND e.category IN :categories " +
            "AND e.date <= :maxDate")
    List<Expense> getTransactionsOfUserBeforeGivenDate(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("maxDate") Date maxDate);

}
