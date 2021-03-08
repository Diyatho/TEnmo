package com.techelevator.tenmo.models;

import java.math.BigDecimal;

public class TransferFunds {
	
	private Integer senderId;
	private Integer receiverId;
	
   // @Positive(message = "Amount cannot be negative")
	private BigDecimal amount;
	private Integer transferId;
	
	public TransferFunds(int senderId, int receiverId, BigDecimal amount) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.amount = amount;
	}
	
	public Integer getSenderId() {
		return senderId;
	}
	public void setSenderId(Integer senderId) {
		this.senderId = senderId;
	}
	public Integer getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(Integer receiverId) {
		this.receiverId = receiverId;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getTransferId() {
		return transferId;
	}

	public void setTransferId(Integer transferId) {
		this.transferId = transferId;
	}
	

}
