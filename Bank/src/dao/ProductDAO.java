package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import dbConn.util.CloseHelper;
import dbConn.util.ConnectionHelper;
import dbConn.util.ConnectionSingletonHelper;
import model.ProductVO;

public class ProductDAO {

	// 상품정보
	public List<ProductVO> getProductInfo() {
		Connection conn = ConnectionHelper.getConnection("mysql");
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
				p.setProduct_type(rs.getInt("product_type"));
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
		Connection conn = ConnectionHelper.getConnection("mysql");
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
				p.setProduct_type(rs.getInt("product_type"));
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
	
	/**
	 * 어드민 상품 추가
	 * 
	 * @param Product
	 * @return true/false
	 */
	public boolean addPorduct(ProductVO product) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int rs = 0;
		boolean success = false;
		
		String sql = "INSERT INTO PRODUCT (product_name, product_type, interest_rate, period_months, max_deposit_amount, max_monthly_deposit) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		
		try {
			conn = ConnectionHelper.getConnection("mysql");
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, product.getProductName());
			pstmt.setInt(2, product.getProduct_type());
			pstmt.setBigDecimal(3, product.getInterestRate());
			pstmt.setInt(4, product.getPeriodMonths());
			pstmt.setBigDecimal(5, product.getMaxDepositAmount());
	        pstmt.setBigDecimal(6, product.getMaxMonthlyDeposit());
			rs = pstmt.executeUpdate();
			
			if (rs > 0) {
	            success = true;
	        }
			
			conn.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
			try {
				conn.rollback();
				System.out.println("[!] 트랜잭션 롤백되었습니다.");
			} catch (SQLException rollbackEx) {
				rollbackEx.printStackTrace();
			}
		} finally {
			try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
		}
		return success;
	}
	
	/**
	 * 어드민 상품 정보 수정
	 * 
	 * @param product
	 * @return true/false
	 */
	public boolean updateProduct(ProductVO product) {
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    int rs = 0;
	    boolean success = false;
	    
	    String sql = "UPDATE PRODUCT SET product_name = ?, interest_rate = ?, period_months = ?, max_deposit_amount = ?, max_monthly_deposit = ? " 
	            + "WHERE product_id = ?";
	    
	    try {
	        conn = ConnectionHelper.getConnection("mysql");
	        conn.setAutoCommit(false);
	        pstmt = conn.prepareStatement(sql);
	        
	        pstmt.setString(1, product.getProductName());
	        pstmt.setBigDecimal(2, product.getInterestRate());
	        pstmt.setInt(3, product.getPeriodMonths());
	        pstmt.setBigDecimal(4, product.getMaxDepositAmount());
	        pstmt.setBigDecimal(5, product.getMaxMonthlyDeposit());
	        pstmt.setInt(6, product.getProductId());     
	        
	        rs = pstmt.executeUpdate();
	        
	        if (rs > 0) {
	            success = true;
	        }
	        
	        conn.commit();
	    } catch (Exception e) {
	        e.printStackTrace();
	        success = false;
	        try {
	            conn.rollback();
	            System.out.println("[!] 트랜잭션 롤백되었습니다.");
	        } catch (SQLException rollbackEx) {
	            rollbackEx.printStackTrace();
	        }
	    } finally {
	        try {
	            if (pstmt != null) pstmt.close();
	            if (conn != null) conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    return success;
	}

	/**
	 * 어드민 상품 삭제
	 * 
	 * @param productId
	 * @return true/false
	 */
	public boolean deleteProduct(int productId) {
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    int rs = 0;
	    boolean success = false;
	    
	    String sql = "DELETE FROM PRODUCT WHERE product_id = ?";
	    
	    try {
	        conn = ConnectionHelper.getConnection("mysql");
	        conn.setAutoCommit(false);
	        pstmt = conn.prepareStatement(sql);
	        
	        pstmt.setInt(1, productId);
	        
	        rs = pstmt.executeUpdate();
	        
	        if (rs > 0) {
	            success = true;
	        }
	        
	        conn.commit();
	    } catch (Exception e) {
	        e.printStackTrace();
	        success = false;
	        try {
	            conn.rollback();
	            System.out.println("[!] 트랜잭션 롤백되었습니다.");
	        } catch (SQLException rollbackEx) {
	            rollbackEx.printStackTrace();
	        }
	    } finally {
	        try {
	            if (pstmt != null) pstmt.close();
	            if (conn != null) conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    return success;
	}

	public List<ProductVO> getProductByType(int product_type) {
		Connection conn = ConnectionHelper.getConnection("mysql");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT * FROM PRODUCT where product_type = ?";
		
		List<ProductVO> list = new ArrayList<ProductVO>();
		ProductVO p = new ProductVO();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, product_type);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				p.setProductId(rs.getInt("product_id"));
				p.setProductName(rs.getString("product_name"));
				p.setProduct_type(rs.getInt("product_type"));
				p.setInterestRate(rs.getBigDecimal("interest_rate"));
				p.setPeriodMonths(rs.getInt("period_months"));
				list.add(p);
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
		return list;
	}

}
