package facade;

import java.sql.SQLException;

import MyExceptions.InvalidLoginException;
import MyExceptions.SystemMalfunctionException;

public abstract class AbsFacade {

	/**
	 * A helper function for login a user into the system.
	 * 
	 * @param name
	 *            - The user name.
	 * @param password
	 *            - The password.
	 * @param type
	 *            - User type.
	 * @return |A matching facade. For ADMIN login type, an AdminFacade will be
	 *         returned. for COMPANY a CompanyFacade will be returned, for
	 *         CUSTOMER a CustomerFacade will be returned.
	 * @throws InvalidLoginException 
	 * @throws SystemMalfunctionException 
	 * @throws SQLException 
	 */
	public static AbsFacade login(String name, String password, LoginType type) throws InvalidLoginException, SystemMalfunctionException {
		switch (type) {
		case ADMIN:
			return AdminFacade.performLogin(name, password);
		case COMPANY:
			return CompanyFacade.performLogin(name, password);
		case CUSTOMER:
			return CustomerFacade.performLogin(name, password);
		default:
			throw new InvalidLoginException("Login type is not supported.");
		}
	}

}
