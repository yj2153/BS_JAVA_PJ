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
//요청메세지 : 요청번호,계좌번호,금액
class RequestMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	private Packet.ID reqBunho; // 요청번호
	private String accNum; // 계좌번호
	
	public Packet.ID getReqBunho() {		return reqBunho;	}
	public void setReqBunho(Packet.ID reqBunho) {		this.reqBunho = reqBunho;	}
	public String getAccNum() 				{		return accNum;	}
	public void setAccNum(String accNum)		 				{		this.accNum = accNum;	}
}

//계좌 생성
class CreateReqMsg extends RequestMsg{
	private static final long serialVersionUID = 1L;
	private int reqType; // 통장타입
	private String strUserName; // 개설자 이름
	private int dInterest; // 이자율
	private int money; // 금액
	
	public int getReqType()			 		{		return reqType;	}
	public String getStrUserName() 	{		return strUserName;	}
	public int getdInterest() 					{		return dInterest;	}
	public int getMoney() 						{		return money;	}
	public void setMoney(int money) 									{		this.money = money;	}
	public void setReqType(int reqType) 							{		this.reqType = reqType;	}
	public void setStrUserName(String strUserName) 	{		this.strUserName = strUserName;	}
	public void setdInterest(int dInterest) 							{		this.dInterest = dInterest;	}
}

//계좌 입금
class AddReqMsg extends RequestMsg {
	private static final long serialVersionUID = 1L;
	private int money; // 금액
	
	public int getMoney() 						{		return money;	}
	public void setMoney(int money) 									{		this.money = money;	}
}
//계좌 출금
class SubReqMsg extends RequestMsg {
	private static final long serialVersionUID = 1L;
	private int money; // 금액\
	
	public int getMoney() 						{		return money;	}
	public void setMoney(int money) 									{		this.money = money;	}
}

//계좌 전체 출력
class AllReqMsg extends RequestMsg {
	private static final long serialVersionUID = 1L;
}
//계좌 하나 출력
class OneReqMsg extends RequestMsg {
	private static final long serialVersionUID = 1L;
}
//계좌 해지
class DeleteReqMsg extends RequestMsg {
	private static final long serialVersionUID = 1L;
}

//===========================================================
//ResponseMsg
//전송 응답메세지 클래스
class ResponseMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	// 응답코드
	private Packet.ID resCode;
	private String accNum; // 계좌번호
	private int accType; // 통장타입
	private String strUserName; // 개설자 이름
	private int money; // 금액
	private int dInterest; // 이자율
	
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

//계좌 생성
class CreateResMsg extends ResponseMsg{
	private static final long serialVersionUID = 1L;
}
//계좌 입금
class AddResMsg extends ResponseMsg {
	private static final long serialVersionUID = 1L;
}
//계좌 출금
class SubResMsg extends ResponseMsg {
	private static final long serialVersionUID = 1L;
}

//계좌 전체 출력
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
//계좌 하나 출력
class OneResMsg extends ResponseMsg {
	private static final long serialVersionUID = 1L;
}
//계좌 해지
class DeleteResMsg extends ResponseMsg {
	private static final long serialVersionUID = 1L;
}
//에러 정보
class ErrorResMsg extends ResponseMsg{
	private static final long serialVersionUID = 1L;
	private String errorMsg;
	public void setErrorMsg(String msg) { errorMsg = msg; }
	public String getErrorMsg() { return errorMsg; }
}