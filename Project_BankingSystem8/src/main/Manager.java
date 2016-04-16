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
	private HashMap<String, Bankbook> bankMap; // <���¹�ȣ, ��������>
	private static Manager mgr;
	// ������
	private Manager() {
		bankMap = new HashMap<>();
	}// Manager() 
	public static Manager getMgr() { return mgr; }

	// ����
	public void execute() {
		boolean bLoop = true; // �ݺ���.

		// �ݺ����� : ���ᰡ �ƴ϶�� ��� ����
		while (bLoop) {

			// �޴� ����.
			switch (menu()) {
			case CREATE:			newUser();				break; // �ű԰��� ���� ( �̸�, ����, �ݾ�, ������, Ư���Ϲ�, �� ���� ��)
			case ADD:					addMoney();			break; // �Ա� ( ����, �ݾ�, ������, Ư���Ϲ�, �� ���� ��)
			case SUB:					subMoney();			break; // ��� (���� , �ݾ�, �� ���� ��)
			case ALL:						allShow();					break; // �������� ��ü ��� (�̸�, ���� �ݾ�, ������, Ư�� �Ϲ�, �� ���� ��)
			case ONE:					showUser();				break; // �������� �˻� (�̸�, ����, ��, ������, Ư���Ϲ�, �� ���¼�)
			case DELETE:				delete();						break; //���� ����
			case EXIT:					bLoop = false;			break; // ����

			// ���� �޴�
			default:
				UI.getUI().errorMsg("���� �޴��Դϴ�.");
				break;
			} //end switch
		}//end while
		UI.getUI().ouputMsg("����� �����մϴ�. �ȳ��� ������.");
	}//execute()

	// �޴� ����
	private int menu() {
		System.out.println("********************");
		System.out.println("::��ŷ�ý���::");
		System.out.println("********************");
		System.out.println("  [��  �� ]  ");
		System.out.println("********************");
		System.out.println(" 1. �ű԰��� ���� ");
		System.out.println(" 2. �� �� ");
		System.out.println(" 3. �� �� ");
		System.out.println(" 4. �������� ��ü ��� ");
		System.out.println(" 5. ��������  �˻� ");
		System.out.println(" 7. �� �� ");
		System.out.println("********************");

		int menu = UI.getUI().nextValue("�޴� >> ");
		return menu;
	}// menu()

	// ���� ����
	private void newUser() {
		// ���� ����, ���¹�ȣ, ���� ������, ��¹�,
		String strSpec = "", strAccount = "",strName = "";
		//���� ������. ������
		int iMoney = 0, dInterest = 0;

		// true : ����, false :������
		strSpec = UI.getUI().nextString("�Ϲݰ��� ������ : 1 , Ư������ ���� : 2\n", String.valueOf(NORMAL), String.valueOf(SPECIAL));
		int iSpec = Integer.valueOf(strSpec);
		// ���� ��ȣ.
		strAccount = UI.getUI().nextString("���� ��ȣ >> ");
		// ������ �̸�
		strName = UI.getUI().nextString("���� ������ >>");
		// ���� ������
		iMoney = UI.getUI().nextValue("���� ������ >> ");
		//Ư�����¶�� ������ �߰�
		if (iSpec == SPECIAL) 
			dInterest = UI.getUI().nextValue("������ >> ");
		//���� ����
		if(BankingClient.getClient().getServer())
		{
			BankingClient.getClient().send(Packet.ID.CREATE, strName, strAccount, iMoney, iSpec, dInterest);
			return;
		}
		
		// ���ϰ��°� �����Ѵٸ�
		if (bankMap.get(strAccount) != null) {
			UI.getUI().errorMsg("���� ���°� �����մϴ�.");
			return;
		}
		//���� ������ �߰�
		 newUser(iSpec,strAccount,strName, iMoney, dInterest);
		
	}//newUser() 

	// �Ա�
	private void addMoney() {
		// ���� �Է�
		String account = UI.getUI().nextString("���¹�ȣ >> ");
		// �Աݾ� �Է�
		int money = UI.getUI().nextValue("�Աݾ� >> ");
		//��������
		if (BankingClient.getClient().getServer()) {
			BankingClient.getClient().send(Packet.ID.ADD,account, money);
			return;
		}

		// ã�� ���°� ���ٸ� ����
		if (bankMap.get(account) == null){
			UI.getUI().errorMsg(account + " ���´� ���� �����Դϴ�.");
			return;
		}
		
		Bankbook bo = bankMap.get(account);
		bo.addMoney(money);
		addMoney(bo.getType(), bo.getName(), bo.getName(),bo.getMoney(), bo.getRest());
	}// addMoney() 

	// ���
	private void subMoney() {
		// ���� �Է�
		String account = UI.getUI().nextString("���¹�ȣ >> ");
		// �Աݾ� �Է�
		int money = UI.getUI().nextValue("��ݾ� >>");
		//��������
		if (BankingClient.getClient().getServer()) {
			BankingClient.getClient().send(Packet.ID.SUB,account, money);
			return;
		}
		// ã�� ���°� ���ٸ� ����
		if (bankMap.get(account) == null) {
			UI.getUI().errorMsg(account + " ���´� ���� �����Դϴ�.");
			return;
		}
		
		// ��ݿϷ� ��¹�
		Bankbook bo = bankMap.get(account);
		// ��� ���� ��
		if (bo.subMoney(money))
			UI.getUI().ouputMsg(String.valueOf(account), " ���¿��� ", String.valueOf(money), " �� ��� �Ϸ��Ͽ����ϴ�. �ܾ��� ",
					String.valueOf(bo.getMoney()), "�� ");
		else
			UI.getUI().ouputMsg("�ܾ��� �����մϴ�.");
	}// subMoney()

	// ��ü �������
	private void allShow() {
		//��������
		if(BankingClient.getClient().getServer()){
			BankingClient.getClient().send(Packet.ID.ALL);
			return;
		}
		
		UI.getUI().ouputMsg("���� ������ ���´� �� " + bankMap.size() + "���Դϴ�.");
		if(bankMap.size() <= 0)
			return;
		
		//Set -> Iterator�� key�� �̾Ƽ� ���
		Set<String> setKey = bankMap.keySet();
		Iterator<String> iteKey = setKey.iterator();
		
		String key = "";	//Ű�� : ���¹�ȣ
		while(iteKey.hasNext())
		{
			key = iteKey.next();	//Ű�� ���
			Bankbook bo = bankMap.get(key);	//���¹�ȣ�� �˻��Ͽ� ���� ã�Ƽ� ��ȯ
			showUser(bo.getType(), bo.getName(), key, bo.getMoney(), bo.getRest());
		}//end while
	
	}//allShow() 
	
	// Ư������ ���
	private void showUser() {
		//���¹�ȣ �Է�
		String account = UI.getUI().nextString("���¹�ȣ >> ");
		// ��������
		if (BankingClient.getClient().getServer()) {
			BankingClient.getClient().send(Packet.ID.ONE,account);
			return;
		}
		// ã�� ���°� ���ٸ� ����
		if (bankMap.get(account) == null){
			UI.getUI().errorMsg(account + " ���´� ���� �����Դϴ�.");
			return;
		}
		
		Bankbook bo = bankMap.get(account);
		showUser(bo.getType(), bo.getName(), account, bo.getMoney(), bo.getRest());
	
	}
	
	//���»���
	private void delete()
	{
		//���¹�ȣ �Է�
		String account = UI.getUI().nextString("���¹�ȣ >> ");
		// ��������
		if (BankingClient.getClient().getServer()) {
			BankingClient.getClient().send(Packet.ID.DELETE, account);
			return;
		}
		
		// ã�� ���°� ���ٸ� ����
		if (bankMap.get(account) == null) {
			UI.getUI().errorMsg(account + " ���´� ���� �����Դϴ�.");
			return;
		}
		delete(account);
	}// delete()
	
//===============================================================

	//���� ����
	public void newUser(int iSpec, String sAcc, String name, int money, int interest) {
		// Ư�� ���� ����
		if (iSpec == SPECIAL) {
			bankMap.put(sAcc, new BankSpecial(name, money, interest));
			UI.getUI().ouputMsg("[ Ư������ ] ", name, " �� ���°��� �Ϸ�.   �ܾ� : ", String.valueOf(bankMap.get(sAcc).getMoney()),
					"��, ������ : ", String.valueOf(interest), "%");
		}
		// �Ϲ� ���� ����
		else {
			bankMap.put(sAcc, new BankNormal(name, money));
			UI.getUI().ouputMsg("[ �Ϲݰ��� ] ", name, " �� ���°��� �Ϸ�.  �ܾ� : ", String.valueOf(money), "��");
		}
	}

	//�ԱݿϷ� ������ ����
	public void addMoney(int type, String name, String sAcc, int money, int interest) {
		//Ŭ���̾�Ʈ ���¿� ������ ����
		if(bankMap.get(sAcc) == null){
			newUser(type, sAcc, name,money,interest);
		}
		
		Bankbook bo = bankMap.get(sAcc);
		bo.setMoney(money);
		// �ԱݿϷ� ��¹�
		if (type == SPECIAL)
			UI.getUI().ouputMsg("[ Ư������ ] :", name, "��] ", sAcc, " ���¿� �Ա� �Ϸ��Ͽ����ϴ�. �ܾ��� ", String.valueOf(money), " ��");
		else
			UI.getUI().ouputMsg("[ �Ϲݰ��� ] :", name, "��] ", sAcc, " ���¿� �Ա� �Ϸ��Ͽ����ϴ�. �ܾ��� ", String.valueOf(money), " ��");
	}

	// ��ݿϷ� ������ ���� 
	public void subMoney(int type, String name, String sAcc, int money, int interest) {
		//Ŭ���̾�Ʈ ���¿� ������ ����
		if(bankMap.get(sAcc) == null){
			newUser(type, sAcc, name,money,interest);
		}
		
		Bankbook bo = bankMap.get(sAcc);
		bo.setMoney(money);
		// �ԱݿϷ� ��¹�
		if (type == SPECIAL)
			UI.getUI().ouputMsg("[ Ư������ ] :", name, "��] ", sAcc, " ���¿� �Ա� �Ϸ��Ͽ����ϴ�. �ܾ��� ", String.valueOf(money), " ��");
		else
			UI.getUI().ouputMsg("[ �Ϲݰ��� ] :", name, "��] ", sAcc, " ���¿� �Ա� �Ϸ��Ͽ����ϴ�. �ܾ��� ", String.valueOf(money), " ��");
	}
	//��������
	public void delete(String sAcc){
		// ���� ����
		bankMap.remove(sAcc);
		UI.getUI().ouputMsg(sAcc + "���¸� ���� �Ͽ����ϴ�.");
	}
	
	//showUser()
	public void showUser(int type, String name, String sAcc, int money, int interest){
		// Ŭ���̾�Ʈ ���¿� ������ ����
		if (bankMap.get(sAcc) == null) {
			newUser(type, sAcc, name, money, interest);
		}

		Bankbook bo = bankMap.get(sAcc);
		// ���� ���� ���
		if (bo.getType() == SPECIAL)
			UI.getUI().ouputMsg("[ Ư������ ] :" , name, "��] ���¹�ȣ : ",sAcc + " , �ܾ� : " ,String.valueOf(money) , " , ������ : ", String.valueOf(interest), "%");
		else
			UI.getUI().ouputMsg("[ �Ϲݰ��� ] :" , name, "��] ���¹�ȣ : " , sAcc + " , �ܾ� : ", String.valueOf(money)) ;
	}
}
