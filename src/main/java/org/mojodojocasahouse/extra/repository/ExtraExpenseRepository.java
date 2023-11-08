package org.mojodojocasahouse.extra.repository;

import java.math.BigDecimal;
import java.sql.Date;

import java.util.List;
import java.util.Map;
import java.util.Optional;


import org.mojodojocasahouse.extra.model.ExtraExpense;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExtraExpenseRepository extends JpaRepository<ExtraExpense, Long>{
    List<ExtraExpense> findAllExpensesByUser(@Param("userId") ExtraUser user);

    Optional<ExtraExpense> findFirstByConcept(String string);

    List<ExtraExpense> findAllExpensesByUserAndCategory(ExtraUser user, String category);

    @Query("SELECT DISTINCT e.category FROM ExtraExpense e WHERE e.user = :userId")
    List<String> findAllDistinctCategoriesByUser(@Param("userId") ExtraUser user);

    Optional<ExtraExpense> findByCategory(String category);

    boolean existsByIdAndUser(Long id, ExtraUser user);

    @Query( "SELECT e.category AS category, SUM(e.amount) AS amount FROM ExtraExpense e " +
            "WHERE e.user = :user " +
                "AND e.category IN :categories " +
                "AND e.date BETWEEN :minDate AND :maxDate " +
            "GROUP BY e.category")
    List<Map<String, BigDecimal>> getSumOfExpensesOfUserByCategoryAndDateInterval(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("minDate") Date minDate,
            @Param("maxDate") Date maxDate);

    @Query( "SELECT e.category AS category, SUM(e.amount) AS amount FROM ExtraExpense e " +
            "WHERE e.user = :user " +
                "AND e.category IN :categories " +
            "GROUP BY e.category")
    List<Map<String, BigDecimal>> getSumOfExpensesByCategories(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories);

    @Query( "SELECT e.category AS category, SUM(e.amount) AS amount FROM ExtraExpense e " +
            "WHERE e.user = :user " +
                "AND e.category IN :categories " +
                "AND e.date >= :minDate " +
            "GROUP BY e.category")
    List<Map<String, BigDecimal>> getSumOfExpensesOfUserAfterGivenDate(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("minDate") Date minDate);
    
    @Query( "SELECT e.category AS category, SUM(e.amount) AS amount FROM ExtraExpense e " +
            "WHERE e.user = :user " +
                "AND e.category IN :categories " +
                "AND e.date <= :maxDate " +
            "GROUP BY e.category")
    List<Map<String, BigDecimal>> getSumOfExpensesOfUserBeforeGivenDate(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("maxDate") Date maxDate);

    @Query( "SELECT e FROM ExtraExpense e " +
            "WHERE e.user = :user " +
                "AND e.category IN :categories " +
                "AND e.date BETWEEN :minDate AND :maxDate")
    List<ExtraExpense> getExpensesOfUserByCategoriesAndDateInterval(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("minDate") Date minDate,
            @Param("maxDate") Date maxDate);

    @Query( "SELECT e FROM ExtraExpense e " +
            "WHERE e.user = :user " +
                "AND e.category IN :categories")
    List<ExtraExpense> getExpensesOfUserByCategory(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories);

    @Query( "SELECT e FROM ExtraExpense e " +
            "WHERE e.user = :user " +
                "AND e.category IN :categories " +
                "AND e.date >= :minDate")
    List<ExtraExpense> getExpensesOfUserAfterGivenDate(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("minDate") Date minDate);

    @Query( "SELECT e FROM ExtraExpense e " +
            "WHERE e.user = :user " +
                "AND e.category IN :categories " +
                "AND e.date <= :maxDate")
    List<ExtraExpense> getExpensesOfUserBeforeGivenDate(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("maxDate") Date maxDate);
}

