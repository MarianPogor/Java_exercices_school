package adaptor;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.*;
import utill.*;

/**
 * This class is used to transfer data from and to the database
 * @author Bogdan and Tomas
 *
 * @param <T>
 */
public class Adaptor<T> implements IAdaptor<T> {

	private Adaptee adaptee;

	/**
	 * Constructor initiates the database schema and tables if they don't exist already
	 * @throws ClassNotFoundException
	 */
	public Adaptor() throws ClassNotFoundException {
		adaptee = new Adaptee();
		
		}

	public void buildDatabase(){
	String sql = "CREATE SCHEMA IF NOT EXISTS workshop;";
	try {
		adaptee.update(sql);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
		System.out.println("CREATE SCHEMA IF NOT EXISTS (workshop) FAILED");
	}
	sql="CREATE TABLE IF NOT EXISTS workshop.location(shelfNr int, box int, nrOfParts int, partId int, PRIMARY KEY (shelfNr, box));";
	try {
		adaptee.update(sql);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
		System.out.println("CREATE TABLE IF NOT EXISTS (location) FAILED");
	}
	
	sql="CREATE TABLE IF NOT EXISTS workshop.part(partId int PRIMARY KEY, name varchar NOT NULL, make varchar NOT NULL, description varchar, category varchar NOT NULL, nrOfParts int, shelfNr int, box int);";
	try {
		adaptee.update(sql);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
		System.out.println("CREATE TABLE IF NOT EXISTS (part) FAILED");
	}
	
	
	sql="CREATE TABLE IF NOT EXISTS workshop.employee(ssn numeric PRIMARY KEY, name varchar NOT NULL, phoneNr int NOT NULL, mngrSsn numeric NOT NULL, username varchar, password varchar);";
	try {
		adaptee.update(sql);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
		System.out.println("CREATE TABLE IF NOT EXISTS (employee) FAILED");
	}

	
	sql="ALTER TABLE location ADD CONSTRAINT fk_partId FOREIGN KEY (partId) REFERENCES part (partId) ON UPDATE CASCADE;";
	try {
		adaptee.update(sql);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
		System.out.println("ALTER TABLE FOR CONSTRAINT fk_partID FAILED");
	}

	
	sql="ALTER TABLE part ADD CONSTRAINT fk_locationId FOREIGN KEY (shelfNr, box) REFERENCES location (shelfNr, box) ON UPDATE CASCADE ON DELETE CASCADE;";
	try {
		adaptee.update(sql);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
		System.out.println("ALTER TABLE FOR CONSTRAINT fk_locationID FAILED");
	}
	}

	/* (non-Javadoc)
	 * @see adaptor.IAdaptor#insert(java.lang.Object)
	 */
	@Override
	public void insert(T element) throws IllegalArgumentException, Exception{
       
		
		try {

			if (element instanceof Employee) {

				Employee temp = (Employee) element;

				String sql = "INSERT INTO \"workshop\".employee (ssn, name, phoneNr,mngrSsn,username,password)"
						+ "values("
						+ temp.getSsn()
						+ ","
						+ "'"
						+ temp.getName()
						+ "',"
						+ temp.getPhoneNr()
						+ ","
						+ temp.getMngrSSN()
						+ ","
						+ "'"
						+ temp.getUsername()
						+ "','" + temp.getPassword() + "');";
				adaptee.update(sql);

			} else if (element instanceof Part) {

				Part temp = (Part) element;
				if (getLocation(temp.getShelfNr(), temp.getBox()) == null) {
					T newLoc = (T) new Location(temp.getShelfNr(),
							temp.getBox());

					insert(newLoc);
					String sql = "INSERT INTO \"workshop\".part (partId, name, make, description, category, nrOfParts, shelfNr, box)"
							+ "values ("
							+ temp.getPartID()
							+ ","
							+ "'"
							+ temp.getName()
							+ "',"
							+ "'"
							+ temp.getMake()
							+ "',"
							+ "'"
							+ temp.getDescription()
							+ "',"
							+ "'"
							+ temp.getCategory()
							+ "',"
							+ temp.getNrOfParts()
							+ ","
							+ temp.getLocation().getShelfNr()
							+ ","
							+ temp.getLocation().getBox() + ");";


				
						adaptee.update(sql);
					updateLocation(temp);
				
				} else {
					String sql = "INSERT INTO \"workshop\".part (partId, name, make, description, category, nrOfParts, shelfNr, box)"
							+ "values ("
							+ temp.getPartID()
							+ ","
							+ "'"
							+ temp.getName()
							+ "',"
							+ "'"
							+ temp.getMake()
							+ "',"
							+ "'"
							+ temp.getDescription()
							+ "',"
							+ "'"
							+ temp.getCategory()
							+ "',"
							+ temp.getNrOfParts()
							+ ","
							+ temp.getLocation().getShelfNr()
							+ ","
							+ temp.getLocation().getBox() + ");";

					adaptee.update(sql);
					updateLocation(temp);

				}

			} else if (element instanceof Location) {
				Location temp = (Location) element;
				String sql = "";
//				if (getLocation(temp.getShelfNr(), temp.getBox()) == null){
					sql = "INSERT INTO \"workshop\".location (shelfnr, box) "
							+ "values (" + temp.getShelfNr() + ","
							+ temp.getBox() + ");";
//				}
				adaptee.update(sql);

			}

		} catch (Exception e) {
			System.out.println("SOMETHING WENT WRONG WHEN ADDING!!!");
			e.printStackTrace();
//			throw new Exception("Cannot add duplicate");
		}
	}

	/* (non-Javadoc)
	 * @see adaptor.IAdaptor#remove(java.lang.Object)
	 */
	@Override
	public T remove(T element) {
		try {

			if (element instanceof Employee) {
				Employee temp = (Employee) element;
				String sql = "DELETE FROM \"workshop\".employee WHERE ssn = "
						+ temp.getSsn() + ";";
				adaptee.update(sql);

			} else if (element instanceof Part) {
				Part temp = (Part) element;
				String sql = "DELETE FROM \"workshop\".part WHERE partId = "
						+ temp.getPartID() + ";";
				adaptee.update(sql);

			} else if (element instanceof Location) {
				Location temp = (Location) element;
				String sql = "DELETE FROM \"workshop\".location WHERE shelfNr = "
						+ temp.getShelfNr()
						+ "and box = "
						+ temp.getBox()
						+ ";";
				adaptee.update(sql);

			}

		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
		return element;

	}

	@Override
	public void update(T element) throws IllegalArgumentException, SQLException {

		if (element instanceof Employee) {
			Employee temp = (Employee) element;
			long ssn = temp.getSsn();

			if (getEmployeeById(ssn) != null) {
				String sql = "UPDATE \"workshop\".employee set name = '"
						+ temp.getName() + "', phoneNr = " + temp.getPhoneNr()
						+ ", mngrssn = " + temp.getMngrSSN() + ", username = '"
						+ temp.getUsername() + "', password = '"
						+ temp.getPassword() + "' where ssn = " + temp.getSsn()
						+ ";";
				adaptee.update(sql);
			}

		}

		else if (element instanceof Part) {

			Part temp = (Part) element;
			int partid = temp.getPartID();
			if (getPartById(partid) != null) {
				String sql = "UPDATE \"workshop\".part set name = '"
						+ temp.getName() + "', make = '" + temp.getMake()
						+ "', description = '" + temp.getDescription()
						+ "', category = '" + temp.getCategory()
						+ "', shelfNr = " + temp.getShelfNr() + ", box = "
						+ temp.getBox() + ", nrOfParts = "
						+ temp.getNrOfParts() + " where partId = "
						+ temp.getPartID() + ";";
				adaptee.update(sql);

			}
		} else if (element instanceof Location) {

			Location temp = (Location) element;
			int shelf = temp.getShelfNr();
			int box = temp.getBox();

			if (getLocation(shelf, box) != null) {
				String sql = "UPDATE \"workshop\".part set shelfNr = "
						+ temp.getShelfNr() + " and box = " + temp.getBox()
						+ "where shelfnr =" + shelf + "and box = " + box + ";";
				adaptee.update(sql);
			}

		}
	}

	@Override
	public void updateNrOfPartsAtLocation(Location location, int nrOfParts,
			int partId) throws SQLException {
		if (location != null) {
			String sql = "UPDATE \"workshop\".part set nrOfParts = "
					+ nrOfParts + "where shelfnr =" + location.getShelfNr()
					+ "and box = " + location.getBox() + "and partId = "
					+ partId + ";";
			adaptee.update(sql);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see adaptor.IAdaptor#getEmployeeById(long)
	 */
	@Override
	public Employee getEmployeeById(long ssn) throws IllegalArgumentException,
			SQLException {

		String sql = "Select * from \"workshop\".employee where ssn = " + ssn
				+ ";";
		ResultSet rs = adaptee.query(sql);
		while (rs.next()) {
			String name = rs.getString("name");
			int phonenr = rs.getInt("phonenr");
			long mngrSSN = rs.getLong("mngrssn");
			String username = rs.getString("username");
			String password = rs.getString("password");
			return new Employee(name, ssn, phonenr, mngrSSN, username, password);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see adaptor.IAdaptor#getEmployeeByUsername(java.lang.String)
	 */
	@Override
	public Employee getEmployeeByUsername(String username)
			throws IllegalArgumentException, SQLException {

		String sql = "Select * from \"workshop\".employee where username = '"
				+ username + "';";
		ResultSet rs = adaptee.query(sql);
		while (rs.next()) {
			int ssn = rs.getInt("ssn");
			String name = rs.getString("name");
			int phonenr = rs.getInt("phonenr");
			int mngrSSN = rs.getInt("mngrssn");
			String password = rs.getString("password");
			return new Employee(name, ssn, phonenr, mngrSSN, username, password);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see adaptor.IAdaptor#getLocation(int, int)
	 */
	@Override
	public Location getLocation(int shelfNr, int box)
			throws IllegalArgumentException, SQLException {

		String sql = "Select * from \"workshop\".location where shelfnr = "
				+ shelfNr + " and box =" + box + ";";
		ResultSet rs = adaptee.query(sql);

		while (rs.next()) {
			int partId = rs.getInt("partid");

			if (partId != 0) {
				Location temp = new Location(shelfNr, box);
				return temp;

			}
			return new Location(shelfNr, box);
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see adaptor.IAdaptor#getPartById(int)
	 */
	@Override
	public Part getPartById(int partID) throws IllegalArgumentException,
			SQLException {

		String sql = "Select * from \"workshop\".part where partId = " + partID
				+ ";";
		ResultSet rs = adaptee.query(sql);
		while (rs.next()) {
			String name = rs.getString("name");
			String make = rs.getString("make");
			String description = rs.getString("description");
			String category = rs.getString("category");
			int nrOfParts = rs.getInt("nrOfParts");
			int shelfNr = rs.getInt("shelfNr");
			int box = rs.getInt("box");

			return new Part(partID, name, make, description, category,
					nrOfParts, shelfNr, box);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see adaptor.IAdaptor#getPartByNrOfParts(int)
	 */
	@Override
	public Part getPartByNrOfParts(int nrOfParts)
			throws IllegalArgumentException, SQLException {

		String sql = "Select * from \"workshop\".part where nrOfParts = "
				+ nrOfParts + ";";
		ResultSet rs = adaptee.query(sql);
		while (rs.next()) {
			int partId = rs.getInt("partId");
			String name = rs.getString("name");
			String make = rs.getString("make");
			String description = rs.getString("description");
			String category = rs.getString("category");
			int shelfNr = rs.getInt("shelfNr");
			int box = rs.getInt("box");
			return new Part(partId, name, make, description, category,
					nrOfParts, shelfNr, box);

		}

		return null;
	}

	/* (non-Javadoc)
	 * @see adaptor.IAdaptor#getPartByName(java.lang.String)
	 */
	@Override
	public Part getPartByName(String name) throws IllegalArgumentException,
			SQLException {
		String sql = "Select * from \"workshop\".part where name = '" + name
				+ "';";
		ResultSet rs = adaptee.query(sql);
		while (rs.next()) {
			int partId = rs.getInt("partId");
			String make = rs.getString("make");
			String description = rs.getString("description");
			String category = rs.getString("category");
			int nrOfParts = rs.getInt("nrOfParts");
			int shelfNr = rs.getInt("shelfNr");
			int box = rs.getInt("box");

			return new Part(partId, name, make, description, category,
					nrOfParts, shelfNr, box);

		}
		return null;
	}

	/* (non-Javadoc)
	 * @see adaptor.IAdaptor#getPartByCategory(java.lang.String)
	 */
	@Override
	public Part getPartByCategory(String category)
			throws IllegalArgumentException, SQLException {
		String sql = "Select * from \"workshop\".part where category = '"
				+ category + "';";
		ResultSet rs = adaptee.query(sql);
		while (rs.next()) {
			int partId = rs.getInt("partId");
			String name = rs.getString("name");
			String make = rs.getString("make");
			String description = rs.getString("description");
			int nrOfParts = rs.getInt("nrOfParts");
			int shelfNr = rs.getInt("shelfNr");
			int box = rs.getInt("box");

			return new Part(partId, name, make, description, category,
					nrOfParts, shelfNr, box);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see adaptor.IAdaptor#getPartAtLocation(model.Location)
	 */
	@Override
	public Part getPartAtLocation(Location location)
			throws IllegalArgumentException, SQLException {
		int shelfNr = location.getShelfNr();
		int box = location.getBox();
		if (getLocation(shelfNr, box) != null) {

			String sql = "Select * from workshop.part where shelfNr = "
					+ shelfNr + " and box = " + box + ";";
			ResultSet rs = adaptee.query(sql);
			while (rs.next()) {
				int partId = rs.getInt("partId");
				String name = rs.getString("name");
				String make = rs.getString("make");
				String description = rs.getString("description");
				String category = rs.getString("category");
				int nrOfParts = rs.getInt("nrOfParts");

				return new Part(partId, name, make, description, category,
						nrOfParts, shelfNr, box);

			}
		}
		return null;

	}

	/* (non-Javadoc)
	 * @see adaptor.IAdaptor#updateLocation(model.Part)
	 */
	@Override
	public void updateLocation(Part element) throws IllegalArgumentException,
			SQLException {

		if (element instanceof Part) {
			Part temp = (Part) element;

			String s = "update workshop.location set nrOfParts = "
					+ temp.getNrOfParts() + ", partid = " + temp.getPartID()
					+ "where shelfNr = " + temp.getShelfNr() + " and box = "
					+ temp.getShelfNr() + ";";

			adaptee.update(s);
		}
	}

	/* (non-Javadoc)
	 * @see adaptor.IAdaptor#getAll(java.lang.String)
	 */
	@Override
	public ResultSet getAll(String element)
			throws IllegalArgumentException, SQLException {

		String sql = "select * from workshop." + element + ";";
		
		return adaptee.query(sql);


	}
}
