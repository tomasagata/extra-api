package org.mojodojocasahouse.extra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExtraUserRepository extends JpaRepository<ExtraUser,Long> {
    Optional<ExtraUser> findOneByEmailAndPassword(String email, String password);
    Optional<ExtraUser> findByEmail(String email);
}
