package org.mojodojocasahouse.extra.repository;
import java.util.List;
import java.util.Optional;

import org.mojodojocasahouse.extra.model.ExtraExpense;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExtraExpenseRepository extends JpaRepository<ExtraExpense, Long>{
//    @Query("SELECT e FROM ExtraExpense e WHERE e.userId = :userId")
    List<ExtraExpense> findAllExpensesByUserId(@Param("userId") ExtraUser user);

    Optional<ExtraExpense> findByConcept(String string);

}
