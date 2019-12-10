package db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import MyExceptions.CouponAlreadyPurshasedException;
import MyExceptions.CouponNotExistsException;
import MyExceptions.CustomerAlreadyExistException;
import MyExceptions.InvalidLoginException;
import MyExceptions.NoSuchCustomerException;
import MyExceptions.SystemMalfunctionException;
import MyExceptions.ZeroCouponAmountException;
import common.ConnectionPool;
import common.ResourceUtils;
import db.Schema;
import model.Customer;
import model.remote.RemoteCoupon;

public class CustomerDBDao implements CustomerDao {
	/**
	 * Constructor
	 */
	public CustomerDBDao() {
		try {
			createTable();
		} catch (SystemMalfunctionException e) {
			// Should never happen. Contact the developer. Which is you!
		}
	}

	/**
	 * This function creates a customer table and a customer coupon table.
	 * 
	 * @throws SystemMalfunctionException.
	 * @author Solal Arroues.
	 */
	@Override
	public void createTable() throws SystemMalfunctionException {
		Connection connection = null;
		Statement stmtCreateCustomerTable = null;
		Statement stmtCreateCustomerCouponTable = null;

		try {
			connection = ConnectionPool.getInstance().getConnection();

			stmtCreateCustomerTable = connection.createStatement();
			stmtCreateCustomerTable.executeUpdate(Schema.getCreateTableCustomer());

			stmtCreateCustomerCouponTable = connection.createStatement();
			stmtCreateCustomerCouponTable.executeUpdate(Schema.getCreateTableCustomerCoupon());

		} catch (SQLException e) {
			throw new SystemMalfunctionException(
					"Impossible to create the Customer table or Customer_Coupon table !" + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(stmtCreateCustomerTable, stmtCreateCustomerCouponTable);
		}
	}

	/**
	 * This function inserts a new customer into the Customer table.
	 * 
	 * @param Customer.
	 * @throws CustomerAlreadyExistException.
	 * @throws SystemMalfunctionException.
	 * @author Solal Arroues.
	 */
	public void createCustomer(Customer customer) throws SystemMalfunctionException, CustomerAlreadyExistException {
		Connection connection = null;
		PreparedStatement prpdGetCustomerStmt = null;

		try {
			connection = ConnectionPool.getInstance().getConnection();

			if (!isExistByName(customer.getName())) {

				prpdGetCustomerStmt = connection.prepareStatement(Schema.getCreateCustomer());

				prpdGetCustomerStmt.setString(1, customer.getName());
				prpdGetCustomerStmt.setString(2, customer.getPassword());

				prpdGetCustomerStmt.executeUpdate();

			} else {
				throw new CustomerAlreadyExistException(
						"This customer (" + customer.getName() + ") is already exist !");
			}

		} catch (SQLException e) {
			throw new SystemMalfunctionException("There was a problem creating a customer." + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(prpdGetCustomerStmt);
		}
	}

	/**
	 * This function we return a customer by id.
	 * 
	 * @param id.
	 * @return Customer.
	 * @throws SystemMalfunctionException..
	 * @throws NoSuchCustomerException.
	 * @author Solal Arroues.
	 */
	@Override
	public Customer getCustomer(long id) throws SystemMalfunctionException, NoSuchCustomerException {
		Connection connection = null;
		PreparedStatement prpdGetCustomerStmt = null;
		ResultSet rs = null;

		Customer customer = new Customer();
		Set<RemoteCoupon> coupons = getCustomerCoupons(id);

		try {
			connection = ConnectionPool.getInstance().getConnection();

			prpdGetCustomerStmt = connection.prepareStatement(Schema.getCustomerByID());
			prpdGetCustomerStmt.setLong(1, id);

			rs = prpdGetCustomerStmt.executeQuery();

			if (rs.first()) {
				resultSetToCustomer(rs, customer, coupons);

			} else {
				throw new NoSuchCustomerException("This customer id (" + id + ") does not exist !");
			}
		} catch (SQLException e) {
			throw new SystemMalfunctionException("Impossible to get customer !" + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(prpdGetCustomerStmt);
			ResourceUtils.close(rs);
		}
		return customer;
	}

	private void resultSetToCustomer(ResultSet rs, Customer customer, Set<RemoteCoupon> coupons) throws SQLException {
		customer.setId(rs.getInt(1));
		customer.setName(rs.getString(2));
		customer.setPassword(rs.getString(3));

		customer.setCoupons(coupons);
	}

	/**
	 * This function clears a customer from the database.
	 * 
	 * @param id.
	 * @throws SystemMalfunctionException.
	 * @throws NoSuchCustomerException.
	 * @author Solal Arroues.
	 */
	@Override
	public void removeCustomer(long id) throws SystemMalfunctionException, NoSuchCustomerException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = ConnectionPool.getInstance().getConnection();
			preparedStatement = connection.prepareStatement(Schema.getRemoveCustomer());

			preparedStatement.setLong(1, id);

			if (preparedStatement.executeUpdate() == 0) {
				throw new NoSuchCustomerException("This id (" + id + ") is not exists !");
			}
		} catch (SQLException e) {
			throw new SystemMalfunctionException("There was a problem remove a customer." + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(preparedStatement);
		}

	}

	/**
	 * This function clears a customer from the database.
	 * 
	 * @param Customer.
	 * @throws SystemMalfunctionException.
	 * @throws NoSuchCustomerException.
	 * @author Solal Arroues.
	 */
	public void removeCustomer(Customer customer) throws SystemMalfunctionException, NoSuchCustomerException {
		removeCustomer(customer.getId());
	}

	/**
	 * This function allows us to change the details of a customer.
	 * 
	 * @param Customer.
	 * @throws SystemMalfunctionException.
	 * @throws NoSuchCustomerException.
	 * @author Solal Arroues.
	 */
	@Override
	public void updateCustomer(Customer customer) throws SystemMalfunctionException, NoSuchCustomerException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = ConnectionPool.getInstance().getConnection();

			preparedStatement = connection.prepareStatement(Schema.getUpdateCustomer());

			preparedStatement.setString(1, customer.getName());
			preparedStatement.setString(2, customer.getPassword());
			preparedStatement.setLong(3, customer.getId());

			if (preparedStatement.executeUpdate() == 0) {
				throw new NoSuchCustomerException("This customer (" + customer.getName() + ") does not exist");
			}
		} catch (SQLException e) {
			throw new SystemMalfunctionException("There was a problem update a customer." + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(preparedStatement);
		}
	}

	/**
	 * This function returns us a collection of all the customers of our database.
	 * 
	 * @throws SystemMalfunctionException.
	 * @throws NoSuchCustomerException.
	 * @author Solal Arroues.
	 */
	@Override
	public Collection<Customer> getAllCustomers() throws SystemMalfunctionException, NoSuchCustomerException {
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;

		Collection<Customer> customers = new ArrayList<>();

		try {
			connection = ConnectionPool.getInstance().getConnection();

			stmt = connection.createStatement();
			rs = stmt.executeQuery(Schema.getTableCustomer());

			while (rs.next()) {
				long customerID = rs.getLong(1);
				customers.add(getCustomer(customerID));
			}

		} catch (SQLException e) {
			throw new SystemMalfunctionException("impossible to create the collection of customers" + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(stmt);
			ResourceUtils.close(rs);
		}
		return customers;
	}

	/**
	 * This function returns us a collection of coupons belonging to the customer
	 * whose id is given in parameter.
	 * 
	 * @param id of the customer.
	 * @throws SystemMalfunctionException.
	 * @throws NoSuchCustomerException.
	 * @author Solal Arroues.
	 */
	@Override
	public Set<RemoteCoupon> getCustomerCoupons(long id) throws SystemMalfunctionException, NoSuchCustomerException {
		Connection connection = null;
		PreparedStatement prpdGetCouponsStmt = null;
		ResultSet rs = null;

		CouponDBDao couponDBDao = new CouponDBDao();

		Set<RemoteCoupon> coupons = new HashSet<>();

		try {
			connection = ConnectionPool.getInstance().getConnection();

			prpdGetCouponsStmt = connection.prepareStatement(Schema.getCouponsByCustomerID());
			prpdGetCouponsStmt.setLong(1, id);

			rs = prpdGetCouponsStmt.executeQuery();

			while (rs.next()) {
				coupons.add(couponDBDao.resultSetToCoupon(rs));
			}

		} catch (SQLException e) {
			throw new SystemMalfunctionException("Impossible to get coupons !" + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(prpdGetCouponsStmt);
			ResourceUtils.close(rs);
		}

		return coupons;
	}

	/**
	 * This function helps us to place a coupon in a customer's account.
	 * 
	 * @param customerId.
	 * @param couponId.
	 * 
	 * @throws SystemMalfunctionException.
	 * @throws CouponAlreadyPurshasedException.
	 * @throws CouponNotExistsException.
	 * @throws NoSuchCustomerException.
	 * @author Solal Arroues.
	 * @throws ZeroCouponAmountException
	 */
	@Override
	public void insertCustomerCoupon(long customerId, long couponId)
			throws SystemMalfunctionException, CouponAlreadyPurshasedException, CouponNotExistsException,
			NoSuchCustomerException, ZeroCouponAmountException {
		CouponDBDao couponDBDao = new CouponDBDao();

		if (!isExistByID(customerId)) {
			throw new NoSuchCustomerException("invalid customer id!");

		} else if (!CouponDBDao.isExistByID(couponId)) {
			throw new CouponNotExistsException("invalid coupon id !");

		} else if (thisRowIsAlreadyExists(customerId, couponId)) {
			throw new CouponAlreadyPurshasedException("This coupon has already been purchased by this customer !");

		} else if (couponDBDao.getCoupon(couponId).getAmount() < 1) {
			throw new ZeroCouponAmountException("there are no more coupons like this one");
		}

		else {

			Connection connection = null;
			PreparedStatement preparedStatement = null;

			try {
				connection = ConnectionPool.getInstance().getConnection();

				preparedStatement = connection.prepareStatement(Schema.getInsertCustomerCoupon());

				preparedStatement.setLong(1, customerId);
				preparedStatement.setLong(2, couponId);

				preparedStatement.executeUpdate();
			} catch (SQLException e) {
				throw new SystemMalfunctionException("Impossible to insert to customer coupon ! " + e.getMessage());
			} finally {
				ConnectionPool.getInstance().returnConnection(connection);
				ResourceUtils.close(preparedStatement);
			}
		}
	}

	/**
	 * This function will allow the customer to connect to his account via his name
	 * and password.
	 * 
	 * @param name.
	 * @param password.
	 * @throws SystemMalfunctionException.
	 * @throws InvalidLoginException.
	 * @author Solal Arroues.
	 * @return customer.
	 * @throws NoSuchCustomerException
	 */
	@Override
	public Customer login(String name, String password) throws SystemMalfunctionException, InvalidLoginException {
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;

		Customer customer = new Customer();

		try {
			connection = ConnectionPool.getInstance().getConnection();

			statement = connection.createStatement();
			rs = statement.executeQuery(Schema.getTableCustomer());

			while (rs.next()) {
				customer.setId(rs.getLong(1));
				customer.setName(rs.getString(2));
				customer.setPassword(rs.getString(3));

				if (name.equals(customer.getName()) && password.equals(customer.getPassword())) {
					customer.setCoupons(getCustomerCoupons(customer.getId()));

					return customer;
				}
			}
			// if the loop ended without logging in
			throw new InvalidLoginException("Incorrect username or password !");

		} catch (SQLException | NoSuchCustomerException e) {
			throw new SystemMalfunctionException("Impossible to connect !" + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(statement);
			ResourceUtils.close(rs);

		}
	}

	/**
	 * This function helps us to know if a client whose name is given as a parameter
	 * exists in the database.
	 * 
	 * @param name.
	 * @return boolean.
	 * @throws SystemMalfunctionException.
	 * @author Solal Arroues.
	 */
	private boolean isExistByName(String name) throws SystemMalfunctionException {
		Connection connection = null;
		Statement preparedStatement = null;
		ResultSet rs = null;

		try {
			connection = ConnectionPool.getInstance().getConnection();

			preparedStatement = connection.createStatement();
			rs = preparedStatement.executeQuery(Schema.getTableCustomer());

			while (rs.next()) {
				if (name.equals(rs.getString(2))) {
					return true;
				}
			}
		} catch (SQLException e) {
			throw new SystemMalfunctionException("Impossible to check if this user exists" + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(preparedStatement);
			ResourceUtils.close(rs);
		}
		return false;
	}

	/**
	 * This function helps us to know if a client whose id is given as a parameter
	 * exists in the database.
	 * 
	 * @param name.
	 * @return boolean.
	 * @throws SystemMalfunctionException.
	 * @author Solal Arroues.
	 */
	private boolean isExistByID(Long id) throws SystemMalfunctionException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;

		try {
			connection = ConnectionPool.getInstance().getConnection();

			preparedStatement = connection.prepareStatement(Schema.getCustomerByID());
			preparedStatement.setLong(1, id);

			rs = preparedStatement.executeQuery();

			if (rs.first()) {
				return true;
			}
		} catch (SQLException e) {
			throw new SystemMalfunctionException("Impossible to check if this customer exists" + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(preparedStatement);
			ResourceUtils.close(rs);
		}
		return false;
	}

	/**
	 * this function helps us to know if the customer whose id is given in parameter
	 * already has the coupon of the id is also given in parameter
	 *
	 * @param customer id.
	 * @param coupon   id.
	 * @return boolean.
	 * @throws SystemMalfunctionException.
	 * @author Solal Arroues.
	 */
	private boolean thisRowIsAlreadyExists(Long customerID, Long couponID) throws SystemMalfunctionException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;

		try {
			connection = ConnectionPool.getInstance().getConnection();

			preparedStatement = connection.prepareStatement(Schema.getRowCustomerCoupon());
			preparedStatement.setLong(1, customerID);
			preparedStatement.setLong(2, couponID);

			rs = preparedStatement.executeQuery();

			if (rs.first()) {
				return true;
			}
		} catch (SQLException e) {
			throw new SystemMalfunctionException("Impossible to check if this customer exists" + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(preparedStatement);
			ResourceUtils.close(rs);
		}
		return false;
	}
}
