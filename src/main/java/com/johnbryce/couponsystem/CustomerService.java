package com.johnbryce.couponsystem;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import MyExceptions.CouponAlreadyPurshasedException;
import MyExceptions.CouponNotExistsException;
import MyExceptions.InvalidUpdateException;
import MyExceptions.NoSuchCustomerException;
import MyExceptions.SystemMalfunctionException;
import MyExceptions.ZeroCouponAmountException;
import common.CouponCategory;
import facade.CustomerFacade;
import model.Customer;
import model.remote.RemoteCoupon;

@Path("customer")
public class CustomerService {

	@Context
	private HttpServletRequest request;

	private CustomerFacade getFacade() {
		HttpSession session = request.getSession(false);
		return (CustomerFacade) session.getAttribute(LoginServlet.CUSTOMER_FACADE);
	}

	@GET
	@Path("getCustomer")
	@Produces(MediaType.APPLICATION_JSON)
	public Customer getCustomer() throws SystemMalfunctionException, NoSuchCustomerException {
		return getFacade().getCustomer();
	}

	@POST
	@Path("updateCustomer")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String updateCustomer(Customer customer) {
		try {
			getFacade().updateCustomer(customer);
			return "Customer updated successfully !";
		} catch (SystemMalfunctionException | InvalidUpdateException | NoSuchCustomerException | SQLException e) {
			return e.getMessage();
		}
	}

	@POST
	@Path("purchaseCoupon")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String purchaseCoupon(@QueryParam("couponId") long couponId, HttpServletResponse resp) throws IOException {
		try {
			getFacade().purchaseCoupon(couponId);
			return "Coupon purchased successfully !";

		} catch (SystemMalfunctionException | CouponNotExistsException | ZeroCouponAmountException
				| NoSuchCustomerException | CouponAlreadyPurshasedException e) {
//			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
			return e.getMessage();
		}
	}

	@GET
	@Path("getAllCustomerCoupons")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<RemoteCoupon> getAllCustomerCoupons() throws SystemMalfunctionException, NoSuchCustomerException {
		return getFacade().getAllCoupons();
	}

	@GET
	@Path("getCouponsCustomerByCategory")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<RemoteCoupon> getCouponsCustomerByCategory(@QueryParam("category") String category)
			throws SystemMalfunctionException, NoSuchCustomerException {
		return getFacade().getCouponsCustomerByCategory(CouponCategory.valueOf(category));
	}

	@GET
	@Path("getCouponsCustomerBelowPrice")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<RemoteCoupon> getCouponsCustomerBelowPrice(@QueryParam("price") double price)
			throws SystemMalfunctionException, NoSuchCustomerException {
		return getFacade().getCouponsCustomerBelowPrice(price);

	}

	@GET
	@Path("getCouponsCustomerBeforeEndDate")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<RemoteCoupon> getCouponsCustomerBeforeEndDate(@QueryParam("date") String dateStr)
			throws SystemMalfunctionException, NoSuchCustomerException {
		LocalDate parsedStartDate = LocalDate.parse(dateStr);
		return getFacade().getCouponsCustomerBeforeEndDate(parsedStartDate);

	}

}
