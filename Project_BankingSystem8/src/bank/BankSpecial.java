package bank;

public class BankSpecial extends Bankbook {
	private int dInterest; // ������

	//������ ( �̸�, ���� �ݾ�, ������
	public BankSpecial(String name, int money, int interest) {
		super(name, money);
		// ============================================================================

		dInterest = interest; // ������
		// ���ڱݾ� �߰�.
		double resultRest = (dInterest / PERCENTAGE);
		super.iMoney += (int) (super.iMoney * resultRest);
	}

	@Override
	public void addMoney(int money) { 
		// �������� �ִٸ�
		double resultRest = (dInterest / 100.0);
		int iTemp = (int) (money * resultRest);

		// �Աݾ� ����
		int iResult = money + iTemp;
		//����ȭ ����
		synchronized (this) {
			iMoney += iResult; // ���±ݾ� + ������
		}
	}

	//����Ÿ�� ��ȯ
	@Override
	public int getType() {		return SPECIAL;	}

	//������ ��ȯ
	@Override
	public int getRest() {		return dInterest;	}

	
}
