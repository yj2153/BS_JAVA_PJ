package network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Iterator;

import common.UI;
import main.Manager;

public class BankingClient {
	
	private static BankingClient bc = null;

	private Socket sock = null;
	private ObjectInputStream oisServer = null;
	private ObjectOutputStream oosServer = null;

	//서버 접속 여부
	private boolean bServer;
	
	private BankingClient() {
		bServer = true;
	}
	//객체반환
	public static BankingClient getClient() {
		if (bc == null)
			bc = new BankingClient();
		
		return bc;
	}
	
	public boolean getServer(){ return bServer; }

	// 서버와 접속
	private void connect() {
		try {
			sock = new Socket("127.0.0.1", Integer.parseInt("8020"));
			OutputStream toServer = sock.getOutputStream();
			InputStream fromServer = sock.getInputStream();

			oisServer = new ObjectInputStream(fromServer);
			oosServer = new ObjectOutputStream(toServer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UI.getUI().ouputMsg(sock + ": 연결됨");
	}
	// 연결 종료
	private void end() {
		try {
			if (oosServer != null)				oosServer.close();
			if (oisServer != null)				oisServer.close();
			if (sock != null)				sock.close();
		} catch (IOException e) {			e.printStackTrace();		}
	}// end

	//서버로 전송
	private void writeObject(RequestMsg msg) {
		connect();
		try {
			oosServer.writeObject(msg);
			oosServer.flush();
		} catch (Exception ex) {
			UI.getUI().ouputMsg("fail send create : " + ex.toString());
		}
		onMessage();
	}

	// ====================================================================================================
	//서버에서 받은 메세지
	private void onMessage() {
		try {
			ResponseMsg msg = (ResponseMsg) oisServer.readObject();

			switch (msg.getResCode()) {
			case CREATE: //계좌 생성
				receive((CreateResMsg) msg);
				break;
			case ADD: //계좌입금
				receive((AddResMsg) msg);
				break;
			case SUB:  //계좌출금
				receive((SubResMsg) msg);
				break;
			case ALL: //전체계좌
				receive((AllResMsg) msg);
				break;
			case ONE: //계좌하나
				receive((OneResMsg) msg);
				break;
			case DELETE: //계좌해지
				receive((DeleteResMsg) msg);
				break;
			case ERROR: //에러
				receive((ErrorResMsg) msg);
				break;
			default:
				UI.getUI().errorMsg("fail receive default");
				break;
			}//end switch
		}//end try
		catch (Exception e) {
			// TODO Auto-generated catch block
			UI.getUI().ouputMsg("fail onMessage : " + e.toString());
		}//end catch
		end();
	}// connect

	// ====================================================================================================

	// 생성 ( 이름, 초기금액, 통장종류
	public void send(Packet.ID id, String name, String iAcc, int firstMoeny, int type, int dInterest) {
		CreateReqMsg msg = new CreateReqMsg();
		msg.setReqBunho(id);					//요청번호
		msg.setAccNum(iAcc);					//계좌번호
		msg.setStrUserName(name);		//계좌명
		msg.setMoney(firstMoeny);			//계좌금액
		msg.setReqType(type);				//계좌 타입
		msg.setdInterest(dInterest);		//계좌 이자율
		
		writeObject(msg);	//서버 전송
	}
	//입금, 출금( 요청번호, 계좌번호, 돈)
	public void send(Packet.ID id, String sAcc, int money) {
		if (id.equals(Packet.ID.ADD)) {
			AddReqMsg msg = new AddReqMsg();
			msg.setReqBunho(id); // 요청번호
			msg.setAccNum(sAcc);// 계좌번호
			msg.setMoney(money); // 돈
			writeObject(msg); // 서버전송
		}else
		{
			SubReqMsg msg = new SubReqMsg();
			msg.setReqBunho(id); //요청번호
			msg.setAccNum(sAcc); //계좌번호
			msg.setMoney(money); //돈
			writeObject(msg); //서버전송
		}
	}
	//전체
	public void send(Packet.ID id) {
		AllReqMsg msg = new AllReqMsg();
		msg.setReqBunho(id);
		msg.setAccNum("0");
		writeObject(msg); // 서버전송
	}
	//하나출력, 해지
	public void send(Packet.ID id, String strAcc){
		if(id.equals(Packet.ID.ONE)){
			OneReqMsg msg = new OneReqMsg();
			msg.setReqBunho(id);
			msg.setAccNum(strAcc);
			writeObject(msg); //서버전송
		}
		//해지
		else{
		AllReqMsg msg = new AllReqMsg();
		msg.setReqBunho(id);
		msg.setAccNum(strAcc);
		writeObject(msg); //서버전송
		}
	}
	// ====================================================================================================
//생성완료 메세지
	private void receive(CreateResMsg msg) {

		UI.getUI().ouputMsg(msg.getResCode() + " :응답번호");
		UI.getUI().ouputMsg(msg.getAccNum() + " :계좌번호");
		UI.getUI().ouputMsg(msg.getAccType() + " :계좌타입");
		UI.getUI().ouputMsg(msg.getdInterest() + " :계좌이자율");
		UI.getUI().ouputMsg(msg.getMoney() + " :계좌금액");
		UI.getUI().ouputMsg(msg.getStrUserName() + " :계좌명");
		UI.getUI().ouputMsg("계좌생성");
		Manager.getMgr().newUser(msg.getAccType(), msg.getAccNum(), msg.getStrUserName(), msg.getMoney(), msg.getdInterest());
	}
	//입금 완료 메세지
	private void receive(AddResMsg msg){
		UI.getUI().ouputMsg(msg.getResCode() + " : 응답번호");
		UI.getUI().ouputMsg(msg.getAccNum() + " : 계좌번호");
		UI.getUI().ouputMsg(msg.getMoney() + " : 잔액");
		UI.getUI().ouputMsg("입금완료");
		Manager.getMgr().addMoney(msg.getAccType(), msg.getStrUserName(), msg.getAccNum(), msg.getMoney(), msg.getdInterest());
	}
	//출금완료 메세지
	private void receive(SubResMsg msg){
		UI.getUI().ouputMsg(msg.getResCode() + " : 응답번호");
		UI.getUI().ouputMsg(msg.getAccNum() + " : 계좌번호");
		UI.getUI().ouputMsg(msg.getMoney() + " : 잔액");
		UI.getUI().ouputMsg("출금완료");
		Manager.getMgr().subMoney(msg.getAccType(), msg.getStrUserName(), msg.getAccNum(), msg.getMoney(), msg.getdInterest());
	}
	//전체 출력 완료
	private void receive(AllResMsg msg){
		UI.getUI().ouputMsg(msg.getResCode() + " : 응답번호");
		UI.getUI().ouputMsg("전체출력완료");
		Iterator<AllResMsg> IA = msg.getList().iterator();
		while(IA.hasNext())
		{
			AllResMsg a = IA.next();
			Manager.getMgr().showUser(a.getAccType(), a.getStrUserName(), a.getAccNum(), a.getMoney(), a.getdInterest());
		}
		
	}
	//하나 출력완료
	private void receive(OneResMsg msg){
		UI.getUI().ouputMsg(msg.getResCode() + " : 응답번호");
		UI.getUI().ouputMsg(msg.getAccNum() + " : 계좌번호");
		UI.getUI().ouputMsg("하나 출력완료");
		Manager.getMgr().showUser(msg.getAccType(), msg.getStrUserName(), msg.getAccNum(), msg.getMoney(), msg.getdInterest());
		
	}
	//해지완료 메세지
	private void receive(DeleteResMsg msg){
		UI.getUI().ouputMsg(msg.getResCode() + " : 응답번호");
		UI.getUI().ouputMsg(msg.getAccNum() + " : 계좌번호");
		UI.getUI().ouputMsg("해지완료");
		Manager.getMgr().delete(msg.getAccNum());
	}
	
	//에러
	private void receive(ErrorResMsg msg){
		UI.getUI().ouputMsg(msg.getResCode() + " : 응답번호");
		UI.getUI().ouputMsg(msg.getAccNum() + " : 계좌번호");
		UI.getUI().ouputMsg(msg.getErrorMsg() + " : 에러");
	}
}
