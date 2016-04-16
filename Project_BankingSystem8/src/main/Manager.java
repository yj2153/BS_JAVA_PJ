package main;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import bank.BankNormal;
import bank.BankSpecial;
import bank.Bankbook;
import common.IBankbook;
import common.UI;
import network.BankingClient;
import network.Packet;

public class Manager implements IBankbook	{
	private HashMap<String, Bankbook> bankMap; // <계좌번호, 계좌정보>
	private static Manager mgr;
	// 생성자
	private Manager() {
		bankMap = new HashMap<>();
	}// Manager() 
	public static Manager getMgr() { return mgr; }

	// 실행
	public void execute() {
		boolean bLoop = true; // 반복문.

		// 반복구문 : 종료가 아니라면 계속 실행
		while (bLoop) {

			// 메뉴 실행.
			switch (menu()) {
			case CREATE:			newUser();				break; // 신규계좌 생성 ( 이름, 계좌, 금액, 이자율, 특별일반, 총 계좌 수)
			case ADD:					addMoney();			break; // 입금 ( 계좌, 금액, 이자율, 특별일반, 총 계좌 수)
			case SUB:					subMoney();			break; // 출금 (계좌 , 금액, 총 계좌 수)
			case ALL:						allShow();					break; // 계좌정보 전체 출력 (이름, 계좌 금액, 이자율, 특별 일반, 총 계좌 수)
			case ONE:					showUser();				break; // 계좌정보 검색 (이름, 계좌, 돈, 이자율, 특별일반, 총 계좌수)
			case DELETE:				delete();						break; //계좌 해지
			case EXIT:					bLoop = false;			break; // 종료

			// 없는 메뉴
			default:
				UI.getUI().errorMsg("없는 메뉴입니다.");
				break;
			} //end switch
		}//end while
		UI.getUI().ouputMsg("사용을 종료합니다. 안녕히 가세요.");
	}//execute()

	// 메뉴 선택
	private int menu() {
		System.out.println("********************");
		System.out.println("::뱅킹시스템::");
		System.out.println("********************");
		System.out.println("  [메  뉴 ]  ");
		System.out.println("********************");
		System.out.println(" 1. 신규계좌 개설 ");
		System.out.println(" 2. 입 금 ");
		System.out.println(" 3. 출 금 ");
		System.out.println(" 4. 계좌정보 전체 출력 ");
		System.out.println(" 5. 계좌정보  검색 ");
		System.out.println(" 7. 종 료 ");
		System.out.println("********************");

		int menu = UI.getUI().nextValue("메뉴 >> ");
		return menu;
	}// menu()

	// 통장 개설
	private void newUser() {
		// 계좌 종류, 계좌번호, 계좌 개설자, 출력문,
		String strSpec = "", strAccount = "",strName = "";
		//계좌 개설액. 이자율
		int iMoney = 0, dInterest = 0;

		// true : 이자, false :무이자
		strSpec = UI.getUI().nextString("일반계좌 무이자 : 1 , 특별계좌 이자 : 2\n", String.valueOf(NORMAL), String.valueOf(SPECIAL));
		int iSpec = Integer.valueOf(strSpec);
		// 계좌 번호.
		strAccount = UI.getUI().nextString("계좌 번호 >> ");
		// 개설자 이름
		strName = UI.getUI().nextString("계좌 예금주 >>");
		// 계좌 개설액
		iMoney = UI.getUI().nextValue("계좌 개설액 >> ");
		//특별계좌라면 이자율 추가
		if (iSpec == SPECIAL) 
			dInterest = UI.getUI().nextValue("이자율 >> ");
		//서버 연결
		if(BankingClient.getClient().getServer())
		{
			BankingClient.getClient().send(Packet.ID.CREATE, strName, strAccount, iMoney, iSpec, dInterest);
			return;
		}
		
		// 동일계좌가 존재한다면
		if (bankMap.get(strAccount) != null) {
			UI.getUI().errorMsg("동일 계좌가 존재합니다.");
			return;
		}
		//실제 데이터 추가
		 newUser(iSpec,strAccount,strName, iMoney, dInterest);
		
	}//newUser() 

	// 입금
	private void addMoney() {
		// 계좌 입력
		String account = UI.getUI().nextString("계좌번호 >> ");
		// 입금액 입력
		int money = UI.getUI().nextValue("입금액 >> ");
		//서버연결
		if (BankingClient.getClient().getServer()) {
			BankingClient.getClient().send(Packet.ID.ADD,account, money);
			return;
		}

		// 찾는 계좌가 없다면 리턴
		if (bankMap.get(account) == null){
			UI.getUI().errorMsg(account + " 계좌는 없는 계좌입니다.");
			return;
		}
		
		Bankbook bo = bankMap.get(account);
		bo.addMoney(money);
		addMoney(bo.getType(), bo.getName(), bo.getName(),bo.getMoney(), bo.getRest());
	}// addMoney() 

	// 출금
	private void subMoney() {
		// 계좌 입력
		String account = UI.getUI().nextString("계좌번호 >> ");
		// 입금액 입력
		int money = UI.getUI().nextValue("출금액 >>");
		//서버연결
		if (BankingClient.getClient().getServer()) {
			BankingClient.getClient().send(Packet.ID.SUB,account, money);
			return;
		}
		// 찾는 계좌가 없다면 리턴
		if (bankMap.get(account) == null) {
			UI.getUI().errorMsg(account + " 계좌는 없는 계좌입니다.");
			return;
		}
		
		// 출금완료 출력문
		Bankbook bo = bankMap.get(account);
		// 출금 성공 시
		if (bo.subMoney(money))
			UI.getUI().ouputMsg(String.valueOf(account), " 계좌에서 ", String.valueOf(money), " 원 출금 완료하였습니다. 잔액은 ",
					String.valueOf(bo.getMoney()), "원 ");
		else
			UI.getUI().ouputMsg("잔액이 부족합니다.");
	}// subMoney()

	// 전체 계좌출력
	private void allShow() {
		//서버연결
		if(BankingClient.getClient().getServer()){
			BankingClient.getClient().send(Packet.ID.ALL);
			return;
		}
		
		UI.getUI().ouputMsg("현재 개설된 계좌는 총 " + bankMap.size() + "개입니다.");
		if(bankMap.size() <= 0)
			return;
		
		//Set -> Iterator로 key값 뽑아서 출력
		Set<String> setKey = bankMap.keySet();
		Iterator<String> iteKey = setKey.iterator();
		
		String key = "";	//키값 : 계좌번호
		while(iteKey.hasNext())
		{
			key = iteKey.next();	//키값 얻기
			Bankbook bo = bankMap.get(key);	//계좌번호로 검색하여 정보 찾아서 반환
			showUser(bo.getType(), bo.getName(), key, bo.getMoney(), bo.getRest());
		}//end while
	
	}//allShow() 
	
	// 특정계좌 출력
	private void showUser() {
		//계좌번호 입력
		String account = UI.getUI().nextString("계좌번호 >> ");
		// 서버연결
		if (BankingClient.getClient().getServer()) {
			BankingClient.getClient().send(Packet.ID.ONE,account);
			return;
		}
		// 찾는 계좌가 없다면 리턴
		if (bankMap.get(account) == null){
			UI.getUI().errorMsg(account + " 계좌는 없는 계좌입니다.");
			return;
		}
		
		Bankbook bo = bankMap.get(account);
		showUser(bo.getType(), bo.getName(), account, bo.getMoney(), bo.getRest());
	
	}
	
	//계좌삭제
	private void delete()
	{
		//계좌번호 입력
		String account = UI.getUI().nextString("계좌번호 >> ");
		// 서버연결
		if (BankingClient.getClient().getServer()) {
			BankingClient.getClient().send(Packet.ID.DELETE, account);
			return;
		}
		
		// 찾는 계좌가 없다면 리턴
		if (bankMap.get(account) == null) {
			UI.getUI().errorMsg(account + " 계좌는 없는 계좌입니다.");
			return;
		}
		delete(account);
	}// delete()
	
//===============================================================

	//계좌 개설
	public void newUser(int iSpec, String sAcc, String name, int money, int interest) {
		// 특별 계좌 생성
		if (iSpec == SPECIAL) {
			bankMap.put(sAcc, new BankSpecial(name, money, interest));
			UI.getUI().ouputMsg("[ 특별계좌 ] ", name, " 님 계좌개설 완료.   잔액 : ", String.valueOf(bankMap.get(sAcc).getMoney()),
					"원, 이자율 : ", String.valueOf(interest), "%");
		}
		// 일반 계좌 생성
		else {
			bankMap.put(sAcc, new BankNormal(name, money));
			UI.getUI().ouputMsg("[ 일반계좌 ] ", name, " 님 계좌개설 완료.  잔액 : ", String.valueOf(money), "원");
		}
	}

	//입금완료 데이터 저장
	public void addMoney(int type, String name, String sAcc, int money, int interest) {
		//클라이언트 계좌에 없으시 생성
		if(bankMap.get(sAcc) == null){
			newUser(type, sAcc, name,money,interest);
		}
		
		Bankbook bo = bankMap.get(sAcc);
		bo.setMoney(money);
		// 입금완료 출력문
		if (type == SPECIAL)
			UI.getUI().ouputMsg("[ 특별계좌 ] :", name, "님] ", sAcc, " 계좌에 입금 완료하였습니다. 잔액은 ", String.valueOf(money), " 원");
		else
			UI.getUI().ouputMsg("[ 일반계좌 ] :", name, "님] ", sAcc, " 계좌에 입금 완료하였습니다. 잔액은 ", String.valueOf(money), " 원");
	}

	// 출금완료 데이터 저장 
	public void subMoney(int type, String name, String sAcc, int money, int interest) {
		//클라이언트 계좌에 없으시 생성
		if(bankMap.get(sAcc) == null){
			newUser(type, sAcc, name,money,interest);
		}
		
		Bankbook bo = bankMap.get(sAcc);
		bo.setMoney(money);
		// 입금완료 출력문
		if (type == SPECIAL)
			UI.getUI().ouputMsg("[ 특별계좌 ] :", name, "님] ", sAcc, " 계좌에 입금 완료하였습니다. 잔액은 ", String.valueOf(money), " 원");
		else
			UI.getUI().ouputMsg("[ 일반계좌 ] :", name, "님] ", sAcc, " 계좌에 입금 완료하였습니다. 잔액은 ", String.valueOf(money), " 원");
	}
	//계좌해지
	public void delete(String sAcc){
		// 계좌 해지
		bankMap.remove(sAcc);
		UI.getUI().ouputMsg(sAcc + "계좌를 해지 하였습니다.");
	}
	
	//showUser()
	public void showUser(int type, String name, String sAcc, int money, int interest){
		// 클라이언트 계좌에 없으시 생성
		if (bankMap.get(sAcc) == null) {
			newUser(type, sAcc, name, money, interest);
		}

		Bankbook bo = bankMap.get(sAcc);
		// 계좌 정보 출력
		if (bo.getType() == SPECIAL)
			UI.getUI().ouputMsg("[ 특별계좌 ] :" , name, "님] 계좌번호 : ",sAcc + " , 잔액 : " ,String.valueOf(money) , " , 이자율 : ", String.valueOf(interest), "%");
		else
			UI.getUI().ouputMsg("[ 일반계좌 ] :" , name, "님] 계좌번호 : " , sAcc + " , 잔액 : ", String.valueOf(money)) ;
	}
}
