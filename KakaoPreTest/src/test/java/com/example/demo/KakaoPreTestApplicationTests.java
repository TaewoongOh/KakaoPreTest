package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.controller.KakaoPreTestController;

@SpringBootTest
class KakaoPreTestApplicationTests {
	@Autowired
	KakaoPreTestController ctrl;

	@Test
	void contextLoads() {
		HashMap<String, Object> paymentIn = new HashMap<String, Object>();
		paymentIn.put("cardno", "1234567898585858");
		paymentIn.put("date", "1020");
		paymentIn.put("cvc", "369");
		paymentIn.put("dividemm", "0");
		paymentIn.put("amount", "1000000");
		
		HashMap<String, Object> payment = ctrl.payment(paymentIn);
		
		HashMap<String, Object> searchIn = new HashMap<String, Object>();
		searchIn.put("id", payment.get("id"));
		HashMap<String, Object> search = ctrl.search(searchIn);
		assertEquals("123456*******858", search.get("cardno"));
		assertEquals("1000000", search.get("amount"));
		
		HashMap<String, Object> cancelIn = new HashMap<String, Object>();
		cancelIn.put("id", payment.get("id"));
		cancelIn.put("amount", paymentIn.get("amount"));
		HashMap<String, Object> cancel = ctrl.cancel(cancelIn);
		
		HashMap<String, Object> searchIn2 = new HashMap<String, Object>();
		searchIn2.put("id", cancel.get("id"));
		HashMap<String, Object> search2 = ctrl.search(searchIn2);
		assertEquals("123456*******858", search2.get("cardno"));
		
	}

}
