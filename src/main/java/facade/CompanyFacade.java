package facade;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import MyExceptions.CouponAlreadyExistsException;
import MyExceptions.CouponNotExistsException;
import MyExceptions.InvalidLoginException;
import MyExceptions.InvalidUpdateException;
import MyExceptions.NoSuchCompanyException;
import MyExceptions.SystemMalfunctionException;
import common.CouponCategory;
import db.dao.CompanyDBDao;
import db.dao.CompanyDao;
import db.dao.CouponDBDao;
import db.dao.CouponDao;
import model.Company;
import model.remote.RemoteCoupon;

public class CompanyFacade extends AbsFacade {
	private final Company company;
	private final CompanyDao companyDao;
	private final CouponDao couponDao;

	/**
	 * Constructor.
	 * 
	 * @param company.
	 * @param companyDao.
	 * @param couponDao.
	 */
	private CompanyFacade(Company company, CompanyDao companyDao, CouponDao couponDao) {
		this.company = company;
		this.companyDao = companyDao;
		this.couponDao = couponDao;
	}

	protected static AbsFacade performLogin(String name, String password)
			throws SystemMalfunctionException, InvalidLoginException {
		CompanyDBDao companyDBDao = new CompanyDBDao();
		Company company = companyDBDao.login(name, password);

		return new CompanyFacade(company, companyDBDao, new CouponDBDao());
	}

	/**
	 * Creates a coupon in data base. A coupon with an existing title, will be
	 * reject on creation attempt.
	 * 
	 * @param coupon - The Coupon you wish to create.
	 * @throws SystemMalfunctionException
	 * @throws CouponAlreadyExistsException
	 * @throws CouponNotExistsException
	 */
	public void createCoupon(RemoteCoupon coupon)
			throws SystemMalfunctionException, CouponAlreadyExistsException, CouponNotExistsException {
		Collection<RemoteCoupon> allCoupons = couponDao.getAllCoupons();

		for (RemoteCoupon c : allCoupons) {

			if (c.getTitle().equals(coupon.getTitle())) {
				throw new CouponAlreadyExistsException("This coupon is already exists !");
			}
		}
		couponDao.createCoupon(coupon, company.getId());
	}

	/**
	 * Deletes a coupon from data base.
	 * 
	 * @param couponId -The id of the coupon you wish to delete.
	 * @throws CouponNotExistsException.
	 * @throws SystemMalfunctionException.
	 */
	public void removeCompanyCoupon(long couponId) throws SystemMalfunctionException, CouponNotExistsException {
		couponDao.removeCoupon(couponId);
	}

	/**
	 * This function allows us to find a coupon from his id.
	 * 
	 * @param couponId.
	 * @return Coupon.
	 * @throws SystemMalfunctionException.
	 * @throws CouponNotExistsException.
	 */
	public RemoteCoupon getCoupon(long couponId) throws SystemMalfunctionException, CouponNotExistsException {
		return couponDao.getCoupon(couponId);
	}

	/**
	 * This function allows us to change the details of a coupon.
	 * 
	 * @param coupon
	 * @throws SystemMalfunctionException
	 * @throws CouponNotExistsException
	 * @throws InvalidUpdateException
	 */
	public void updateCoupon(RemoteCoupon coupon)
			throws SystemMalfunctionException, CouponNotExistsException, InvalidUpdateException {
		couponDao.updateCoupon(coupon);
	}
	
	public void updateCompany(Company company) throws SystemMalfunctionException, NoSuchCompanyException {
		companyDao.updateCompany(company);
	}

	/**
	 * This function returns us the company in question.
	 * 
	 * @return
	 */
	public Company getCompany() {
		return company;
	}

	/**
	 * This function returns us a collection of coupons belonging to this company.
	 * 
	 * @return Collection of coupons.
	 * @throws SystemMalfunctionException
	 * @throws NoSuchCompanyException
	 */
	public Collection<RemoteCoupon> getAllCompanyCoupons() throws SystemMalfunctionException, NoSuchCompanyException {
		return companyDao.getCoupons(company.getId());
	}

	/**
	 * Get all coupons of this company is a specified category.
	 * 
	 * @param category
	 * @return Collection of coupons.
	 * @throws SystemMalfunctionException
	 * @throws NoSuchCompanyException
	 */
	public Collection<RemoteCoupon> getCouponsCompanyByCategory(CouponCategory category)
			throws SystemMalfunctionException, NoSuchCompanyException {
		Collection<RemoteCoupon> coupons = new ArrayList<>();

		for (RemoteCoupon c : getAllCompanyCoupons()) {
			if (c.getCategory() == category.getValue()) {
				coupons.add(c);
			}
		}
		return coupons;
	}

	/**
	 * Get all coupons with a price below a given price.
	 * 
	 * @param price.
	 * @return Collection of coupons.
	 * @throws SystemMalfunctionException.
	 * @throws NoSuchCompanyException.
	 */
	public Collection<RemoteCoupon> getCouponsCompanyBelowPrice(double price)
			throws SystemMalfunctionException, NoSuchCompanyException {
		Collection<RemoteCoupon> coupons = new ArrayList<>();

		for (RemoteCoupon c : getAllCompanyCoupons()) {
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
	 * @throws SystemMalfunctionException.
	 * @throws NoSuchCompanyException.
	 */
	public Collection<RemoteCoupon> getCouponsCompanyBeforeEndDate(LocalDate date)
			throws SystemMalfunctionException, NoSuchCompanyException {
		Collection<RemoteCoupon> coupons = new ArrayList<>();

		for (RemoteCoupon c : getAllCompanyCoupons()) {
			if (c.getEndDate().isBefore(date)) {
				coupons.add(c);
			}
		}
		return coupons;
	}

}
