package org.mojodojocasahouse.extra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.mojodojocasahouse.extra.model.impl.ExtraUser;
import org.springframework.stereotype.Repository;

@Repository
public interface ExtraUserRepository extends JpaRepository<ExtraUser, Long>{
    
}
