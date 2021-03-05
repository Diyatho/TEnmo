package com.techelevator.tenmo.models;

import java.math.BigDecimal;

public class TransactionHistory {
	
	private Integer transferId;
	private Integer transfer_type_id;
	private Integer transfer_status_id;
	private String senderName;
	private String receiverName;
	private BigDecimal amount;
	public Integer getTransferId() {
		return transferId;
	}
	public void setTransferId(Integer transferId) {
		this.transferId = transferId;
	}
	public Integer getTransfer_type_id() {
		return transfer_type_id;
	}
	public void setTransfer_type_id(Integer transfer_type_id) {
		this.transfer_type_id = transfer_type_id;
	}
	public Integer getTransfer_status_id() {
		return transfer_status_id;
	}
	public void setTransfer_status_id(Integer transfer_status_id) {
		this.transfer_status_id = transfer_status_id;
	}

	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
		}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	

}
