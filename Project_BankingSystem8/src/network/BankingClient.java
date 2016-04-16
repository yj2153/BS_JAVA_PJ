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

	//���� ���� ����
	private boolean bServer;
	
	private BankingClient() {
		bServer = true;
	}
	//��ü��ȯ
	public static BankingClient getClient() {
		if (bc == null)
			bc = new BankingClient();
		
		return bc;
	}
	
	public boolean getServer(){ return bServer; }

	// ������ ����
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
		UI.getUI().ouputMsg(sock + ": �����");
	}
	// ���� ����
	private void end() {
		try {
			if (oosServer != null)				oosServer.close();
			if (oisServer != null)				oisServer.close();
			if (sock != null)				sock.close();
		} catch (IOException e) {			e.printStackTrace();		}
	}// end

	//������ ����
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
	//�������� ���� �޼���
	private void onMessage() {
		try {
			ResponseMsg msg = (ResponseMsg) oisServer.readObject();

			switch (msg.getResCode()) {
			case CREATE: //���� ����
				receive((CreateResMsg) msg);
				break;
			case ADD: //�����Ա�
				receive((AddResMsg) msg);
				break;
			case SUB:  //�������
				receive((SubResMsg) msg);
				break;
			case ALL: //��ü����
				receive((AllResMsg) msg);
				break;
			case ONE: //�����ϳ�
				receive((OneResMsg) msg);
				break;
			case DELETE: //��������
				receive((DeleteResMsg) msg);
				break;
			case ERROR: //����
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

	// ���� ( �̸�, �ʱ�ݾ�, ��������
	public void send(Packet.ID id, String name, String iAcc, int firstMoeny, int type, int dInterest) {
		CreateReqMsg msg = new CreateReqMsg();
		msg.setReqBunho(id);					//��û��ȣ
		msg.setAccNum(iAcc);					//���¹�ȣ
		msg.setStrUserName(name);		//���¸�
		msg.setMoney(firstMoeny);			//���±ݾ�
		msg.setReqType(type);				//���� Ÿ��
		msg.setdInterest(dInterest);		//���� ������
		
		writeObject(msg);	//���� ����
	}
	//�Ա�, ���( ��û��ȣ, ���¹�ȣ, ��)
	public void send(Packet.ID id, String sAcc, int money) {
		if (id.equals(Packet.ID.ADD)) {
			AddReqMsg msg = new AddReqMsg();
			msg.setReqBunho(id); // ��û��ȣ
			msg.setAccNum(sAcc);// ���¹�ȣ
			msg.setMoney(money); // ��
			writeObject(msg); // ��������
		}else
		{
			SubReqMsg msg = new SubReqMsg();
			msg.setReqBunho(id); //��û��ȣ
			msg.setAccNum(sAcc); //���¹�ȣ
			msg.setMoney(money); //��
			writeObject(msg); //��������
		}
	}
	//��ü
	public void send(Packet.ID id) {
		AllReqMsg msg = new AllReqMsg();
		msg.setReqBunho(id);
		msg.setAccNum("0");
		writeObject(msg); // ��������
	}
	//�ϳ����, ����
	public void send(Packet.ID id, String strAcc){
		if(id.equals(Packet.ID.ONE)){
			OneReqMsg msg = new OneReqMsg();
			msg.setReqBunho(id);
			msg.setAccNum(strAcc);
			writeObject(msg); //��������
		}
		//����
		else{
		AllReqMsg msg = new AllReqMsg();
		msg.setReqBunho(id);
		msg.setAccNum(strAcc);
		writeObject(msg); //��������
		}
	}
	// ====================================================================================================
//�����Ϸ� �޼���
	private void receive(CreateResMsg msg) {

		UI.getUI().ouputMsg(msg.getResCode() + " :�����ȣ");
		UI.getUI().ouputMsg(msg.getAccNum() + " :���¹�ȣ");
		UI.getUI().ouputMsg(msg.getAccType() + " :����Ÿ��");
		UI.getUI().ouputMsg(msg.getdInterest() + " :����������");
		UI.getUI().ouputMsg(msg.getMoney() + " :���±ݾ�");
		UI.getUI().ouputMsg(msg.getStrUserName() + " :���¸�");
		UI.getUI().ouputMsg("���»���");
		Manager.getMgr().newUser(msg.getAccType(), msg.getAccNum(), msg.getStrUserName(), msg.getMoney(), msg.getdInterest());
	}
	//�Ա� �Ϸ� �޼���
	private void receive(AddResMsg msg){
		UI.getUI().ouputMsg(msg.getResCode() + " : �����ȣ");
		UI.getUI().ouputMsg(msg.getAccNum() + " : ���¹�ȣ");
		UI.getUI().ouputMsg(msg.getMoney() + " : �ܾ�");
		UI.getUI().ouputMsg("�ԱݿϷ�");
		Manager.getMgr().addMoney(msg.getAccType(), msg.getStrUserName(), msg.getAccNum(), msg.getMoney(), msg.getdInterest());
	}
	//��ݿϷ� �޼���
	private void receive(SubResMsg msg){
		UI.getUI().ouputMsg(msg.getResCode() + " : �����ȣ");
		UI.getUI().ouputMsg(msg.getAccNum() + " : ���¹�ȣ");
		UI.getUI().ouputMsg(msg.getMoney() + " : �ܾ�");
		UI.getUI().ouputMsg("��ݿϷ�");
		Manager.getMgr().subMoney(msg.getAccType(), msg.getStrUserName(), msg.getAccNum(), msg.getMoney(), msg.getdInterest());
	}
	//��ü ��� �Ϸ�
	private void receive(AllResMsg msg){
		UI.getUI().ouputMsg(msg.getResCode() + " : �����ȣ");
		UI.getUI().ouputMsg("��ü��¿Ϸ�");
		Iterator<AllResMsg> IA = msg.getList().iterator();
		while(IA.hasNext())
		{
			AllResMsg a = IA.next();
			Manager.getMgr().showUser(a.getAccType(), a.getStrUserName(), a.getAccNum(), a.getMoney(), a.getdInterest());
		}
		
	}
	//�ϳ� ��¿Ϸ�
	private void receive(OneResMsg msg){
		UI.getUI().ouputMsg(msg.getResCode() + " : �����ȣ");
		UI.getUI().ouputMsg(msg.getAccNum() + " : ���¹�ȣ");
		UI.getUI().ouputMsg("�ϳ� ��¿Ϸ�");
		Manager.getMgr().showUser(msg.getAccType(), msg.getStrUserName(), msg.getAccNum(), msg.getMoney(), msg.getdInterest());
		
	}
	//�����Ϸ� �޼���
	private void receive(DeleteResMsg msg){
		UI.getUI().ouputMsg(msg.getResCode() + " : �����ȣ");
		UI.getUI().ouputMsg(msg.getAccNum() + " : ���¹�ȣ");
		UI.getUI().ouputMsg("�����Ϸ�");
		Manager.getMgr().delete(msg.getAccNum());
	}
	
	//����
	private void receive(ErrorResMsg msg){
		UI.getUI().ouputMsg(msg.getResCode() + " : �����ȣ");
		UI.getUI().ouputMsg(msg.getAccNum() + " : ���¹�ȣ");
		UI.getUI().ouputMsg(msg.getErrorMsg() + " : ����");
	}
}
