package mdch.login.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import mdch.login.login.model.impl.Usuario;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    
}
