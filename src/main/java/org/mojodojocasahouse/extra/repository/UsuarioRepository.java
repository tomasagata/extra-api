package org.mojodojocasahouse.extra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.mojodojocasahouse.extra.model.impl.Usuario;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    
}
