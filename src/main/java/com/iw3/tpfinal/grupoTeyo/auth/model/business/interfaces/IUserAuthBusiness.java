package com.iw3.tpfinal.grupoTeyo.auth.model.business.interfaces;

import java.util.List;

import com.iw3.tpfinal.grupoTeyo.auth.model.User;
import com.iw3.tpfinal.grupoTeyo.auth.model.business.exceptions.BadPasswordException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;

public interface IUserAuthBusiness {
	public User load(String usernameOrEmail) throws NotFoundException, BusinessException;

	public void changePassword(String usernameOrEmail, String oldPassword, String newPassword, PasswordEncoder pEncoder)
			throws BadPasswordException, NotFoundException, BusinessException;

	public void disable(String usernameOrEmail) throws NotFoundException, BusinessException;

	public void enable(String usernameOrEmail) throws NotFoundException, BusinessException;
	
	public List<User> list() throws BusinessException;

}
