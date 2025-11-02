package com.iw3.tpfinal.grupoTeyo.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iw3.tpfinal.grupoTeyo.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByRazonSocial(String razonSocial);

    Optional<Cliente> findByRazonSocialAndIdNot(String razonSocial, long id);
}
