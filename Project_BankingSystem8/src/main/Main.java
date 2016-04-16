package main;

//작성일 : 2015 - 12- 20
//작성자 : 오연주
//뱅킹 시스템 ( 신규, 입/출금, 전체출력, 검색출력, 종료
//버전 1.7

//서버 연동

public class Main {
	public static void main(String[] args) {
		Manager bank = Manager.getMgr();
		bank.execute();
	}
}
