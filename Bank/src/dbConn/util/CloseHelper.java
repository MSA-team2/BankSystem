package dbConn.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/*
 		if(pstmt!=null) pstmt.close();
 		if(stmt != null) stmt.close();
 		if(conn != null) conn.close();
 		if( rs != null) rs.close();

 */
public class CloseHelper {

	public static void close(Connection conn) {

		if (conn != null) {
			try {
				conn.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static void close(PreparedStatement pstmt) {
		if (pstmt != null) {
			try {
				pstmt.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void close(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
