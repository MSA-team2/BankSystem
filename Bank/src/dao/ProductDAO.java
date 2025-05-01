package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import dbConn.util.CloseHelper;
import dbConn.util.ConnectionSingletonHelper;
import model.ProductVO;

public class ProductDAO {

	// 상품정보
	public List<ProductVO> getProductInfo() {
		Connection conn = ConnectionSingletonHelper.getConnection("mysql");
		Statement stmt = null;
		ResultSet rs = null;
		
		List<ProductVO> list = new ArrayList<ProductVO>();
		String sql = "SELECT * FROM PRODUCT";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				ProductVO p = new ProductVO();
				p.setProductId(rs.getInt("product_id"));
				p.setProductName(rs.getString("product_name"));
				p.setInterestRate(rs.getBigDecimal("interest_rate"));
				p.setPeriodMonths(rs.getInt("period_months"));
				list.add(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public ProductVO getProductById(int productId) {
		Connection conn = ConnectionSingletonHelper.getConnection("mysql");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT * FROM PRODUCT where product_id = ?";
		
		ProductVO p = new ProductVO();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, productId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				p.setProductId(rs.getInt("product_id"));
				p.setProductName(rs.getString("product_name"));
				p.setInterestRate(rs.getBigDecimal("interest_rate"));
				p.setPeriodMonths(rs.getInt("period_months"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (conn != null)
					conn.rollback();
			} catch (Exception rollbackEx) {
				rollbackEx.printStackTrace();
			}
		} finally {
			CloseHelper.close(pstmt);
			CloseHelper.close(conn);
		}
		return p;
	}
}
