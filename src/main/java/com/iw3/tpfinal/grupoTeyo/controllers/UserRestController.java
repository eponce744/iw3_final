package com.iw3.tpfinal.grupoTeyo.controllers;


import com.iw3.tpfinal.grupoTeyo.auth.model.User;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IUserBusiness;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping(Constants.URL_USUARIOS)
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UserRestController extends BaseRestController{

    @Autowired
    private IUserBusiness userBusiness;

    @Autowired
    private PasswordEncoder pEncoder;

    @SneakyThrows
    @GetMapping(value="", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> list() {
        List<User> users = userBusiness.list();
        return ResponseEntity.ok(users);
    }

    @SneakyThrows
    @GetMapping(value = "", params = {"page", "size"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("idUser").descending());
        return new ResponseEntity<>(userBusiness.list(pageable), HttpStatus.OK);
    }

    @SneakyThrows
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadUser(@PathVariable Long id){
        return new ResponseEntity<>(userBusiness.load(id), HttpStatus.OK);
    }

    @SneakyThrows
    @GetMapping(value = "/name/{user}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadUser(@PathVariable String user){
        return new ResponseEntity<>(userBusiness.load(user), HttpStatus.OK);
    }

    @SneakyThrows
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> add(@Valid @RequestBody User user){
        user.setPassword(pEncoder.encode(user.getPassword()));
        User response = userBusiness.add(user);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location", Constants.URL_USUARIOS + "/" + response.getIdUser());
        return new ResponseEntity<>(response, responseHeaders, HttpStatus.CREATED);
    }

    @SneakyThrows
    @PutMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@Valid @RequestBody User user){
        userBusiness.update(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @SneakyThrows
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@PathVariable Long id){
        userBusiness.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
