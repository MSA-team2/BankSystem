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
	// 하나의 프로세스에서 공통으로 사용할 수 있는 공용자원(static)
	private static Connection conn;

	// default constructor
	private ConnectionSingletonHelper() {

	}

	public static Connection getConnection(String dsn) {

		if (conn != null) {
			return conn;
		}

		try {
			if (dsn.equals("mysql")) {

				// 1. Driver Load.. exception
				Class.forName("com.mysql.cj.jdbc.Driver");

				// 2. Connection & Open
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "1234");

			}

			if (dsn.equals("oracle")) {
				// 1. Driver Load.. exception
				Class.forName("oracle.jdbc.OracleDriver");

				// 2. Connection & Open
				conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "root", "1234");

				System.out.println("Oracle Connection Success");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return conn;
		}
	}

	public static void close() throws SQLException {
		if (conn != null) {
			if (!conn.isClosed()) {
				conn.close();

			}
		}
	}
}
