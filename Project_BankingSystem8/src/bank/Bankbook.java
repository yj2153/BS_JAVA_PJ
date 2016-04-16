package bank;

import common.IBankbook;

public abstract class Bankbook implements IBankbook{

	protected String strUserName; // ������ �̸�
	protected int iMoney; // ���±ݾ�

	abstract public int getType();	//���� Ÿ�� ��ȯ
	abstract public int getRest(); //��������ȯ
	abstract public void addMoney(int money);
	
	// �ű԰��� ���� ( �̸�, �ݾ�)
	protected Bankbook(String name, int money) {
		strUserName = name;		 // ������ �̸�
		iMoney = money; 				// ���±ݾ�
	}
	
	//�������
	public boolean subMoney(int money){
		if(this.iMoney < money)
			return false;
		
		iMoney -= money;
		return true;
	}
	
	//���±ݾ�
	public void setMoney(int money){this.iMoney = money;}
	public int getMoney() { return iMoney; }	
	//������
	public String getName() { return strUserName; }	
}
