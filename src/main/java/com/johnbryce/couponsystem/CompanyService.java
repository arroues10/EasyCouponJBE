package com.johnbryce.couponsystem;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

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

import MyExceptions.CouponAlreadyExistsException;
import MyExceptions.CouponNotExistsException;
import MyExceptions.InvalidUpdateException;
import MyExceptions.NoSuchCompanyException;
import MyExceptions.SystemMalfunctionException;
import common.CouponCategory;
import facade.CompanyFacade;
import model.Company;
import model.remote.RemoteCoupon;

@Path("company")
public class CompanyService {

	@Context
	private HttpServletRequest request;

	private CompanyFacade getFacade() {
		HttpSession session = request.getSession(false);
		return (CompanyFacade) session.getAttribute(LoginServlet.COMPANY_FACADE);
	}

	@POST
	@Path("createCoupon")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String createCoupon(RemoteCoupon coupon) {
		try {
			getFacade().createCoupon(coupon);
			return "Coupon created successfully!";

		} catch (CouponAlreadyExistsException | SystemMalfunctionException | CouponNotExistsException e) {
			return e.getMessage();
		}
	}

	@GET
	@Path("getCoupon")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public RemoteCoupon getCoupon(@QueryParam("id") long couponId)
			throws SystemMalfunctionException, CouponNotExistsException {
		return getFacade().getCoupon(couponId);
	}

	@POST
	@Path("updateCoupon")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String updateCoupon(RemoteCoupon coupon) {
		try {
			getFacade().updateCoupon(coupon);
			return "Coupon updated successfully !";

		} catch (SystemMalfunctionException | CouponNotExistsException | InvalidUpdateException e) {
			return e.getMessage();
		}
	}

	@POST
	@Path("updateCompany")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String updateCompany(Company company) {
		try {
			getFacade().updateCompany(company);
			return "Company updated successfully !";

		} catch (SystemMalfunctionException | NoSuchCompanyException e) {
			return e.getMessage();
		}
	}

	@POST
	@Path("removeCompanyCoupon")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String removeCompanyCoupon(@QueryParam("id") long couponId, HttpServletResponse resp) throws IOException {
		try {
			getFacade().removeCompanyCoupon(couponId);
			return "Coupon removed successfully !";
		} catch (SystemMalfunctionException | CouponNotExistsException e) {
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
			return e.getMessage();
		}

	}

	@GET
	@Path("getAllCompanyCoupons")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<RemoteCoupon> getAllCompanyCoupons() {
		try {
			return getFacade().getAllCompanyCoupons();
		} catch (SystemMalfunctionException | NoSuchCompanyException e) {
			return Collections.emptyList();
		}
	}

	@GET
	@Path("getCompany")
	@Produces(MediaType.APPLICATION_JSON)
	public Company getCompany() {
		return getFacade().getCompany();
	}

	@GET
	@Path("getCouponsCompanyByCategory")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<RemoteCoupon> getCouponsCompanyByCategory(@QueryParam("category") String category)
			throws SystemMalfunctionException, NoSuchCompanyException {
		return getFacade().getCouponsCompanyByCategory(CouponCategory.valueOf(category));
	}

	@GET
	@Path("getCompanyCouponsBelowPrice")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<RemoteCoupon> getCouponsCompanyBelowPrice(@QueryParam("price") double price)
			throws SystemMalfunctionException, NoSuchCompanyException {
		return getFacade().getCouponsCompanyBelowPrice(price);
	}

	@GET
	@Path("getCouponsCompanyBeforeEndDate")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<RemoteCoupon> getCouponsCompanyBeforeEndDate(@QueryParam("date") String dateStr)
			throws SystemMalfunctionException, NoSuchCompanyException {

		LocalDate parsedStartDate = LocalDate.parse(dateStr);
		return getFacade().getCouponsCompanyBeforeEndDate(parsedStartDate);
	}
}
