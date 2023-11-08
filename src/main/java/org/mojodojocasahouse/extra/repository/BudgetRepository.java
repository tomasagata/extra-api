package org.mojodojocasahouse.extra.repository;
import java.sql.Date;
import java.util.List;

import org.mojodojocasahouse.extra.model.ExtraBudget;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<ExtraBudget, Long> {

    boolean existsByIdAndUser(Long id, ExtraUser user);

    List<ExtraBudget> findAllBudgetsByUser(ExtraUser user);

    @Query(value = "SELECT * FROM BUDGETS " +
           "WHERE user_id = :userId " +
           "AND category = :category " +
           "AND limitdate > :today " +
           "ORDER BY limitdate ASC", nativeQuery = true)
    ExtraBudget findActiveBudgetByUserAndCategory(
        @Param("userId") ExtraUser userId,
        @Param("today") Date today,
        @Param("category") String category
    );

}
