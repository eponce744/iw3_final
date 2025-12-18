package com.iw3.tpfinal.grupoTeyo.model.business.interfaces;

import com.iw3.tpfinal.grupoTeyo.auth.model.User;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;

import java.util.List;

public interface IUserBusiness {
    public List<User> list() throws BusinessException;

    public User load(long id) throws NotFoundException, BusinessException;

    public User load(String user) throws NotFoundException, BusinessException;

    public User add(User user) throws FoundException, BusinessException;

    public User update(User user) throws NotFoundException, BusinessException, FoundException;

    public void delete(User user) throws NotFoundException, BusinessException;

    public void delete(long id) throws NotFoundException, BusinessException;

}
