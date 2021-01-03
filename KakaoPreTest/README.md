1. 개발 프레임워크 : Spring boot
---

2. 테이블 설계
```
CREATE TABLE `KAKAOPRETEST` (
  `id` varchar(20) NOT NULL COMMENT '관리번호',
  `carddata` varchar2(450) NOT NULL COMMENT '전문데이터',
  `success` varchar(1) NOT NULL COMMENT '성공여부',
  `orignid` varchar(20) NOT NULL COMMENT '원거래관리번호',
  `processcd` varchar(10) NOT NULL COMMENT '데이터구분',
  `amount` varchar(10) NOT NULL COMMENT '거래금액',
  `tax` varchar(10) NOT NULL COMMENT '부가가치세',
  `seccardno` varchar(300) NOT NULL COMMENT '암호화된카드번호'
  )
```
---

3. 문제해결전략

결제API
  - 카드정보 및 결제요청 정보를 POST 방식으로 받은 후 카드정보는 AES128로 암호화 처리
  - 부가가치세 정보 없을 시 부가가치세 계산
  - 위 정보 String 문자열로 만들어서 테이블에 저장

취소API
  - POST 방식으로 받은 관리번호로 결제정보 조회
  - 취소금액과 기존에 저장된 결제정보의 금액 비교, 부가가치세와 기존에 저장된 부가가치세 금액 비교 등을 통하여 전체취소와 부분취소를 구분
  - 취소실패정보 메세지처리
  - 취소성공 시 문자열 생성 후 저장

조회API
  - POST 방식으로 받은 관리번호로 결제정보 조회
  - 기존 암호화 된 카드정보를 복호화 처리하여 return정보로 세팅
  - 카드번호 마스킹 처리
  - 실패 시 메세지처리
---

4. 빌드 및 실행 방법
  eclipse에 import 후 자동빌드
  웹페이지 에서 localhost 실행
