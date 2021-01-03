package com.example.controller;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.CancelFailException;
import com.example.service.KakaoPreTestService;
import com.example.vo.MemberRepository;
import com.example.vo.MemberVo;

@RestController
public class KakaoPreTestController{
	
	@Autowired
	KakaoPreTestService ks;
	
	
	@PostMapping("/payment")
	public HashMap<String, Object> payment(@RequestBody HashMap<String, Object> in){
		
		String cardno = (String)in.get("cardno");
		String date = (String)in.get("date");
		String cvc = (String)in.get("cvc");
		String dividemm = (String)in.get("dividemm");
		String amount = (String)in.get("amount");
		String tax = (String)in.get("tax");
		
		
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		MemberVo MemberVo = ks.payment(cardno, date, cvc, dividemm, amount, tax);
		
		result.put("id", MemberVo.getId());
		result.put("carddata", MemberVo.getCarddata());
		return result;
	}
	
	@PostMapping("/cancel")
	public HashMap<String, Object> cancel(@RequestBody HashMap<String, Object> in){
		
		String id = (String)in.get("id");
		String amount = (String)in.get("amount");
		String tax = (String)in.get("tax");
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		try {
			MemberVo MemberVo = ks.cancel(id, amount, tax);
			result.put("id", MemberVo.getId());
			result.put("carddata", MemberVo.getCarddata());
		} catch (CancelFailException e) {
			result.put("error_code", e.getCode());
			result.put("error_message", e.getMessage());
		}
		return result;
	}
	
	@PostMapping("/search")
	public HashMap<String, Object> search(@RequestBody HashMap<String, Object> in){
		
		String id = (String)in.get("id");
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		try {
			result = ks.search(id);
		} catch (CancelFailException e) {
			result.put("error_code", e.getCode());
			result.put("error_message", e.getMessage());
		}
		return result;
	}
	
} 
