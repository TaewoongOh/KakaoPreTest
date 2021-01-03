package com.example.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.AES128;
import com.example.CancelFailException;
import com.example.vo.MemberRepository;
import com.example.vo.MemberVo;

@Component
public class KakaoPreTestService{
	
	@Autowired
	MemberRepository mr;
	
	
	// 숫자 우측으로 정렬, 빈 자리 공백, ex) 4자리 숫자 : 3 -> "___3"
	  private String padNumber(String value, int length) {
	    while (value.length() < length) {
	      value = " " + value;
	    }
	    return value;
	  }

	  //숫자(0) 우측으로 정렬, 빈 자리 0, ex) 4자리 숫자(0) : 3 -> "0003"
	  private String padZero(String value, int length) {
	    while (value.length() < length) {
	      value = "0" + value;
	    }
	    return value;
	  }
	  
	  //숫자(L) 좌측으로 정렬, 빈 자리 공백, ex) 4자리 숫자(L) : 3 -> "3___"
	  private String padNumberL(String value, int length) {
	    while (value.length() < length) {
	      value = value + " ";
	    }
	    return value;
	  }
	  
	//문자 좌측으로 정렬, 빈 자리 공백, ex) 10자리 문자 : HOMEWORK -> "HOMEWORK__"
	  private String padText(String value, int length) {
	    while (value.length() < length) {
	      value = value + " ";
	    }
	    return value;
	  }
	  
	  private String buildCardDataHeader(String processcd, String id) {
		    StringBuilder sb = new StringBuilder();
		    sb.append(padZero("446", 4));
		    sb.append(padText(processcd, 10));
		    sb.append(padText(id, 20));
		    
		    return sb.toString();
	  }
	  
	  private String buildCardDataBody(String cardno, String date, String cvc, String dividemm, String amount, String tax, String orignid, String endCardNo) {
	    StringBuilder sb = new StringBuilder();
	    sb.append(padNumberL(cardno, 20));
	    sb.append(padZero(dividemm, 2));
	    sb.append(padNumberL(date, 4));
	    sb.append(padNumberL(cvc, 3));
	    sb.append(padNumber(amount, 10));
	    sb.append(padZero(tax, 10));
	    sb.append(padText(orignid, 20));
	    
	    sb.append(padText(endCardNo, 300));
	    sb.append(padText(" ", 47));
	    
	    return sb.toString();
	  }
	  
	//tax 계산
	  private String calTax(String amount) {
	    String value = null;
	    BigDecimal Bamount = new BigDecimal(amount);
	    BigDecimal tax = Bamount.divide(new BigDecimal(11), 0, BigDecimal.ROUND_HALF_UP);
	    
	    value = tax.toString();
	    return value;
	  }
	

	public MemberVo payment(String cardno, String date, String cvc, String dividemm, String amount, String tax) {
		
		MemberVo result = new MemberVo();
		
		String tempTax = null;
		if(tax != null) {
			tempTax = tax;
		}else {
			tempTax = calTax(amount);
		}
		
		String id = padZero(String.valueOf(System.currentTimeMillis()), 20);
		String endCardNo = AES128.encAES(cardno+"|"+date+"|"+cvc);
		String cardData = buildCardDataHeader("PAYMENT", id)+buildCardDataBody(cardno, date, cvc, dividemm, amount, tempTax, " ", endCardNo);
		
		result.setId(id);
		result.setCarddata(cardData);
		result.setSuccess("Y");
		result.setOrignid("");
		result.setProcesscd("PAYMENT");
		result.setAmount(amount);
		result.setTax(tempTax);
		result.setSeccardno(endCardNo);
		
		mr.save(result);
		
		return result;
	}

	public MemberVo cancel(String id, String amount, String tax) {
		
		MemberVo result = new MemberVo();
		
		String cancelCd = null;
		Optional<MemberVo> orginData = mr.findById(id);
		if(!orginData.isPresent()) {
			throw new CancelFailException("원 거래가 없습니다.", "001");
		}
		
		/* 관리번호로 기존 취소정보 조회 */
		BigDecimal sumCancel = BigDecimal.ZERO;
		BigDecimal sumTax = BigDecimal.ZERO;
		List<MemberVo> memberVoList = mr.findByOrignid(id);
		// 부분취소 정보를 조회해서 부분취소 한 금액과 부가가치세를 합산한다.
		for(MemberVo vo : memberVoList) {
			sumCancel = new BigDecimal(vo.getAmount()).add(sumCancel);
			sumTax = new BigDecimal(vo.getTax()).add(sumTax);
		}
		
		MemberVo memberVo = orginData.get();
		BigDecimal orginAmount = new BigDecimal(memberVo.getAmount());
		BigDecimal tmpAmount = new BigDecimal(amount);
		if(orginAmount.compareTo(tmpAmount) < 0) {
			throw new CancelFailException("취소 금액이 원금액보다 큽니다.", "002");
		}else if(orginAmount.compareTo(tmpAmount) > 0) {
			if((orginAmount.subtract(sumCancel)).compareTo(tmpAmount) < 0) {
				throw new CancelFailException(amount + "원 취소하려 했으나 남은 결제금액 보다 커서 실패", "004");
			}else {
				cancelCd = "부분취소";
			}
		}else {
			cancelCd = "전체취소";
		}
		
		BigDecimal orginTax = new BigDecimal(memberVo.getTax());
		if(tax == null && "전체취소".equals(cancelCd)) {
			tax = memberVo.getTax();
		}else if(tax == null && "부분취소".equals(cancelCd)) {
			if ((orginAmount.subtract(sumCancel)).compareTo(tmpAmount) == 0) {
				tax = orginTax.subtract(sumTax).toString();
			} else {
				tax = calTax(amount);
			}
		}
		BigDecimal tmpTax = new BigDecimal(tax);
		if(orginTax.compareTo(tmpTax) < 0) {
			throw new CancelFailException("취소 부가가치세가 원부가가치세보다 큽니다.", "003");
		}else if(orginTax.compareTo(tmpTax) > 0) {
			if((orginTax.subtract(sumTax)).compareTo(tmpTax) < 0) {
				throw new CancelFailException(amount + "(" + tax + ")원 취소하려했으나 남은 부가가치세가 더 작으므로 실패", "005");
			}
		}
		
		if((orginAmount.subtract(sumCancel)).compareTo(tmpAmount) == 0 && (orginTax.subtract(sumTax)).compareTo(tmpTax) != 0) {
			throw new CancelFailException(amount + "(" + tax + ")원 취소하려했으나 남은 부가 가치세 금액(" + orginAmount.subtract(sumCancel).toString() + ")이 더 크므로 실패", "006");
		}
		
		
		
		String cancelId = padZero(String.valueOf(System.currentTimeMillis()), 20);
		String endCardNo = memberVo.getSeccardno();
		
		String decCardNo = AES128.decAES(endCardNo);
		
		String[] cardInfo = decCardNo.split("\\|");
		
		String cardData = buildCardDataHeader("CANCEL", cancelId)+buildCardDataBody(cardInfo[0], cardInfo[1], cardInfo[2], "00", amount, tax, id, endCardNo);
		
		result.setId(cancelId);
		result.setCarddata(cardData);
		result.setSuccess("Y");
		result.setOrignid(id);
		result.setProcesscd("CANCEL");
		result.setAmount(amount);
		result.setTax(tax);
		result.setSeccardno(endCardNo);
		
		mr.save(result);
		
		return result;
	}

	public HashMap<String, Object> search(String id) {
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		Optional<MemberVo> orginData = mr.findById(id);
		if(!orginData.isPresent()) {
			throw new CancelFailException("조회된 내용이 없습니다.", "010");
		}
		
		MemberVo memberVo = orginData.get();
		String endCardNo = memberVo.getSeccardno();
		String decCardNo = AES128.decAES(endCardNo);
		String[] cardInfo = decCardNo.split("\\|");
		
		result.put("id", id);
		result.put("cardno", cardInfo[0].substring(0, 6)+cardInfo[0].substring(6, cardInfo[0].length()-3).replaceAll(".", "*")+cardInfo[0].substring(cardInfo[0].length()-3));
		result.put("date", cardInfo[1]);
		result.put("cvc", cardInfo[2]);
		result.put("processcd", memberVo.getProcesscd());
		result.put("amount", memberVo.getAmount());
		result.put("tax", memberVo.getTax());
		return result;
	}
	
}
