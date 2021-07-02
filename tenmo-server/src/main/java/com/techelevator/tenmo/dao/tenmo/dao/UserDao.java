package com.techelevator.tenmo.dao.tenmo.dao;

import com.techelevator.tenmo.dao.tenmo.model.User;

import java.util.List;

public interface UserDao {

    List<User> findAll();

    User findByUsername(String username);

    int findIdByUsername(String username);

    boolean create(String username, String password);

    List<User> listSendUsers(Long userId);
}
