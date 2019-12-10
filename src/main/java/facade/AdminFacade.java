package facade;

import java.sql.SQLException;
import java.util.Collection;

import MyExceptions.CompanyAlreadyExistsException;
import MyExceptions.CouponNotExistsException;
import MyExceptions.CustomerAlreadyExistException;
import MyExceptions.InvalidLoginException;
import MyExceptions.InvalidUpdateException;
import MyExceptions.NoSuchCompanyException;
import MyExceptions.NoSuchCustomerException;
import MyExceptions.SystemMalfunctionException;
import db.dao.CompanyDBDao;
import db.dao.CompanyDao;
import db.dao.CouponDBDao;
import db.dao.CouponDao;
import db.dao.CustomerDBDao;
import db.dao.CustomerDao;
import model.Company;
import model.Customer;
import model.remote.RemoteCoupon;

public class AdminFacade extends AbsFacade {
	private final CouponDao couponDao;
	private final CustomerDao customerDao;
	private final CompanyDao companyDao;

	/**
	 * Constructor.
	 * 
	 * @param couponDao
	 * @param customerDao
	 * @param companyDao
	 */
	private AdminFacade(CouponDao couponDao, CustomerDao customerDao, CompanyDao companyDao) {
		this.couponDao = couponDao;
		this.customerDao = customerDao;
		this.companyDao = companyDao;
	}

	protected static AdminFacade performLogin(String user, String password) throws InvalidLoginException {
		if ("admin".equals(user) && "1234".equals(password)) {
			return new AdminFacade(new CouponDBDao(), new CustomerDBDao(), new CompanyDBDao());
		} else {
			String msg = String.format("Can not login as Admin with name = %s, and password = %s.", user, password);
			throw new InvalidLoginException(msg);
		}
	}

	/**
	 * Inserts a new company to the data base if the company doesn't already exists.
	 * 
	 * @param company - The company to insert.
	 * @throws NoSuchCompanyException
	 * @throws SystemMalfunctionException.
	 * @throws CompanyAlreadyExistsException.
	 */
	public void createCompany(Company company)
			throws SystemMalfunctionException, CompanyAlreadyExistsException, NoSuchCompanyException {
		Collection<Company> allCompanies = companyDao.getAllCompanies();

		for (Company c : allCompanies) {
			if (c.getName().equals(company.getName())) {
				throw new CompanyAlreadyExistsException(
						String.format("Company with name %s, already exists!", company.getName()));
			}
		}
		companyDao.createCompany(company);
	}

	/**
	 * This function allows us as an admin to delete a company from our database.
	 * 
	 * @param companyId.
	 * @throws SystemMalfunctionException.
	 * @throws NoSuchCompanyException.
	 */
	public void removeCompany(long companyId) throws SystemMalfunctionException, NoSuchCompanyException {
		Collection<RemoteCoupon> coupons = companyDao.getCoupons(companyId);

		// Delete the company.
		companyDao.removeCompany(companyId);

		// Delete company's coupons.
		for (RemoteCoupon coupon : coupons) {
			try {
				couponDao.removeCoupon(coupon.getId());
			} catch (CouponNotExistsException e) {
				// Should never happen.
			}
		}
	}

	/**
	 * Updates a specific company, not allowing an update in company's name.
	 * 
	 * @param company.
	 * @throws InvalidUpdateException.
	 * @throws NoSuchCompanyException.
	 * @throws SystemMalfunctionException.
	 */
	public void updateCompany(Company company)
			throws InvalidUpdateException, SystemMalfunctionException, NoSuchCompanyException {
		// 1. Get the company from the DB.
		Company c = companyDao.getCompany(company.getId());
		// 2. If the name is not equal to the name of the company - ignore.
		if (!c.getName().equals(company.getName())) {
			throw new InvalidUpdateException("Unable to update company's name!");
		}
		// 3. If the name is the same name - update.
		companyDao.updateCompany(company);
	}

	/**
	 * This function returns us all the companies in the database.
	 * 
	 * @return Collection of companies.
	 * @throws NoSuchCompanyException
	 * @throws SystemMalfunctionException.
	 */
	public Collection<Company> getAllCompanies() throws SystemMalfunctionException, NoSuchCompanyException {
		return companyDao.getAllCompanies();
	}

	/**
	 * This function returns us a company according to his id.
	 * 
	 * @param id.
	 * @return Company.
	 * @throws SystemMalfunctionException.
	 * @throws NoSuchCompanyException.
	 */
	public Company getCompany(long id) throws SystemMalfunctionException, NoSuchCompanyException {
		return companyDao.getCompany(id);
	}

	/**
	 * This function allows us as admin to create a client in the database.
	 * 
	 * @param customer.
	 * @throws SystemMalfunctionException.
	 * @throws NoSuchCustomerException.
	 * @throws CustomerAlreadyExistException.
	 */
	public void createCustomer(Customer customer)
			throws SystemMalfunctionException, NoSuchCustomerException, CustomerAlreadyExistException {
		Collection<Customer> allCustomers = customerDao.getAllCustomers();

		for (Customer c : allCustomers) {
			if (c.getName().equals(customer.getName())) {
				throw new CustomerAlreadyExistException(
						String.format("Customer with name %s already exists!", customer.getName()));
			}
		}
		customerDao.createCustomer(customer);
	}

	public void removeCustomer(long id) throws SystemMalfunctionException, NoSuchCustomerException {
		customerDao.removeCustomer(id);
	}

	/**
	 * Update a Customer in DB, changing customer's name is not allowed!
	 * 
	 * @param customer The customer to update.
	 * @throws SystemMalfunctionException
	 * @throws InvalidUpdateException
	 * @throws NoSuchCustomerException
	 * @throws SQLException
	 */
	public void updateCustomer(Customer customer)
			throws SystemMalfunctionException, InvalidUpdateException, NoSuchCustomerException, SQLException {
		Collection<Customer> allCustomers = customerDao.getAllCustomers();
		for (Customer c : allCustomers) {
			if (c.getId() == customer.getId() && !c.getName().equals(customer.getName())) {
				throw new InvalidUpdateException("Changing customer's name is not allowed!");
			}
		}
		customerDao.updateCustomer(customer);
	}

	/**
	 * Update a Customer in DB, changing customer's name is not allowed! NOTE: this
	 * is an alternate version of the already existing updateCustomer function.
	 * 
	 * @param customer The customer to update.
	 * @throws SystemMalfunctionException
	 * @throws InvalidUpdateException
	 * @throws NoSuchCustomerException
	 * @throws SQLException
	 */
	public void updateCustomerAlternative(Customer customer)
			throws SystemMalfunctionException, InvalidUpdateException, NoSuchCustomerException {
		Customer c = customerDao.getCustomer(customer.getId());
		if (!c.getName().equals(customer.getName())) {
			throw new InvalidUpdateException("Changing customer's name is not allowed!");
		}
		customerDao.updateCustomer(customer);
	}

	/**
	 * This function returns to us all the clients of the database.
	 * 
	 * @return All the Customers from DB.
	 * @throws SystemMalfunctionException.
	 * @throws NoSuchCustomerException.
	 */
	public Collection<Customer> getAllCustomers() throws SystemMalfunctionException, NoSuchCustomerException {
		return customerDao.getAllCustomers();
	}

	public Collection<RemoteCoupon> getAllCoupons() throws SystemMalfunctionException, CouponNotExistsException {
		return couponDao.getAllCoupons();
	}

	/**
	 * This function returns us a customer according to are id.
	 * 
	 * @return A Customer from DB.
	 * @param customerId - The ID of the customer you wish to retrieve from DB.
	 * @throws SystemMalfunctionException.
	 * @throws NoSuchCustomerException.
	 */
	public Customer getCustomer(long customerId) throws SystemMalfunctionException, NoSuchCustomerException {
		return customerDao.getCustomer(customerId);
	}
}
