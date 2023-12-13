package org.mojodojocasahouse.extra.repository;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.mojodojocasahouse.extra.model.Budget;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    boolean existsByIdAndUser(Long id, ExtraUser user);

    List<Budget> findAllBudgetsByUser(ExtraUser user);

    @Query( "SELECT b FROM Budget b " +
            "WHERE b.user = :user " +
                "AND b.category = :category " +
                "AND :date BETWEEN b.startingDate AND b.limitDate " +
            "ORDER BY b.limitDate ASC ")
    List<Budget> findActiveBudgetByUserAndCategoryAndDate(
            @Param("user") ExtraUser user,
            @Param("category") String category,
            @Param("date") Date date
    );

    // Find existing budgets that overlap with given date range
    //
    // Added edge validation cases
    // used:  https://www.codespeedy.com/check-if-two-date-ranges-overlap-or-not-in-java/ for reference
    @Query( "SELECT b FROM Budget b " +
            "WHERE b.user = :user " +
                "AND b.category = :category " +
                "AND (" +
                    "   (:startingDate <= b.startingDate AND :limitDate >= b.startingDate) " +
                    "OR (:startingDate <= b.limitDate    AND :limitDate >= b.limitDate   ) " +
                    "OR (:startingDate <= b.startingDate AND :limitDate >= b.limitDate   ) " +
                    "OR (:startingDate >= b.startingDate AND :limitDate <= b.limitDate   ) " +
            ") ORDER BY b.limitDate ASC")
    List<Budget> findBudgetByUserAndCategoryAndStartDateAndEndDate(
            ExtraUser user,
            String category,
            Date startingDate,
            Date limitDate
    );

    @Query("SELECT DISTINCT b.category FROM Budget b WHERE b.user = :user")
    List<String> findAllDistinctCategoriesByUser(ExtraUser user);

    @Query( "SELECT DISTINCT e.category AS category, e.iconId AS iconId " +
            "FROM Budget e " +
            "WHERE e.user = :user")
    List<Map<String, String>> findAllDistinctCategoriesByUserWithIcons(ExtraUser user);
}
