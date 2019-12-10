package db;

public final class Schema {

	/* Table names. */
	private static final String TABLE_NAME_COUPON = "Coupon";
	private static final String TABLE_NAME_CUSTOMER = "Customer";
	private static final String TABLE_NAME_COMPANY = "Company";
	private static final String TABLE_NAME_CUSTOMER_COUPON = "Customer_Coupon";
	private static final String TABLE_NAME_COMPANY_COUPON = "Company_Coupon";
	/* Common column names. */
	public static final String COL_ID = "id";
	public static final String COL_NAME = "name";
	public static final String COL_PASSWORD = "password";
	public static final String COL_EMAIL = "email";
	/* Coupon columns. */
	private static final String COL_TITLE = "title";
	private static final String COL_START_DATE = "startDate";
	private static final String COL_END_DATE = "endDate";
	private static final String COL_AMOUNT = "amount";
	private static final String COL_CATEGORY = "category";
	private static final String COL_MESSAGE = "message";
	private static final String COL_PRICE = "price";
	private static final String COL_IMAGE_URL = "imageURL";
	/* Join tables columns */
	private static final String COL_CUSTOMER_ID = "customer_id";
	private static final String COL_COUPON_ID = "coupon_id";
	private static final String COL_COMPANY_ID = "company_id";

	public static String getCreateTableCustomer() {
		return "create table if not exists " + TABLE_NAME_CUSTOMER + "(" + COL_ID
				+ " integer primary key auto_increment, " + COL_NAME + " varchar(50), " + COL_PASSWORD
				+ " varchar(50));";
	}

	public static String getCreateTableCoupon() {
		return "create table if not exists " + TABLE_NAME_COUPON + "(" + COL_ID
				+ " integer primary key auto_increment, " + COL_TITLE + " varchar(100), " + COL_START_DATE + " date, "
				+ COL_END_DATE + " date, " + COL_AMOUNT + " integer, " + COL_CATEGORY + " integer, " + COL_MESSAGE
				+ " varchar(255), " + COL_PRICE + " double, " + COL_IMAGE_URL + " varchar(100)" + ");";
	}

	public static String getCreateTableCompany() {
		return "create table if not exists " + TABLE_NAME_COMPANY + "(" + COL_ID
				+ " integer primary key auto_increment, " + COL_NAME + " varchar(50), " + COL_PASSWORD
				+ " varchar(50), " + COL_EMAIL + " varchar(50));";
	}

	public static String getCreateTableCustomerCoupon() {
		return "create table if not exists " + TABLE_NAME_CUSTOMER_COUPON + "(" + COL_CUSTOMER_ID + " integer, "
				+ COL_COUPON_ID + " integer, foreign key (" + COL_CUSTOMER_ID + ") references " + TABLE_NAME_CUSTOMER
				+ "(" + COL_ID + ") on delete cascade, foreign key (" + COL_COUPON_ID + ") references "
				+ TABLE_NAME_COUPON + "(" + COL_ID + ") on delete cascade, primary key (" + COL_CUSTOMER_ID + ", "
				+ COL_COUPON_ID + "));";
	}

	public static String getCreateTableCompanyCoupon() {
		return "create table if not exists " + TABLE_NAME_COMPANY_COUPON + "(" + COL_COMPANY_ID + " integer, "
				+ COL_COUPON_ID + " integer, foreign key (" + COL_COMPANY_ID + ") references " + TABLE_NAME_COMPANY
				+ "(" + COL_ID + ") on delete cascade, foreign key (" + COL_COUPON_ID + ") references "
				+ TABLE_NAME_COUPON + "(" + COL_ID + ") on delete cascade,primary key (" + COL_COMPANY_ID + ", "
				+ COL_COUPON_ID + "));";
	}

	public static String getCreateCustomer() {
		return "insert into " + TABLE_NAME_CUSTOMER + "(" + COL_NAME + ", " + COL_PASSWORD + ")" + " values(?, ?);";
	}

	public static String getRemoveCustomer() {
		return "delete from " + TABLE_NAME_CUSTOMER + " where " + COL_ID + " = ?;";

	}

	public static String getCustomerByID() {
		return "select * from " + TABLE_NAME_CUSTOMER + " where " + COL_ID + " = ?;";
	}

	public static String getUpdateCustomer() {
		return "update " + TABLE_NAME_CUSTOMER + " set " + COL_NAME + " = ?, " + COL_PASSWORD + " = ? where " + COL_ID
				+ " = ?;";
	}

	public static String getTableCustomer() {
		return "select * from " + TABLE_NAME_CUSTOMER + ";";
	}

	public static String getInsertCoupon() {
		return "insert into " + TABLE_NAME_COUPON + "(" + COL_TITLE + ", " + COL_START_DATE + ", " + COL_END_DATE + ", "
				+ COL_AMOUNT + ", " + COL_CATEGORY + ", " + COL_MESSAGE + ", " + COL_PRICE + ", " + COL_IMAGE_URL
				+ ") values (?, ?, ?, ?, ?, ?, ?, ?);";
	}

	public static String getInsertCustomerCoupon() {
		return "insert into " + TABLE_NAME_CUSTOMER_COUPON + " (" + COL_CUSTOMER_ID + ", " + COL_COUPON_ID
				+ ") values (?, ?);";
	}

	public static String getCouponsByCustomerID() {
		return "select * from " + TABLE_NAME_COUPON + " t1 inner join " + TABLE_NAME_CUSTOMER_COUPON + " t2 on t1."
				+ COL_ID + " = t2." + COL_COUPON_ID + " where t2." + COL_CUSTOMER_ID + " = ?;";
	}

	public static String getInsertCompanyCoupon() {
		return "insert into " + TABLE_NAME_COMPANY_COUPON + "(" + COL_COMPANY_ID + ", " + COL_COUPON_ID
				+ ") values (?, ?);";
	}

	public static String getInsertCompany() {
		return "insert into " + TABLE_NAME_COMPANY + " (" + COL_NAME + ", " + COL_PASSWORD + ", " + COL_EMAIL
				+ ") values (?, ?, ?);";
	}

	public static String getRemoveCoupon() {
		return "delete from " + TABLE_NAME_COUPON + " where " + COL_ID + " = ?;";
	}

	public static String getUpdateCoupon() {
		return "update " + TABLE_NAME_COUPON + " set " + COL_TITLE + " = ?, " + COL_START_DATE + " = ?, " + COL_END_DATE
				+ " = ?, " + COL_AMOUNT + " = ?, " + COL_CATEGORY + " = ?, " + COL_MESSAGE + " = ?, " + COL_PRICE
				+ " = ?," + COL_IMAGE_URL + " = ? where " + COL_ID + " = ?;";
	}

	public static String getDecrementCouponAmount() {
		return "update " + TABLE_NAME_COUPON + " set " + COL_AMOUNT + " = " + COL_AMOUNT + " -1 where " + COL_ID
				+ " = ? and " + COL_AMOUNT + " > 0";
	}

	public static String getCouponByID() {
		return "select * from " + TABLE_NAME_COUPON + " where " + COL_ID + " = ?;";
	}

	public static String getAllCoupons() {
		return "select * from " + TABLE_NAME_COUPON + ";";
	}

	public static String getAllCouponsByCategory() {
		return "select * from " + TABLE_NAME_COUPON + " where " + COL_CATEGORY + " = ?;";
	}

	public static String getRemoveCompany() {
		return "delete from " + TABLE_NAME_COMPANY + " where " + COL_ID + " = ?;";
	}

	public static String getUpdateCompanyById() {
		return "update " + TABLE_NAME_COMPANY + " set " + COL_NAME + " = ?, " + COL_PASSWORD + " = ?, " + COL_EMAIL
				+ " = ? where " + COL_ID + " = ?;";
	}

	public static String getSelectCompanyById() {
		return "select * from " + TABLE_NAME_COMPANY + " where " + COL_ID + " = ?;";
	}

	public static String getSelectCompanyCouponInnerJoinById() {
		return "select * from " + TABLE_NAME_COUPON + " t1 inner join " + TABLE_NAME_COMPANY_COUPON
				+ " t2 on t1.id = t2." + COL_COUPON_ID + " where t2." + COL_COMPANY_ID + " = ?;";
	}

	public static String getAllCompanies() {
		return "select * from " + TABLE_NAME_COMPANY + ";";
	}

	public static String getCompanyByNamePassword() {
		return "select * from " + TABLE_NAME_COMPANY + " where " + COL_NAME + " = ? and " + COL_PASSWORD + " = ?;";
	}

	public static String getRowCustomerCoupon() {
		return "select * from " + TABLE_NAME_CUSTOMER_COUPON + " where " + COL_CUSTOMER_ID + " = ? and " + COL_COUPON_ID
				+ " = ?;";
	}

}
