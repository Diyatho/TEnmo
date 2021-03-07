package com.techelevator.tenmo.controller;


import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.TransactionHistory;
import com.techelevator.tenmo.model.TransferFunds;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserAction;

@RequestMapping("/tenmo")
@RestController
public class TenmoTransactionsController {
	
	  private UserDAO userDAO;
	  
	  public TenmoTransactionsController(UserDAO userDAO) {
	        this.userDAO = userDAO;
	  }
	  @RequestMapping( path = "/{id}" + "/balance", method = RequestMethod.GET)
	  public BigDecimal getBalance(@PathVariable int id) {
		  return userDAO.getBalance(id);
		  
	  }
	  @RequestMapping( path = "/{id}" + "/getOtherUsers", method = RequestMethod.GET)
	  public List<User> userList(@PathVariable int id)   {
		  return userDAO.getUsers(id);
	  }
	  @RequestMapping( path = "/transfer", method = RequestMethod.POST)
	  public boolean transfer(@RequestBody TransferFunds transferFunds) {
		  return userDAO.transfer(transferFunds);
	  }
	  @RequestMapping( path = "/{id}/history", method = RequestMethod.GET)
	  public List<TransactionHistory> getUserHistory(@PathVariable int id) {
		  return userDAO.getUserHistory(id);
	  }
	  
	  @RequestMapping( path = "/request", method = RequestMethod.POST)
	  public boolean request(@RequestBody TransferFunds transferFunds) {
		  return userDAO.request(transferFunds);
	  }
	  
	  @RequestMapping( path = "/{id}/requests", method = RequestMethod.GET)
	  public List<TransactionHistory> getPendingRequests(@PathVariable int id) {
		  return userDAO.getPendingRequests(id);
	  }
	  @RequestMapping( path = "/action", method = RequestMethod.POST)
	  public boolean actionOnRequest (@RequestBody UserAction userAction) {
		  //System.out.println("Request to approve at server");
		  return userDAO.actionOnRequest(userAction);
	  }
}