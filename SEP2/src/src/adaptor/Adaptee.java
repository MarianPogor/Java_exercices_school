package adaptor;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import utill.SetADT;


/**
 * Takes care of the database access read/write
 * @author Alexandru
 * @version 1.0
 */
public class Adaptee {
	private String url;
	private String user;
	private String pw;
	private Connection connection;

	private static final String DRIVER = "org.postgresql.Driver";
	private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
	private static final String USER = "postgres";
	private static final String PASSWORD = "alprocons";


	
	/**
	 * Constructor that uses driver, url, user and password
	 * @param driver the driver for postgressql
	 * @param url the url for accessing the host
	 * @param user the username to access the database	
	 * @param pw the password to access the database
	 * @throws ClassNotFoundException
	 */
	public Adaptee(String driver, String url, String user, String pw)
			throws ClassNotFoundException {
		this.url = url;
		this.user = user;
		this.pw = pw;
		connection = null;
		Class.forName(driver);
	}

	/**
	 * Constructor that uses database name, user and password
	 * @param databaseName database name
	 * @param user the username to access the database	
	 * @param pw the password to access the database
	 * @throws ClassNotFoundException
	 */
	public Adaptee(String databaseName, String user, String pw)
			throws ClassNotFoundException {
		this(DRIVER, URL + databaseName, user, pw);
	}

	/**
	 * Constructor that uses database name
	 * @param databaseName database name
	 * @throws ClassNotFoundException
	 */
	public Adaptee(String databaseName) throws ClassNotFoundException {
		this(DRIVER, URL + databaseName, USER, PASSWORD);
	}

	/**
	 * Constructor uses local variables
	 * @throws ClassNotFoundException
	 */
	public Adaptee() throws ClassNotFoundException {
		this(DRIVER, URL, USER, PASSWORD);
	}

	/**
	 * @param sql
	 * @throws SQLException
	 */
	public void update(String sql) throws SQLException {
		openDatabase();
		Statement stm = connection.createStatement();
		stm.executeUpdate(sql);
		closeDatabase();

	}

	/**
	 * @throws SQLException
	 */
	private void openDatabase() throws SQLException {
		connection = DriverManager.getConnection(url, user, pw);
	}

	/**
	 * @throws SQLException
	 */
	private void closeDatabase() throws SQLException {
		connection.close();
	}

	/**
	 * @param sql
	 * @return ResultSet
	 * @throws SQLException
	 */
	public ResultSet query(String sql) throws SQLException {
		openDatabase();
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		closeDatabase();
		return rs;

	}

}
