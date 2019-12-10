package facade;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import MyExceptions.CouponAlreadyPurshasedException;
import MyExceptions.CouponNotExistsException;
import MyExceptions.CustomerAlreadyExistException;
import MyExceptions.InvalidLoginException;
import MyExceptions.InvalidUpdateException;
import MyExceptions.NoSuchCustomerException;
import MyExceptions.SystemMalfunctionException;
import MyExceptions.ZeroCouponAmountException;
import common.CouponCategory;
import db.dao.CouponDBDao;
import db.dao.CouponDao;
import db.dao.CustomerDBDao;
import db.dao.CustomerDao;
import model.Customer;
import model.remote.RemoteCoupon;

public class CustomerFacade extends AbsFacade {
	private final CustomerDao customerDao;
	private final CouponDao couponDao;
	public final Customer customer;

	/**
	 * Constructor.
	 * 
	 * @param customerDao.
	 * @param couponDao.
	 * @param customer.
	 */
	private CustomerFacade(CustomerDao customerDao, CouponDao couponDao, Customer customer) {
		this.customerDao = customerDao;
		this.couponDao = couponDao;
		this.customer = customer;
	}

	/**
	 * This feature allows the customer to log in to his account.
	 * 
	 * @param name.
	 * @param password.
	 * @throws SystemMalfunctionException.
	 * @throws InvalidLoginException.
	 */
	protected static CustomerFacade performLogin(String name, String password)
			throws SystemMalfunctionException, InvalidLoginException {

		CustomerDBDao customerDBDao = new CustomerDBDao();
		Customer customer = customerDBDao.login(name, password);

		if (customer.getId() == Customer.NO_ID) {
			throw new InvalidLoginException("invalid password or user !");
		} else {
			return new CustomerFacade(customerDBDao, new CouponDBDao(), customer);
		}
	}

	/**
	 * This function allows us as customer to create a client in the database.
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
//		Collection<Customer> allCustomers = customerDao.getAllCustomers();
//		for (Customer c : allCustomers) {
//			if (c.getId() == customer.getId() && !c.getName().equals(customer.getName())) {
//				throw new InvalidUpdateException("Changing customer's name is not allowed!");
//			}
//		}
		customerDao.updateCustomer(customer);
	}

	/**
	 * This function returns us a customer according to are id.
	 * 
	 * @return A Customer from DB.
	 * @param customerId - The ID of the customer you wish to retrieve from DB.
	 * @throws SystemMalfunctionException.
	 * @throws NoSuchCustomerException.
	 */
	public Customer getCustomer() throws SystemMalfunctionException, NoSuchCustomerException {
		return customerDao.getCustomer(customer.getId());
	}

	/**
	 * This function will start when the customer makes the purchase of a coupon.
	 * 
	 * @param couponId.
	 * @throws SystemMalfunctionException.
	 * @throws CouponNotExistsException.
	 * @throws ZeroCouponAmountException.
	 * @throws NoSuchCustomerException.
	 * @throws CouponAlreadyPurshasedException.
	 */
	public void purchaseCoupon(long couponId) throws SystemMalfunctionException, CouponNotExistsException,
			ZeroCouponAmountException, NoSuchCustomerException, CouponAlreadyPurshasedException {
		// TODO: Purchase a coupon - this will result in manipulating the
		// Customer_Coupon table and Coupon table. The coupon should have a
		// decremented amount after purchase and it should be indicated that the
		// customer is now the owner of this coupon.
		customerDao.insertCustomerCoupon(customer.getId(), couponId);
		couponDao.decrementCouponAmount(couponId);
	}

	/**
	 * This function returns us a collection of coupons belonging to this customer.
	 * 
	 * @return Collection of coupons.
	 * @throws SystemMalfunctionException.
	 * @throws NoSuchCustomerException.
	 */
	public Collection<RemoteCoupon> getAllCoupons() throws SystemMalfunctionException, NoSuchCustomerException {
		return customerDao.getCustomerCoupons(customer.getId());
	}

	/**
	 * This function returns us a collection of coupons according to the category
	 * given in parameter.
	 * 
	 * @param category.
	 * @return Collection of coupons.
	 * @throws SystemMalfunctionException
	 * @throws NoSuchCustomerException
	 */
	public Collection<RemoteCoupon> getCouponsCustomerByCategory(CouponCategory category)
			throws SystemMalfunctionException, NoSuchCustomerException {
		Collection<RemoteCoupon> coupons = new ArrayList<>();

		for (RemoteCoupon c : getAllCoupons()) {
			if (c.getCategory() == category.getValue()) {
				coupons.add(c);
			}
		}
		return coupons;
	}

	/**
	 * This function returns us a collection of coupons whose price is inferior to
	 * the price given in parametre.
	 * 
	 * @param price.
	 * @return Collection of coupons.
	 * @throws SystemMalfunctionException.
	 * @throws NoSuchCustomerException.
	 */
	public Collection<RemoteCoupon> getCouponsCustomerBelowPrice(double price)
			throws SystemMalfunctionException, NoSuchCustomerException {
		Collection<RemoteCoupon> coupons = new ArrayList<>();

		for (RemoteCoupon c : getAllCoupons()) {
			if (c.getPrice() < price) {
				coupons.add(c);
			}
		}
		return coupons;
	}

	/**
	 * Get all coupons before a given end date.
	 * 
	 * @param date.
	 * @return Collection of coupons.
	 * @throws SystemMalfunctionException
	 * @throws NoSuchCustomerException
	 * 
	 */
	public Collection<RemoteCoupon> getCouponsCustomerBeforeEndDate(LocalDate date)
			throws SystemMalfunctionException, NoSuchCustomerException {
		Collection<RemoteCoupon> coupons = new ArrayList<>();

		for (RemoteCoupon c : getAllCoupons()) {
			if (c.getEndDate().isBefore(date)) {
				coupons.add(c);
			}
		}
		return coupons;
	}
}
