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

import MyExceptions.InvalidLoginException;
import MyExceptions.NoSuchCompanyException;
import MyExceptions.SystemMalfunctionException;
import common.ConnectionPool;
import common.ResourceUtils;
import db.Schema;
import model.Company;
import model.remote.RemoteCoupon;

public class CompanyDBDao implements CompanyDao {

	/**
	 * Constructor.
	 * 
	 * @author Solal Arroues.
	 */
	public CompanyDBDao() {
		try {
			createTable();
		} catch (SystemMalfunctionException e) {
			// Should never happen.
		}
	}

	/**
	 * This function will allow us to create a table of companies and a table of
	 * companies coupons.
	 * 
	 * @throws SystemMalfunctionException.
	 * @author Solal Arroues.
	 */
	@Override
	public void createTable() throws SystemMalfunctionException {
		Statement stmtCreateCompanyTable = null;
		Statement stmtCreateCompanyCouponTable = null;
		Connection connection = null;

		try {
			connection = ConnectionPool.getInstance().getConnection();

			stmtCreateCompanyTable = connection.createStatement();
			stmtCreateCompanyTable.executeUpdate(Schema.getCreateTableCompany());

			stmtCreateCompanyCouponTable = connection.createStatement();
			stmtCreateCompanyCouponTable.executeUpdate(Schema.getCreateTableCompanyCoupon());

		} catch (SQLException e) {
			throw new SystemMalfunctionException("Impossible to create the table!" + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(stmtCreateCompanyTable, stmtCreateCompanyCouponTable);
		}
	}

	/**
	 * This function will allow us to insert a new company to our database.
	 * 
	 * @param company.
	 * @throws SystemMalfunctionException.
	 * @author Solal Arroues.
	 */
	@Override
	public void createCompany(Company company) throws SystemMalfunctionException {
		PreparedStatement stmtCreateCompany = null;
		Connection connection = null;
		try {
			connection = ConnectionPool.getInstance().getConnection();

			stmtCreateCompany = connection.prepareStatement(Schema.getInsertCompany());
			applyCompanyValuesOnStatement(stmtCreateCompany, company);

			stmtCreateCompany.executeUpdate();
		} catch (SQLException e) {
			throw new SystemMalfunctionException("There was a problem creating a company!" + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(stmtCreateCompany);
		}
	}

	/**
	 * This function will allow us to fill in the sql request field which creates a
	 * new company.
	 * 
	 * @param stmtCreateCompany.
	 * @param company.
	 * @throws SQLException.
	 * @author Solal Arroues.
	 */
	private static void applyCompanyValuesOnStatement(PreparedStatement stmtCreateCompany, Company company)
			throws SQLException {

		stmtCreateCompany.setString(1, company.getName());
		stmtCreateCompany.setString(2, company.getPassword());
		stmtCreateCompany.setString(3, company.getEmail());
	}

	/**
	 * This function will allow us to delete a company from our database.
	 * 
	 * @param id.
	 * @throws SystemMalfunctionException.
	 * @throws NoSuchCompanyException.
	 * @author Solal Arroues.
	 */
	@Override
	public void removeCompany(long id) throws SystemMalfunctionException, NoSuchCompanyException {
		PreparedStatement stmtDeleteCompany = null;
		Connection connection = null;

		try {
			connection = ConnectionPool.getInstance().getConnection();

			stmtDeleteCompany = connection.prepareStatement(Schema.getRemoveCompany());
			stmtDeleteCompany.setLong(1, id);

			if (stmtDeleteCompany.executeUpdate() == 0) {
				throw new NoSuchCompanyException(String.format("Unable to remove company. Invalid id: %d", id));
			}
		} catch (SQLException e) {
			throw new SystemMalfunctionException("There was problem removing company " + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(stmtDeleteCompany);
		}
	}

	/**
	 * This function allows us to change the details of a company.
	 * 
	 * @param Customer.
	 * @throws SystemMalfunctionException.
	 * @throws NoSuchCompanyException.
	 * @author Solal Arroues.
	 */
	@Override
	public void updateCompany(Company company) throws SystemMalfunctionException, NoSuchCompanyException {
		PreparedStatement stmtUpdateCompany = null;
		Connection connection = null;

		try {
			connection = ConnectionPool.getInstance().getConnection();

			stmtUpdateCompany = connection.prepareStatement(Schema.getUpdateCompanyById());
			applyCompanyValuesOnStatement(stmtUpdateCompany, company);
			stmtUpdateCompany.setLong(4, company.getId());

			if (stmtUpdateCompany.executeUpdate() == 0) {
				throw new NoSuchCompanyException("This id (" + company.getId() + ") is not exists");
			}

		} catch (SQLException e) {
			throw new SystemMalfunctionException("Unable to update company " + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(stmtUpdateCompany);
		}
	}

	/**
	 * This function returns us a company according to its id.
	 * 
	 * @param id.
	 * @throws SystemMalfunctionException.
	 * @throws NoSuchCompanyException.
	 * @return Company.
	 * @author Solal Arroues.
	 */
	@Override
	public Company getCompany(long id) throws SystemMalfunctionException, NoSuchCompanyException {
		Connection connection = null;
		PreparedStatement stmtSelectCompany = null;
		ResultSet rs = null;

		Company company = new Company();
		try {
			connection = ConnectionPool.getInstance().getConnection();

			stmtSelectCompany = connection.prepareStatement(Schema.getSelectCompanyById());
			stmtSelectCompany.setLong(1, id);

			rs = stmtSelectCompany.executeQuery();

			if (rs.first()) {

				company.setId(rs.getLong(1));
				company.setName(rs.getString(2));
				company.setPassword(rs.getString(3));
				company.setEmail(rs.getString(4));
				company.setCoupons(getCompanyCoupons(id));

			} else {
				throw new NoSuchCompanyException(String.format("There's no company for id: %d", id));
			}
		} catch (SQLException e) {
			throw new SystemMalfunctionException("Unable to get company: " + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(stmtSelectCompany);
			ResourceUtils.close(rs);
		}
		return company;
	}

	/**
	 * This function returns us a set of coupons belonging to a company from an id
	 * of the company.
	 * 
	 * @param id.
	 * @param connection.
	 * @return set of coupons.
	 * @throws SystemMalfunctionException.
	 * @author Solal Arroues.
	 * @throws NoSuchCompanyException
	 */
	private static Set<RemoteCoupon> getCompanyCoupons(long id)
			throws SystemMalfunctionException, NoSuchCompanyException {
		Connection connection = null;
		PreparedStatement stmtSelectCompanyCoupons = null;
		ResultSet rs = null;

		CouponDBDao couponDBDao = new CouponDBDao();

		Set<RemoteCoupon> coupons = new HashSet<>();

		try {
			connection = ConnectionPool.getInstance().getConnection();

			stmtSelectCompanyCoupons = connection.prepareStatement(Schema.getSelectCompanyCouponInnerJoinById());
			stmtSelectCompanyCoupons.setLong(1, id);

			rs = stmtSelectCompanyCoupons.executeQuery();

			while (rs.next()) {
				coupons.add(couponDBDao.resultSetToCoupon(rs));
			}

		} catch (SQLException e) {
			throw new SystemMalfunctionException("Unable to get company's coupons " + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(stmtSelectCompanyCoupons);
			ResourceUtils.close(rs);
		}
		return coupons;
	}

	/**
	 * This function returns a collection containing all the companies in our
	 * database.
	 * 
	 * @return Collection of companies.
	 * @throws SystemMalfunctionException.
	 * @author Solal Arroues.
	 * @throws NoSuchCompanyException
	 */
	@Override
	public Collection<Company> getAllCompanies() throws SystemMalfunctionException, NoSuchCompanyException {
		Connection connection = null;
		Statement stmtGetAllCompanies = null;
		ResultSet rs = null;

		Collection<Company> companies = new ArrayList<>();

		try {
			connection = ConnectionPool.getInstance().getConnection();

			stmtGetAllCompanies = connection.createStatement();
			rs = stmtGetAllCompanies.executeQuery(Schema.getAllCompanies());

			while (rs.next()) {
				long companyID = rs.getLong(1);
				companies.add(getCompany(companyID));
			}

		} catch (SQLException e) {
			throw new SystemMalfunctionException("Failed getting all comapnies: " + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(stmtGetAllCompanies);
			ResourceUtils.close(rs);
		}
		return companies;
	}

	/**
	 * This function returns us a collection of coupons belonging to the company
	 * whose id is given in parameter.
	 * 
	 * @param companyId.
	 * @return collection of coupons.
	 * @throws SystemMalfunctionException.
	 * @throws NoSuchCompanyException.
	 * @author Solal Arroues.
	 */
	@Override
	public Collection<RemoteCoupon> getCoupons(long companyId)
			throws SystemMalfunctionException, NoSuchCompanyException {
		Connection connection = null;

		try {
			connection = ConnectionPool.getInstance().getConnection();

			return getCompanyCoupons(companyId);
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
		}
	}

	/**
	 * This function will allow the company to log in to its account.
	 * 
	 * @param name.
	 * @param password.
	 * @return company.
	 * @throws SystemMalfunctionException.
	 * @throws InvalidLoginException.
	 * @author Solal Arroues.
	 */
	@Override
	public Company login(String name, String password) throws SystemMalfunctionException, InvalidLoginException {
		Connection connection = null;
		PreparedStatement stmtGetCompany = null;
		ResultSet rs = null;

		String messageInvalidLogin = String.format("Invalid login for name = %s, and password = %s", name, password);

		try {
			connection = ConnectionPool.getInstance().getConnection();

			stmtGetCompany = connection.prepareStatement(Schema.getCompanyByNamePassword());
			stmtGetCompany.setString(1, name);
			stmtGetCompany.setString(2, password);

			rs = stmtGetCompany.executeQuery();

			if (rs.first()) {
				long companyId = rs.getLong(1);
				return getCompany(companyId);
			} else {
				throw new InvalidLoginException("incorrect fields");
			}
		} catch (SQLException | NoSuchCompanyException e) {
			throw new InvalidLoginException(messageInvalidLogin);
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(stmtGetCompany);
			ResourceUtils.close(rs);
		}
	}

}
