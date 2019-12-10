package db.dao;

import java.util.Collection;

import MyExceptions.CouponNotExistsException;
import MyExceptions.SystemMalfunctionException;
import MyExceptions.ZeroCouponAmountException;
import common.CouponCategory;
import model.remote.RemoteCoupon;

public interface CouponDao {
	void createTable() throws SystemMalfunctionException;

	void createCoupon(RemoteCoupon coupon, long companyId) throws SystemMalfunctionException;

	void removeCoupon(Long id) throws SystemMalfunctionException, CouponNotExistsException;

	void updateCoupon(RemoteCoupon coupon) throws SystemMalfunctionException, CouponNotExistsException;

	void decrementCouponAmount(long couponId)
			throws SystemMalfunctionException, CouponNotExistsException, ZeroCouponAmountException;

	RemoteCoupon getCoupon(long id) throws SystemMalfunctionException, CouponNotExistsException;

	Collection<RemoteCoupon> getAllCoupons() throws SystemMalfunctionException, CouponNotExistsException;

	Collection<RemoteCoupon> getCouponsByCategory(CouponCategory category)
			throws SystemMalfunctionException, CouponNotExistsException;
}