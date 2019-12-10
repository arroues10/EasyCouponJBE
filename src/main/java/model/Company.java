package model;

import java.util.HashSet;
import java.util.Set;

import model.remote.RemoteCoupon;

public class Company {
	public static final int NO_ID = -1;

	private long id = NO_ID;
	private String name;
	private String password;
	private String email;
	private Set<RemoteCoupon> coupons;

	public Company() {
		// H.W. What is HashSet ?
		coupons = new HashSet<>();
	}

	public Company(long id, String name, String password, String email) {
		this();
		this.id = id;
		this.name = name;
		this.password = password;
		this.email = email;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<RemoteCoupon> getCoupons() {
		return coupons;
	}

	public void setCoupons(Set<RemoteCoupon> coupons) {
		this.coupons = coupons;
	}

	@Override
	public String toString() {
		return name + "," + password + "," + email + "," + coupons;
	}
}
