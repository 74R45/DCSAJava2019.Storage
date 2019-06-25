package com.x74R45.storage;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import com.x74R45.storage.database.DBConnector;
import com.x74R45.storage.database.DBInteractor;
import com.x74R45.storage.server.Category;
import com.x74R45.storage.server.Product;

public class DBTest {
	
	@BeforeClass
	public static void setup() {
		if (!DBConnector.isConnected())
			DBConnector.initialize();
	}
	
	@Test
	public void loginTest() {
		try {
			assertTrue("Didn't find user with login \"login\" and password \"password\".",
					DBInteractor.checkUser("login", "password"));
			assertFalse("Found user with login \"nonexistent\" and password \"blablabla\".",
					DBInteractor.checkUser("nonexistent", "blablabla"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail("SQLException!");
		}
	}
	
	@Test
	public void productsTest() {
		try {
			ArrayList<Product> prs = DBInteractor.getAllProducts();
			assertEquals("The amount of products was not 0 before adding any.", 0, prs.size());
			
			int category_id = DBInteractor.createCategory(new Category(0, "all", null));
			Product pr = new Product(0, "rice", null, "someone", 5, "16.75", category_id);
			int rice_id = DBInteractor.createProduct(pr);
			pr.setId(rice_id);
			
			prs = DBInteractor.getAllProducts();
			assertEquals("Amount of products is not 1 after adding one product.", 1, prs.size());
			assertEquals("The product has changed after adding it to the database.", pr, prs.get(0));
			
			pr.setAmount(7);
			pr.setDescription("description");
			pr.setMaker("nobody");
			pr.setName("spaghetti");
			pr.setPrice(new BigDecimal("18.99"));
			DBInteractor.changeProduct(pr);
			
			prs = DBInteractor.getAllProducts();
			assertEquals("Amount of products is not 1 after adding one product.", 1, prs.size());
			assertEquals("The product has changed after adding it to the database.", pr, prs.get(0));
			
			DBInteractor.deleteProduct(pr.getId());
			prs = DBInteractor.getAllProducts();
			assertEquals("The amount of products was not 0 after deleting the product.", 0, prs.size());
			
			DBInteractor.deleteCategory(category_id);
		} catch (SQLException e) {
			e.printStackTrace();
			fail("SQLException!");
		}
	}

	@Test
	public void categoriesTest() {
		try {
			ArrayList<Category> cats = DBInteractor.getAllCategories();
			assertEquals("The amount of categories was not 0 before adding any.", 0, cats.size());
			
			Category cat = new Category(0, "category", null);
			int id = DBInteractor.createCategory(cat);
			cat.setId(id);
			
			cats = DBInteractor.getAllCategories();
			assertEquals("Amount of categories is not 1 after adding one category.", 1, cats.size());
			assertEquals("The category has changed after adding it to the database.", cat, cats.get(0));
			
			cat.setDescription("description");
			cat.setName("dog food");
			DBInteractor.changeCategory(cat);
			
			cats = DBInteractor.getAllCategories();
			assertEquals("Amount of categories is not 1 after adding one category.", 1, cats.size());
			assertEquals("The category has changed after adding it to the database.", cat, cats.get(0));
			
			DBInteractor.deleteCategory(cat.getId());
			cats = DBInteractor.getAllCategories();
			assertEquals("The amount of categories was not 0 after deleting the category.", 0, cats.size());
		} catch (SQLException e) {
			e.printStackTrace();
			fail("SQLException!");
		}
	}
}