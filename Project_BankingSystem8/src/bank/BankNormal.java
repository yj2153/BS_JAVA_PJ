package bank;

public class BankNormal extends Bankbook {

	// ������ ( �̸�, �����ݾ�
	public BankNormal(String name, int money) {
		super(name, money);
	}

	// �Ա� ( ����, �ݾ�, ������, Ư���Ϲ�, �� ���� ��) :����ȭ
	@Override
	public void addMoney(int money) {
		// �ܾ� + �Աݾ�
		super.iMoney += money;
	}

	// ���� Ÿ�Թ�ȯ
	@Override
	public int getType() {
		return NORMAL;
	}

	// ������ ����.
	@Override
	public int getRest() {
		return -1;
	}
}
