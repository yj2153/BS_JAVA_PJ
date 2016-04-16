package bank;

public class BankNormal extends Bankbook {

	// 생성자 ( 이름, 개설금액
	public BankNormal(String name, int money) {
		super(name, money);
	}

	// 입금 ( 계좌, 금액, 이자율, 특별일반, 총 계좌 수) :동기화
	@Override
	public void addMoney(int money) {
		// 잔액 + 입금액
		super.iMoney += money;
	}

	// 계좌 타입반환
	@Override
	public int getType() {
		return NORMAL;
	}

	// 이자율 없음.
	@Override
	public int getRest() {
		return -1;
	}
}
