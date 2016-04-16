package network;

import java.io.Serializable;
import java.util.LinkedList;

public interface Packet {
	enum ID{
		ERROR,
		LOGIN, CREATE, ADD, SUB, ALL, ONE, DELETE
	}
	enum TYPE{
		NULL, NORMAL, SPECIAL
	}
}

//Request
//��û�޼��� : ��û��ȣ,���¹�ȣ,�ݾ�
class RequestMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	private Packet.ID reqBunho; // ��û��ȣ
	private String accNum; // ���¹�ȣ
	
	public Packet.ID getReqBunho() {		return reqBunho;	}
	public void setReqBunho(Packet.ID reqBunho) {		this.reqBunho = reqBunho;	}
	public String getAccNum() 				{		return accNum;	}
	public void setAccNum(String accNum)		 				{		this.accNum = accNum;	}
}

//���� ����
class CreateReqMsg extends RequestMsg{
	private static final long serialVersionUID = 1L;
	private int reqType; // ����Ÿ��
	private String strUserName; // ������ �̸�
	private int dInterest; // ������
	private int money; // �ݾ�
	
	public int getReqType()			 		{		return reqType;	}
	public String getStrUserName() 	{		return strUserName;	}
	public int getdInterest() 					{		return dInterest;	}
	public int getMoney() 						{		return money;	}
	public void setMoney(int money) 									{		this.money = money;	}
	public void setReqType(int reqType) 							{		this.reqType = reqType;	}
	public void setStrUserName(String strUserName) 	{		this.strUserName = strUserName;	}
	public void setdInterest(int dInterest) 							{		this.dInterest = dInterest;	}
}

//���� �Ա�
class AddReqMsg extends RequestMsg {
	private static final long serialVersionUID = 1L;
	private int money; // �ݾ�
	
	public int getMoney() 						{		return money;	}
	public void setMoney(int money) 									{		this.money = money;	}
}
//���� ���
class SubReqMsg extends RequestMsg {
	private static final long serialVersionUID = 1L;
	private int money; // �ݾ�\
	
	public int getMoney() 						{		return money;	}
	public void setMoney(int money) 									{		this.money = money;	}
}

//���� ��ü ���
class AllReqMsg extends RequestMsg {
	private static final long serialVersionUID = 1L;
}
//���� �ϳ� ���
class OneReqMsg extends RequestMsg {
	private static final long serialVersionUID = 1L;
}
//���� ����
class DeleteReqMsg extends RequestMsg {
	private static final long serialVersionUID = 1L;
}

//===========================================================
//ResponseMsg
//���� ����޼��� Ŭ����
class ResponseMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	// �����ڵ�
	private Packet.ID resCode;
	private String accNum; // ���¹�ȣ
	private int accType; // ����Ÿ��
	private String strUserName; // ������ �̸�
	private int money; // �ݾ�
	private int dInterest; // ������
	
	public int getAccType() 						{		return accType;	}
	public String getStrUserName() 		{		return strUserName;	}
	public int getMoney() 							{		return money;	}
	public int getdInterest() 						{		return dInterest;	}
	
	public void setAddType(int reqType) 							{		this.accType = reqType;	}
	public void setStrUserName(String strUserName) 	{		this.strUserName = strUserName;	}
	public void setMoney(int money) 									{		this.money = money;	}
	public void setdInterest(int dInterest) 							{		this.dInterest = dInterest;	}

	public String getAccNum() 					{		return accNum;	}
	public void setAccNum(String accNum) 						{		this.accNum = accNum;	}
	
	public Packet.ID getResCode() {		return resCode;	}
	public void setResCode(Packet.ID code)	{		resCode =code;	}
}

//���� ����
class CreateResMsg extends ResponseMsg{
	private static final long serialVersionUID = 1L;
}
//���� �Ա�
class AddResMsg extends ResponseMsg {
	private static final long serialVersionUID = 1L;
}
//���� ���
class SubResMsg extends ResponseMsg {
	private static final long serialVersionUID = 1L;
}

//���� ��ü ���
class AllResMsg extends ResponseMsg {
	private static final long serialVersionUID = 1L;
	private LinkedList<AllResMsg> list;
	public LinkedList<AllResMsg> getList() {
		return list;
	}
	public void setList(LinkedList<AllResMsg> list) {
		this.list = list;
	}
}
//���� �ϳ� ���
class OneResMsg extends ResponseMsg {
	private static final long serialVersionUID = 1L;
}
//���� ����
class DeleteResMsg extends ResponseMsg {
	private static final long serialVersionUID = 1L;
}
//���� ����
class ErrorResMsg extends ResponseMsg{
	private static final long serialVersionUID = 1L;
	private String errorMsg;
	public void setErrorMsg(String msg) { errorMsg = msg; }
	public String getErrorMsg() { return errorMsg; }
}