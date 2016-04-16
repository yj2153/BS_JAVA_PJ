package bank;

public class BankSpecial extends Bankbook {
	private int dInterest; // 이자율

	//생성자 ( 이름, 개설 금액, 이자율
	public BankSpecial(String name, int money, int interest) {
		super(name, money);
		// ============================================================================

		dInterest = interest; // 이자율
		// 이자금액 추가.
		double resultRest = (dInterest / PERCENTAGE);
		super.iMoney += (int) (super.iMoney * resultRest);
	}

	@Override
	public void addMoney(int money) { 
		// 이자율이 있다면
		double resultRest = (dInterest / 100.0);
		int iTemp = (int) (money * resultRest);

		// 입금액 저장
		int iResult = money + iTemp;
		//동기화 적용
		synchronized (this) {
			iMoney += iResult; // 계좌금액 + 이자율
		}
	}

	//계좌타입 반환
	@Override
	public int getType() {		return SPECIAL;	}

	//이자율 반환
	@Override
	public int getRest() {		return dInterest;	}

	
}
