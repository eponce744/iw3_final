package com.iw3.tpfinal.grupoTeyo.model.business.implementations;

import com.iw3.tpfinal.grupoTeyo.auth.model.User;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IUserBusiness;
import com.iw3.tpfinal.grupoTeyo.model.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserBusiness implements IUserBusiness {

    @Autowired
    private UserRepository userDAO;

    @Override
    public List<User> list() throws BusinessException {
        try {
            return userDAO.findAll();
        } catch (Exception e) {
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public User load(long id) throws NotFoundException, BusinessException{
        Optional<User> userFound;
        try {
            userFound = userDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (userFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra el usuario id=" + id).build();
        return userFound.get();
    }

    @Override
    public User load(String user) throws NotFoundException, BusinessException{
        Optional<User> userFound;
        try {
            userFound = userDAO.findByUsername(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (userFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra el usuario " + user).build();
        return userFound.get();
    }

    @Override
    public User add(User user) throws FoundException, BusinessException {
        try {
            load(user.getIdUser());
            throw FoundException.builder().message("Se encuentra el usuario id=" + user.getIdUser()).build();
        }catch (Exception ignored){
        }

        try{
            load(user.getUsername());
            throw FoundException.builder().message("Se encuentra el Usuario " + user.getUsername()).build();
        }catch (NotFoundException ignored){
        }

        try{
            return userDAO.save(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public User update(User user) throws NotFoundException, BusinessException, FoundException{

        User userToUpdate = load(user.getIdUser());
        Optional<User> userFound;

        try{
            userFound = userDAO.findByUsernameAndIdUserNot(user.getUsername(), user.getIdUser());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if(userFound.isPresent()){
            throw FoundException.builder().message("Se encuentra el Usuario " + user.getUsername()).build();
        }

        try{
            user.setPassword(userToUpdate.getPassword());
            return userDAO.save(user);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

    }

    @Override
    public void delete(User user) throws NotFoundException, BusinessException{
        delete(user.getIdUser());
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException{
        User userToDelete = load(id);
        try{
            userDAO.deleteById(id);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

}
