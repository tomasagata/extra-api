package org.mojodojocasahouse.extra.repository;
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

    @Query( "SELECT b FROM ExtraBudget b " +
            "WHERE b.user = :user " +
                "AND b.category = :category " +
                "AND CURRENT DATE BETWEEN b.startingDate AND b.limitDate " +
            "ORDER BY b.limitDate ASC")
    ExtraBudget findActiveBudgetByUserAndCategory(
        @Param("user") ExtraUser user,
        @Param("category") String category
    );

}
