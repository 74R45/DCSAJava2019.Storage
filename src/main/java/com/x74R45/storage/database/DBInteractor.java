package com.x74R45.storage.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.x74R45.storage.server.Category;
import com.x74R45.storage.server.Product;

public class DBInteractor {

	public static boolean checkUser(String login, String password) throws SQLException {
		PreparedStatement st = DBConnector.getConnection()
				.prepareStatement("SELECT * FROM user WHERE user_login = ? AND user_password = ?;");
		st.setString(1, login);
		st.setString(2, password);
		ResultSet rs = st.executeQuery();
		
		boolean res = false;
		if (rs.next())
			res = true;
		
		st.close();
		rs.close();
		return res;
	}
	
	public static ArrayList<Product> getAllProducts() throws SQLException {
		PreparedStatement st = DBConnector.getConnection()
				.prepareStatement("SELECT * FROM product");
		ResultSet rs = st.executeQuery();
		
		ArrayList<Product> res = new ArrayList<>();
		while (rs.next()) {
			Product pr = new Product(
					rs.getInt("product_id"),
					rs.getString("product_name"),
					rs.getString("product_description"),
					rs.getString("product_maker"),
					rs.getInt("product_amount"),
					rs.getBigDecimal("product_price"),
					rs.getInt("category_id"));
			res.add(pr);
		}
		st.close();
		rs.close();
		return res;
	}

	public static void changeProduct(Product pr) throws SQLException {
		PreparedStatement st = DBConnector.getConnection()
				.prepareStatement("SELECT * FROM product WHERE product_id = ?");
		st.setInt(1, pr.getId());
		ResultSet rs = st.executeQuery();
		if (!rs.next()) {
			st.close();
			rs.close();
			throw new SQLException();
		}
		st.close();
		rs.close();
		st = DBConnector.getConnection()
				.prepareStatement("UPDATE product SET product_name = ?, product_description = ?, "
						+ "product_maker = ?, product_amount = ?, product_price = ?, category_id = ? WHERE product_id = ?");
		st.setString(1, pr.getName());
		st.setString(2, pr.getDescription());
		st.setString(3, pr.getMaker());
		st.setInt(4, pr.getAmount());
		st.setBigDecimal(5, pr.getPrice());
		st.setInt(6, pr.getCategoryId());
		st.setInt(7, pr.getId());
		st.executeUpdate();
		st.close();
	}
	
	public static int createProduct(Product pr) throws SQLException {
		PreparedStatement st = DBConnector.getConnection()
				.prepareStatement("INSERT INTO product (product_name, product_description, "
						+ "product_maker, product_amount, product_price, category_id)"
						+ "VALUES (?, ?, ?, ?, ?, ?)");
		st.setString(1, pr.getName());
		st.setString(2, pr.getDescription());
		st.setString(3, pr.getMaker());
		st.setInt(4, pr.getAmount());
		st.setBigDecimal(5, pr.getPrice());
		st.setInt(6, pr.getCategoryId());
		st.executeUpdate();
		st.close();
		
		st = DBConnector.getConnection()
				.prepareStatement("SELECT * FROM product WHERE product_name = ?");
		st.setString(1, pr.getName());
		ResultSet rs = st.executeQuery();
		if (rs.next()) {
			int res = rs.getInt("product_id");
			rs.close();
			st.close();
			return res;
		} else {
			rs.close();
			st.close();
			throw new SQLException();
		}
	}

	public static void deleteProduct(int id) throws SQLException {
		PreparedStatement st = DBConnector.getConnection()
				.prepareStatement("SELECT * FROM product WHERE product_id = ?");
		st.setInt(1, id);
		ResultSet rs = st.executeQuery();
		if (!rs.next()) {
			st.close();
			rs.close();
			throw new SQLException();
		}
		rs.close();
		st.close();
		
		st = DBConnector.getConnection()
				.prepareStatement("DELETE FROM product WHERE product_id = ?");
		st.setInt(1, id);
		st.executeUpdate();
		st.close();
	}

	public static ArrayList<Category> getAllCategories() throws SQLException {
		PreparedStatement st = DBConnector.getConnection().prepareStatement(
				"SELECT category.category_id, category_name, category_description, " + 
				"COALESCE(SUM(product_amount * product_price), 0) AS category_total_price " +
				"FROM category LEFT JOIN product ON category.category_id = product.category_id " + 
				"GROUP BY category.category_id");
		ResultSet rs = st.executeQuery();
		
		ArrayList<Category> res = new ArrayList<>();
		while (rs.next()) {
			Category pr = new Category(
					rs.getInt("category_id"),
					rs.getString("category_name"),
					rs.getString("category_description"),
					rs.getBigDecimal("category_total_price"));
			res.add(pr);
		}
		st.close();
		rs.close();
		return res;
	}

	public static void changeCategory(Category c) throws SQLException {
		PreparedStatement st = DBConnector.getConnection()
				.prepareStatement("SELECT * FROM category WHERE category_id = ?");
		st.setInt(1, c.getId());
		ResultSet rs = st.executeQuery();
		if (!rs.next()) {
			st.close();
			rs.close();
			throw new SQLException();
		}
		st.close();
		rs.close();
		st = DBConnector.getConnection()
				.prepareStatement("UPDATE category SET category_name = ?, category_description = ? "
						+ "WHERE category_id = ?");
		st.setString(1, c.getName());
		st.setString(2, c.getDescription());
		st.setInt(3, c.getId());
		st.executeUpdate();
		st.close();
	}

	public static int createCategory(Category c) throws SQLException {
		PreparedStatement st = DBConnector.getConnection()
				.prepareStatement("INSERT INTO category (category_name, category_description) "
						+ "VALUES (?, ?)");
		st.setString(1, c.getName());
		st.setString(2, c.getDescription());
		st.executeUpdate();
		st.close();
		
		st = DBConnector.getConnection()
				.prepareStatement("SELECT * FROM category WHERE category_name = ?");
		st.setString(1, c.getName());
		ResultSet rs = st.executeQuery();
		if (rs.next()) {
			int res = rs.getInt("category_id");
			rs.close();
			st.close();
			return res;
		} else {
			rs.close();
			st.close();
			throw new SQLException();
		}
	}

	public static void deleteCategory(int id) throws SQLException {
		PreparedStatement st = DBConnector.getConnection()
				.prepareStatement("SELECT * FROM category WHERE category_id = ?");
		st.setInt(1, id);
		ResultSet rs = st.executeQuery();
		if (!rs.next()) {
			st.close();
			rs.close();
			throw new SQLException();
		}
		rs.close();
		st.close();
		
		st = DBConnector.getConnection()
				.prepareStatement("DELETE FROM category WHERE category_id = ?");
		st.setInt(1, id);
		st.executeUpdate();
		st.close();
	}
}