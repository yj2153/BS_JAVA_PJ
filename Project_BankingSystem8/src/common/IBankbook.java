package common;

public interface IBankbook {
	int NULL = -1;
	
	// 		일반 계좌 , 		특별 계좌
	int NORMAL = 1, SPECIAL = 2;
	// 								계좌생성,		 입금, 		출금,			 전체출력, 	검색출력, 		종료
	int CREATE = 1, ADD = 2, SUB = 3, ALL = 4, ONE = 5, DELETE = 6, EXIT = 7;
	
	//백분율
	double PERCENTAGE  = 100.0;
}
