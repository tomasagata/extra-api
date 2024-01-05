package org.mojodojocasahouse.extra.repository;

import java.sql.Date;

import java.util.List;
import java.util.Map;
import java.util.Optional;


import org.mojodojocasahouse.extra.dto.CategoryWithIconDTO;
import org.mojodojocasahouse.extra.model.Expense;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>{
    List<Expense> findAllExpensesByUser(@Param("userId") ExtraUser user);

    Optional<Expense> findFirstByConcept(String string);

    List<Expense> findAllExpensesByUserAndCategory(ExtraUser user, String category);

    @Query("SELECT DISTINCT e.category FROM Expense e WHERE e.user = :userId")
    List<String> findAllDistinctCategoriesByUser(@Param("userId") ExtraUser user);

    Optional<Expense> findByCategory(String category);

    boolean existsByIdAndUser(Long id, ExtraUser user);

    @Query( "SELECT e.category AS category, SUM(e.amount) AS amount FROM Expense e " +
            "WHERE e.user = :user " +
                "AND e.category IN :categories " +
                "AND e.date BETWEEN :minDate AND :maxDate " +
            "GROUP BY e.category " +
            "ORDER BY SUM(e.amount) DESC")
    List<Map<String, String>> getSumOfExpensesOfUserByCategoryAndDateInterval(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("minDate") Date minDate,
            @Param("maxDate") Date maxDate);

    @Query( "SELECT e.category AS category, SUM(e.amount) AS amount FROM Expense e " +
            "WHERE e.user = :user " +
                "AND e.category IN :categories " +
            "GROUP BY e.category " +
            "ORDER BY SUM(e.amount) DESC")
    List<Map<String, String>> getSumOfExpensesByCategories(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories);

    @Query( "SELECT e.category AS category, SUM(e.amount) AS amount FROM Expense e " +
            "WHERE e.user = :user " +
                "AND e.category IN :categories " +
                "AND e.date >= :minDate " +
            "GROUP BY e.category " +
            "ORDER BY SUM(e.amount) DESC")
    List<Map<String, String>> getSumOfExpensesOfUserAfterGivenDate(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("minDate") Date minDate);
    
    @Query( "SELECT e.category AS category, SUM(e.amount) AS amount FROM Expense e " +
            "WHERE e.user = :user " +
                "AND e.category IN :categories " +
                "AND e.date <= :maxDate " +
            "GROUP BY e.category " +
            "ORDER BY SUM(e.amount) DESC")
    List<Map<String, String>> getSumOfExpensesOfUserBeforeGivenDate(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("maxDate") Date maxDate);

    @Query( "SELECT YEAR(e.date) AS year, SUM(e.amount) AS amount FROM Expense e " +
            "WHERE e.user = :user " +
            "AND e.category IN :categories " +
            "AND e.date BETWEEN :minDate AND :maxDate " +
            "GROUP BY YEAR(e.date) " +
            "ORDER BY YEAR(e.date) ASC")
    List<Map<String, String>> getYearlySumOfExpensesOfUserByCategoryAndDateInterval(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("minDate") Date minDate,
            @Param("maxDate") Date maxDate);

    @Query( "SELECT YEAR(e.date) AS year, SUM(e.amount) AS amount FROM Expense e " +
            "WHERE e.user = :user " +
            "AND e.category IN :categories " +
            "GROUP BY YEAR(e.date) " +
            "ORDER BY YEAR(e.date) ASC")
    List<Map<String, String>> getYearlySumOfExpensesByCategories(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories);

    @Query( "SELECT YEAR(e.date) AS year, SUM(e.amount) AS amount FROM Expense e " +
            "WHERE e.user = :user " +
            "AND e.category IN :categories " +
            "AND e.date >= :minDate " +
            "GROUP BY YEAR(e.date) " +
            "ORDER BY YEAR(e.date) ASC")
    List<Map<String, String>> getYearlySumOfExpensesOfUserAfterGivenDate(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("minDate") Date minDate);

    @Query( "SELECT YEAR(e.date) AS year, SUM(e.amount) AS amount FROM Expense e " +
            "WHERE e.user = :user " +
            "AND e.category IN :categories " +
            "AND e.date <= :maxDate " +
            "GROUP BY YEAR(e.date) " +
            "ORDER BY YEAR(e.date) ASC")
    List<Map<String, String>> getYearlySumOfExpensesOfUserBeforeGivenDate(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("maxDate") Date maxDate);

    @Query( "SELECT e FROM Expense e " +
            "WHERE e.user = :user " +
                "AND e.category IN :categories " +
                "AND e.date BETWEEN :minDate AND :maxDate")
    List<Expense> getExpensesOfUserByCategoriesAndDateInterval(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("minDate") Date minDate,
            @Param("maxDate") Date maxDate);

    @Query( "SELECT e FROM Expense e " +
            "WHERE e.user = :user " +
                "AND e.category IN :categories")
    List<Expense> getExpensesOfUserByCategory(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories);

    @Query( "SELECT e FROM Expense e " +
            "WHERE e.user = :user " +
                "AND e.category IN :categories " +
                "AND e.date >= :minDate")
    List<Expense> getExpensesOfUserAfterGivenDate(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("minDate") Date minDate);

    @Query( "SELECT e FROM Expense e " +
            "WHERE e.user = :user " +
                "AND e.category IN :categories " +
                "AND e.date <= :maxDate")
    List<Expense> getExpensesOfUserBeforeGivenDate(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("maxDate") Date maxDate);

    @Query( "SELECT NEW org.mojodojocasahouse.extra.dto.CategoryWithIconDTO(UPPER(e.category), e.iconId) " +
            "FROM Expense e " +
            "WHERE e.user = :user")
    List<CategoryWithIconDTO> findAllDistinctCategoriesByUserWithIcons(ExtraUser user);

}

