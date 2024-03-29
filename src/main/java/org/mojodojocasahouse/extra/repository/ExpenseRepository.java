package org.mojodojocasahouse.extra.repository;

import java.sql.Date;

import java.util.List;
import java.util.Map;
import java.util.Optional;


import org.mojodojocasahouse.extra.model.Category;
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

    boolean existsByIdAndUser(Long id, ExtraUser user);

    @Query( "SELECT e.category.name AS category, SUM(e.amount) AS amount FROM Expense e " +
            "WHERE e.user = :user " +
                "AND e.category.name IN :categories " +
                "AND e.date BETWEEN :minDate AND :maxDate " +
            "GROUP BY e.category " +
            "ORDER BY SUM(e.amount) DESC")
    List<Map<String, String>> getSumOfExpensesOfUserByCategoryAndDateInterval(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("minDate") Date minDate,
            @Param("maxDate") Date maxDate);

    @Query( "SELECT e.category.name AS category, SUM(e.amount) AS amount FROM Expense e " +
            "WHERE e.user = :user " +
                "AND e.category.name IN :categories " +
            "GROUP BY e.category " +
            "ORDER BY SUM(e.amount) DESC")
    List<Map<String, String>> getSumOfExpensesByCategories(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories);

    @Query( "SELECT e.category.name AS category, SUM(e.amount) AS amount FROM Expense e " +
            "WHERE e.user = :user " +
                "AND e.category.name IN :categories " +
                "AND e.date >= :minDate " +
            "GROUP BY e.category " +
            "ORDER BY SUM(e.amount) DESC")
    List<Map<String, String>> getSumOfExpensesOfUserAfterGivenDate(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("minDate") Date minDate);
    
    @Query( "SELECT e.category.name AS category, SUM(e.amount) AS amount FROM Expense e " +
            "WHERE e.user = :user " +
                "AND e.category.name IN :categories " +
                "AND e.date <= :maxDate " +
            "GROUP BY e.category " +
            "ORDER BY SUM(e.amount) DESC")
    List<Map<String, String>> getSumOfExpensesOfUserBeforeGivenDate(
            @Param("user") ExtraUser user,
            @Param("categories") List<String> categories,
            @Param("maxDate") Date maxDate);

}

