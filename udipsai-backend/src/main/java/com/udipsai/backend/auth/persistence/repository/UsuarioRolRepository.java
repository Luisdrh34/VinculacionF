package com.udipsai.backend.auth.persistence.repository;

import com.udipsai.backend.usuarios.persistence.entity.UsuarioRolEntity;
import com.udipsai.backend.usuarios.persistence.entity.UsuarioRolId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRolEntity, UsuarioRolId> {

}
