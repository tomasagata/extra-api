package org.mojodojocasahouse.extra.repository;

import org.mojodojocasahouse.extra.model.SessionToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SessionTokenRepository extends JpaRepository<SessionToken, UUID> {

}
