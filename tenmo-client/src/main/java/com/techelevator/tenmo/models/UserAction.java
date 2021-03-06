package com.techelevator.tenmo.models;

public class UserAction {
	private Integer transferId;
	private Integer action;
	private Integer userId;
	
	public UserAction(Integer transferId, Integer action, Integer userId) {
		this.transferId = transferId;
		this.action = action;
		this.setUserId(userId);
	}
	
	public Integer getTransferId() {
		return transferId;
	}
	public void setTransferId(Integer transferId) {
		this.transferId = transferId;
	}
	public Integer getAction() {
		return action;
	}
	public void setAction(Integer action) {
		this.action = action;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

}
