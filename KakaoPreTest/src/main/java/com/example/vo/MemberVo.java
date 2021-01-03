package com.example.vo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;


@Entity(name = "KAKAOPRETEST")
public class MemberVo {
	@Id
	private String id;
	@Lob
	private String carddata;
	private String success;
	private String orignid;
	private String processcd;
	private String amount;
	private String tax;
	private String seccardno;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCarddata() {
		return carddata;
	}
	public void setCarddata(String carddata) {
		this.carddata = carddata;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getOrignid() {
		return orignid;
	}
	public void setOrignid(String orignid) {
		this.orignid = orignid;
	}
	public String getProcesscd() {
		return processcd;
	}
	public void setProcesscd(String processcd) {
		this.processcd = processcd;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getTax() {
		return tax;
	}
	public void setTax(String tax) {
		this.tax = tax;
	}
	public String getSeccardno() {
		return seccardno;
	}
	public void setSeccardno(String seccardno) {
		this.seccardno = seccardno;
	}

}
