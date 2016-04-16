package bank;

import common.IBankbook;

public abstract class Bankbook implements IBankbook{

	protected String strUserName; // 개설자 이름
	protected int iMoney; // 계좌금액

	abstract public int getType();	//계좌 타입 반환
	abstract public int getRest(); //이자율반환
	abstract public void addMoney(int money);
	
	// 신규계좌 생성 ( 이름, 금액)
	protected Bankbook(String name, int money) {
		strUserName = name;		 // 개설자 이름
		iMoney = money; 				// 계좌금액
	}
	
	//예금출금
	public boolean subMoney(int money){
		if(this.iMoney < money)
			return false;
		
		iMoney -= money;
		return true;
	}
	
	//계좌금액
	public void setMoney(int money){this.iMoney = money;}
	public int getMoney() { return iMoney; }	
	//예금주
	public String getName() { return strUserName; }	
}
