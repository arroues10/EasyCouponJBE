package com.johnbryce.couponsystem;

import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import MyExceptions.CompanyAlreadyExistsException;
import MyExceptions.CouponNotExistsException;
import MyExceptions.CustomerAlreadyExistException;
import MyExceptions.NoSuchCompanyException;
import MyExceptions.NoSuchCustomerException;
import MyExceptions.SystemMalfunctionException;
import facade.AdminFacade;
import model.Company;
import model.Customer;
import model.remote.RemoteCoupon;

@Path("admin")
public class AdminServices {

	@Context
	private HttpServletRequest request;

	private AdminFacade getFacade() {
		HttpSession session = request.getSession(false);
		return (AdminFacade) session.getAttribute(LoginServlet.ADMIN_FACADE);
	}

	@POST
	@Path("createCompany")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String createCompany(Company company) {
		try {
			getFacade().createCompany(company);
		} catch (SystemMalfunctionException | CompanyAlreadyExistsException | NoSuchCompanyException e) {
			e.getMessage();
		}

		return "Company created successfully";
	}

	@POST
	@Path("removeCompany")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String removeCompany(@QueryParam("id") long companyId) {
		try {
			getFacade().removeCompany(companyId);
			return "Company removed successfully!";
		} catch (SystemMalfunctionException | NoSuchCompanyException e) {
			return e.getMessage();
		}
	}

	@POST
	@Path("createCustomer")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String createCustomer(Customer customer) {
		try {
			getFacade().createCustomer(customer);
		} catch (SystemMalfunctionException | NoSuchCustomerException | CustomerAlreadyExistException e) {
			e.getMessage();
		}

		return "Customer created successfully";
	}

	@POST
	@Path("removeCustomer")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String removeCustomer(@QueryParam("id") long customerId) {
		try {
			getFacade().removeCustomer(customerId);
			return "Customer removed successfully!";
		} catch (SystemMalfunctionException | NoSuchCustomerException e) {
			return e.getMessage();
		}
	}

	@GET
	@Path("getAllCustomers")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Customer> getAllCustomers() throws SystemMalfunctionException, NoSuchCustomerException {
		Collection<Customer> allCustomers = getFacade().getAllCustomers();

		if (!allCustomers.isEmpty()) {
			return allCustomers;
		}
		return Collections.emptyList();
	}

	@GET
	@Path("getAllCompanies")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Company> getAllCompanies() throws SystemMalfunctionException, NoSuchCompanyException {
		Collection<Company> allCompanies = getFacade().getAllCompanies();

		if (!allCompanies.isEmpty()) {
			System.out.println("AdminServices.getAllCompanies()");
			return allCompanies;
		}
		return Collections.emptyList();
	}

	@GET
	@Path("getAllCoupons")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<RemoteCoupon> getAllCoupons() throws SystemMalfunctionException, CouponNotExistsException {
		Collection<RemoteCoupon> allCoupons = getFacade().getAllCoupons();

		if (!allCoupons.isEmpty()) {
			return allCoupons;
		}
		return Collections.emptyList();
	}
}
