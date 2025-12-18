package com.iw3.tpfinal.grupoTeyo.auth.model.persistence;

import java.util.Optional;

import com.iw3.tpfinal.grupoTeyo.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthRepository extends JpaRepository<User, Long>{
	public Optional<User> findOneByUsernameOrEmail(String username, String email);
}
