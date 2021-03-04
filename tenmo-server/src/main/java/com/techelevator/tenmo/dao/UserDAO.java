package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferFunds;
import com.techelevator.tenmo.model.User;
import java.math.BigDecimal;
import java.util.List;


public interface UserDAO {

    List<User> findAll();

    User findByUsername(String username);

    int findIdByUsername(String username);

    boolean create(String username, String password);
    
    BigDecimal getBalance(int id);
    
    List<User> getUsers(int id);
    
    boolean transfer(TransferFunds transferFunds);
    
    List<TransferFunds> getUserHistory(int id);
}
