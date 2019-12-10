package db.dao;

import java.util.Collection;

import MyExceptions.InvalidLoginException;
import MyExceptions.NoSuchCompanyException;
import MyExceptions.SystemMalfunctionException;
import model.Company;
import model.remote.RemoteCoupon;

public interface CompanyDao {
	void createTable() throws SystemMalfunctionException;

	void createCompany(Company company) throws SystemMalfunctionException;

	void removeCompany(long id) throws SystemMalfunctionException, NoSuchCompanyException;

	void updateCompany(Company company) throws SystemMalfunctionException, NoSuchCompanyException;

	Company getCompany(long id) throws SystemMalfunctionException, NoSuchCompanyException;

	Collection<Company> getAllCompanies() throws SystemMalfunctionException, NoSuchCompanyException;

	Collection<RemoteCoupon> getCoupons(long companyId) throws SystemMalfunctionException, NoSuchCompanyException;

	Company login(String name, String password) throws SystemMalfunctionException, InvalidLoginException;
}
