package dbConn.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ConnectionHelper 클래스의 문제 1) 매번 드라이버 로드 2) 매번 커넥션 생성...
 * 
 * 어차피 하나의 프로세스에서 하나만 만들어서 재사용하자! => 공유자원(싱글톤)
 */
public class ConnectionSingletonHelper {

	public static Connection getConnection(String dsn) {
		try {
			
		if (dsn.equals("mysql")) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "mysql");
        }

        if (dsn.equals("oracle")) {
            Class.forName("oracle.jdbc.OracleDriver");
            return DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "root", "1234");
        }

		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("지원하지 않는 DB 타입입니다.");
	}
}
