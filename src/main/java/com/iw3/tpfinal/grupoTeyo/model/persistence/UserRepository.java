package com.iw3.tpfinal.grupoTeyo.model.persistence;

import com.iw3.tpfinal.grupoTeyo.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndIdUserNot(String username, long id);
}
