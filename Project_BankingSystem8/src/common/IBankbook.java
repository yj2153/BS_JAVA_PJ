package common;

public interface IBankbook {
	int NULL = -1;
	
	// 		�Ϲ� ���� , 		Ư�� ����
	int NORMAL = 1, SPECIAL = 2;
	// 								���»���,		 �Ա�, 		���,			 ��ü���, 	�˻����, 		����
	int CREATE = 1, ADD = 2, SUB = 3, ALL = 4, ONE = 5, DELETE = 6, EXIT = 7;
	
	//�����
	double PERCENTAGE  = 100.0;
}
