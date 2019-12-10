package db.dao;

import java.util.Collection;

import MyExceptions.CouponAlreadyPurshasedException;
import MyExceptions.CouponNotExistsException;
import MyExceptions.CustomerAlreadyExistException;
import MyExceptions.InvalidLoginException;
import MyExceptions.NoSuchCustomerException;
import MyExceptions.SystemMalfunctionException;
import MyExceptions.ZeroCouponAmountException;
import model.Customer;
import model.remote.RemoteCoupon;

public interface CustomerDao {

	void createTable() throws SystemMalfunctionException;

	void createCustomer(Customer customer) throws SystemMalfunctionException, CustomerAlreadyExistException;

	void removeCustomer(Customer customer) throws SystemMalfunctionException, NoSuchCustomerException;

	void removeCustomer(long id) throws SystemMalfunctionException, NoSuchCustomerException;

	void updateCustomer(Customer customer) throws SystemMalfunctionException, NoSuchCustomerException;

	Customer getCustomer(long id) throws SystemMalfunctionException, NoSuchCustomerException;

	Collection<Customer> getAllCustomers() throws SystemMalfunctionException, NoSuchCustomerException;

	Collection<RemoteCoupon> getCustomerCoupons(long customerId) throws SystemMalfunctionException, NoSuchCustomerException;

	void insertCustomerCoupon(long customerId, long couponId) throws SystemMalfunctionException,
			NoSuchCustomerException, CouponAlreadyPurshasedException, CouponNotExistsException, ZeroCouponAmountException;

	Customer login(String name, String password) throws SystemMalfunctionException, InvalidLoginException, NoSuchCustomerException;

}
