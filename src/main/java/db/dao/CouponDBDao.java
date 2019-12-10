package db.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import MyExceptions.CouponNotExistsException;
import MyExceptions.SystemMalfunctionException;
import MyExceptions.ZeroCouponAmountException;
import common.ConnectionPool;
import common.CouponCategory;
import common.ResourceUtils;
import db.Schema;
import model.remote.RemoteCoupon;

public class CouponDBDao implements CouponDao {
	/**
	 * Constructor
	 */
	public CouponDBDao() {
		try {
			createTable();
		} catch (SystemMalfunctionException e) {
			// Should never happen. Contact the developer. Which is you!

		}
	}

	@Override
	public void createTable() throws SystemMalfunctionException {
		Connection connection = null;
		Statement stmtCreateTableCoupon = null;

		try {
			connection = ConnectionPool.getInstance().getConnection();

			stmtCreateTableCoupon = connection.createStatement();
			stmtCreateTableCoupon.executeUpdate(Schema.getCreateTableCoupon());

		} catch (SQLException e) {
			throw new SystemMalfunctionException("There was a problem creating a coupon table." + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(stmtCreateTableCoupon);
		}
	}

	@Override
	public void createCoupon(RemoteCoupon coupon, long companyId) throws SystemMalfunctionException {
		Connection connection = null;
		PreparedStatement preparedStmtCreateCoupon = null;
		PreparedStatement preparedStmtInsertCompanyCoupon = null;

		try {
			connection = ConnectionPool.getInstance().getConnection();
			preparedStmtCreateCoupon = connection.prepareStatement(Schema.getInsertCoupon(),
					Statement.RETURN_GENERATED_KEYS);

			applyCouponValuesOnStatement(preparedStmtCreateCoupon, coupon);

			preparedStmtCreateCoupon.executeUpdate();

			/*-----------------------------------------------------------------------------------------------------------------------------------------------------*/

			preparedStmtInsertCompanyCoupon = connection.prepareStatement(Schema.getInsertCompanyCoupon());

			preparedStmtInsertCompanyCoupon.setLong(1, companyId);
			preparedStmtInsertCompanyCoupon.setLong(2, getGeneratedPrimaryKey(preparedStmtCreateCoupon));

			preparedStmtInsertCompanyCoupon.executeUpdate();

		} catch (SQLException e) {
			throw new SystemMalfunctionException("impossible to create the coupon !" + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(preparedStmtCreateCoupon, preparedStmtInsertCompanyCoupon);
		}
	}

	private static long getGeneratedPrimaryKey(PreparedStatement statement) throws SQLException {
		long key = -1;
		ResultSet generatedKeys = statement.getGeneratedKeys();

		if (generatedKeys.next()) {
			key = generatedKeys.getLong(1);
		}
		return key;
	}

	@Override
	public void removeCoupon(Long id) throws SystemMalfunctionException, CouponNotExistsException {
		Connection connection = null;
		PreparedStatement preparedStmtRemoveCoupon = null;
		try {
			connection = ConnectionPool.getInstance().getConnection();
			preparedStmtRemoveCoupon = connection.prepareStatement(Schema.getRemoveCoupon());
			preparedStmtRemoveCoupon.setLong(1, id);

			if (preparedStmtRemoveCoupon.executeUpdate() == 0) {
				throw new CouponNotExistsException("this coupon id is not exists !");
			}

		} catch (SQLException e) {
			throw new SystemMalfunctionException("impossible to remove this coupon !" + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(preparedStmtRemoveCoupon);
		}
	}

	@Override
	public void updateCoupon(RemoteCoupon coupon) throws SystemMalfunctionException, CouponNotExistsException {
		Connection connection = null;
		PreparedStatement preparedStmtUpdateCoupon = null;
		try {
			connection = ConnectionPool.getInstance().getConnection();
			preparedStmtUpdateCoupon = connection.prepareStatement(Schema.getUpdateCoupon());

			applyCouponValuesOnStatement(preparedStmtUpdateCoupon, coupon);
			preparedStmtUpdateCoupon.setLong(9, coupon.getId());

			if (preparedStmtUpdateCoupon.executeUpdate() == 0) {
				throw new CouponNotExistsException("this coupon id is not exists !");
			}

		} catch (SQLException e) {
			throw new SystemMalfunctionException("There was a problem update a coupon !" + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(preparedStmtUpdateCoupon);
		}
	}

	private static void applyCouponValuesOnStatement(PreparedStatement stmtCreateCoupon, RemoteCoupon coupon)
			throws SQLException {
		stmtCreateCoupon.setString(1, coupon.getTitle());
		stmtCreateCoupon.setDate(2, Date.valueOf(coupon.getStartDate().plusDays(1)));
		stmtCreateCoupon.setDate(3, Date.valueOf(coupon.getEndDate().plusDays(1)));
		stmtCreateCoupon.setInt(4, coupon.getAmount());
		stmtCreateCoupon.setInt(5, coupon.getCategory());
		stmtCreateCoupon.setString(6, coupon.getMessage());
		stmtCreateCoupon.setDouble(7, coupon.getPrice());
		stmtCreateCoupon.setString(8, coupon.getImageURL());
	}

	@Override
	public void decrementCouponAmount(long couponId) throws SystemMalfunctionException, ZeroCouponAmountException {
		Connection connection = null;
		PreparedStatement preparedStmtDecrementCouponAmount = null;

		try {
			connection = ConnectionPool.getInstance().getConnection();
			preparedStmtDecrementCouponAmount = connection.prepareStatement(Schema.getDecrementCouponAmount());
			preparedStmtDecrementCouponAmount.setLong(1, couponId);

			if (preparedStmtDecrementCouponAmount.executeUpdate() == 0) {
				throw new ZeroCouponAmountException("this coupon id is not exists !");
			}

		} catch (SQLException e) {
			throw new SystemMalfunctionException("impossible to decrement this coupon !");
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(preparedStmtDecrementCouponAmount);
		}
	}

	@Override
	public RemoteCoupon getCoupon(long id) throws SystemMalfunctionException, CouponNotExistsException {
		Connection connection = null;
		PreparedStatement preparedStmtGetCoupon = null;
		ResultSet rs = null;

		RemoteCoupon coupon = new RemoteCoupon();

		try {

			connection = ConnectionPool.getInstance().getConnection();
			preparedStmtGetCoupon = connection.prepareStatement(Schema.getCouponByID());
			preparedStmtGetCoupon.setLong(1, id);

			rs = preparedStmtGetCoupon.executeQuery();

			if (rs.first()) {
				coupon = resultSetToCoupon(rs);
			} else {
				throw new CouponNotExistsException("this id coupon is not exist !");
			}

		} catch (SQLException e) {
			throw new SystemMalfunctionException("impossible to get this coupon !");
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(preparedStmtGetCoupon);
			ResourceUtils.close(rs);
		}
		return coupon;
	}

	public RemoteCoupon resultSetToCoupon(ResultSet resultSet) throws SQLException {

		RemoteCoupon coupon = new RemoteCoupon();

		coupon.setId(resultSet.getLong(1));
		coupon.setTitle(resultSet.getString(2));
		coupon.setStartDate(resultSet.getDate(3).toLocalDate());
		coupon.setEndDate(resultSet.getDate(4).toLocalDate());
		coupon.setAmount(resultSet.getInt(5));
		coupon.setCategory(resultSet.getInt(6));
		coupon.setMessage(resultSet.getString(7));
		coupon.setPrice(resultSet.getDouble(8));
		coupon.setImageURL(resultSet.getString(9));

		return coupon;
	}

	@Override
	public Collection<RemoteCoupon> getAllCoupons() throws SystemMalfunctionException, CouponNotExistsException {
		Connection connection = null;
		Statement stmtGetAllCoupons = null;
		ResultSet rs = null;

		Collection<RemoteCoupon> coupons = new ArrayList<>();

		try {
			connection = ConnectionPool.getInstance().getConnection();
			stmtGetAllCoupons = connection.createStatement();
			rs = stmtGetAllCoupons.executeQuery(Schema.getAllCoupons());

			while (rs.next()) {
				long couponID = rs.getLong(1);
				coupons.add(getCoupon(couponID));
			}

		} catch (SQLException e) {
			throw new SystemMalfunctionException("impossible to get all coupons !");
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(stmtGetAllCoupons);
			ResourceUtils.close(rs);
		}
		return coupons;
	}

	@Override
	public Collection<RemoteCoupon> getCouponsByCategory(CouponCategory category)
			throws SystemMalfunctionException, CouponNotExistsException {
		Connection connection = null;
		PreparedStatement stmtGetAllCouponsByCategory = null;
		ResultSet rs = null;

		Collection<RemoteCoupon> coupons = new ArrayList<>();

		try {
			connection = ConnectionPool.getInstance().getConnection();
			stmtGetAllCouponsByCategory = connection.prepareStatement(Schema.getAllCouponsByCategory());
			stmtGetAllCouponsByCategory.setInt(1, category.ordinal());

			rs = stmtGetAllCouponsByCategory.executeQuery();

			while (rs.next()) {
				long couponID = rs.getLong(1);
				coupons.add(getCoupon(couponID));
			}

		} catch (SQLException e) {
			throw new SystemMalfunctionException("impossible to get coupons by category !");
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(stmtGetAllCouponsByCategory);
			ResourceUtils.close(rs);
		}
		return coupons;
	}

	protected static boolean isExistByID(Long id) throws SystemMalfunctionException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;

		try {
			connection = ConnectionPool.getInstance().getConnection();

			preparedStatement = connection.prepareStatement(Schema.getCouponByID());
			preparedStatement.setLong(1, id);

			rs = preparedStatement.executeQuery();

			if (rs.first()) {
				return true;
			}
		} catch (SQLException e) {
			throw new SystemMalfunctionException("Impossible to check if this coupon exists" + e.getMessage());
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
			ResourceUtils.close(preparedStatement);
			ResourceUtils.close(rs);
		}
		return false;
	}
}
