package dbConn.util;

import java.sql.Connection;
import java.sql.DriverManager;

/*
  DB 연결 정보 반복적인 코딩을 해결하기 위해
  다른 클래스에서 아래 코드 구현을 하지 않도록 설계
  
  Class.forName("");
  Connection conn = DriverManager.getConnection("","","");
  이런식으로 사용
  
  ConnectionHelper.getConnection("mysql") or ("oracle") or .... 
  dsn(data source name)
 */

public class ConnectionHelper {

	// method(접근지정자 : public static) : 연결에 대한 것은 바로 불러서 사용하기 위해서
	public static Connection getConnection(String dsn) {

        Connection conn = null;

        try {
            if (dsn.equals("mysql")) {
                Class.forName("com.mysql.cj.jdbc.Driver"); // 드라이버 이름 최신 버전
                conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/bankDB", "root", "1234");
            } else if (dsn.equals("oracle")) {
                Class.forName("oracle.jdbc.OracleDriver");
                conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "root", "1234");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return conn;
        }
}

public static Connection getConnection(String dsn, String userid, String pwd) {
    Connection conn = null;

    try {
        if (dsn.equals("mysql")) {
            Class.forName("com.mysql.cj.jdbc.Driver"); // 드라이버 이름 최신 버전
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bankDB", "root", "1234"); 
        } else if (dsn.equals("oracle")) {
            Class.forName("oracle.jdbc.OracleDriver");
            conn = DriverManager.getConnection(
                    "jdbc:oracle:thin:@127.0.0.1:1521:xe", userid, pwd);
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        return conn;
    }
}

}
