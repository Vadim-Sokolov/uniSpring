package com.foxminded.univer.dao.impl;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dbunit.Assertion;
import org.dbunit.DBTestCase;
import org.dbunit.JndiBasedDBTestCase;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.foxminded.univer.dao.PropertiesHolder;
import com.foxminded.univer.models.Auditorium;

//@RunWith(JUnit4.class)
public class AuditoriumDaoTest extends JndiBasedDBTestCase {

	private AuditoriumDao auditoriumDao = Mockito.spy(AuditoriumDao.class);

	@Before
	public void setUp() throws SQLException, ClassNotFoundException {
		Class.forName("org.postgresql.Driver");
		Connection connection = DriverManager.getConnection(PropertiesHolder.URL, PropertiesHolder.USER,
				PropertiesHolder.PASSWORD);
		Mockito.doReturn(connection).when(auditoriumDao).getConnection();
	}

	public AuditoriumDaoTest(String name) {
		super(name);
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, PropertiesHolder.DRIVER);
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, PropertiesHolder.URL);
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, PropertiesHolder.USER);
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, PropertiesHolder.PASSWORD);
	}

	@Test
	public void testSaveNewAuditorium() throws Exception {
		// Given
		String[] toIgnore = { "id" };
		ITable expectedTable = getExpectedTable("addAuditoriumTestTable.xml");
		Auditorium auditorium = new Auditorium();
		auditorium.setName("A4");
		auditorium.setCapacity(30);

		// When
		auditoriumDao.save(auditorium);
		ITable actualTable = getActualTable();

		// Then
		Assertion.assertEqualsIgnoreCols(expectedTable, actualTable, toIgnore);
	}

	@Test
	public void testSaveExistingAuditorium() throws Exception {
		// Given
		ITable expectedTable = getExpectedTable("updateAuditoriumTestTable.xml");
		Auditorium auditorium = new Auditorium();
		auditorium.setId(1);
		auditorium.setName("A1");
		auditorium.setCapacity(60);

		// When
		auditoriumDao.save(auditorium);
		ITable actualTable = getActualTable();

		// Then
		Assertion.assertEquals(expectedTable, actualTable);
	}

	@Test
	public void testDelete() throws Exception {
		// Given
		ITable expectedTable = getExpectedTable("deleteAuditoriumTestTable.xml");
		Auditorium auditorium = new Auditorium();
		auditorium.setId(1);
		auditorium.setName("A1");
		auditorium.setCapacity(30);

		// When
		auditoriumDao.delete(auditorium);
		ITable actualTable = getActualTable();

		// Then
		Assertion.assertEquals(expectedTable, actualTable);
	}

	@Test
	public void testFindAll() throws Exception {
		// Given
		ITable expectedTable = getExpectedTable("findAllAuditoriumTestTable.xml");

		// When
		ITable actualTable = getActualTable();

		// Then
		Assertion.assertEquals(expectedTable, actualTable);
	}

	@Test
	public void testFindById() throws ClassNotFoundException {
		// Given
		Auditorium expectedAuditorium = new Auditorium();
		expectedAuditorium.setId(1);
		expectedAuditorium.setName("A1");
		expectedAuditorium.setCapacity(30);

		// When
		Auditorium actualAuditorium = auditoriumDao.findById(1).get();

		// Then
		Assert.assertEquals(expectedAuditorium, actualAuditorium);
	}

	protected IDataSet getDataSet() throws Exception {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		InputStream inputFile = classLoader.getResourceAsStream("inputAuditoriumTestTable.xml");
		return new FlatXmlDataSetBuilder().build(inputFile);
	}

	private ITable getActualTable() throws Exception {
		IDataSet actualDataSet = getConnection().createDataSet();
		ITable actualTable = actualDataSet.getTable("AUDITORIUMS");
		return actualTable;
	}

	private ITable getExpectedTable(String fileName) throws Exception {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		InputStream inputFile = classLoader.getResourceAsStream(fileName);
		IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(inputFile);
		ITable expectedTable = expectedDataSet.getTable("AUDITORIUMS");
		return expectedTable;
	}

	protected DatabaseOperation getSetUpOperation() throws Exception {
		return DatabaseOperation.REFRESH;
	}

	protected DatabaseOperation getTearDownOperation() throws Exception {
		return DatabaseOperation.CLEAN_INSERT;
	}

	protected String getLookupName() {
		return "jdbc/university";
	}
}
