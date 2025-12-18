package com.iw3.tpfinal.grupoTeyo.auth.model.persistence;

import com.iw3.tpfinal.grupoTeyo.auth.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleAuthRepository extends JpaRepository<Role, Integer>{
}

