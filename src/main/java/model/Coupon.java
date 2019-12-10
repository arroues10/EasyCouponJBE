package model;

import java.time.LocalDate;

public class Coupon {

	public static final int NO_ID = -1;

	private long id = NO_ID;
	private String title;
	private LocalDate startDate;
	private LocalDate endDate;
	private int amount;
	private int category;
	private String message;
	private double price;
	private String imageURL;

	public Coupon(long id, String title, LocalDate startDate, LocalDate endDate, int amount, int category,
			String message, double price, String imageURL) {
		super();
		this.id = id;
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.amount = amount;
		this.category = category;
		this.message = message;
		this.price = price;
		this.imageURL = imageURL;
	}

	public Coupon() {

	}

	public void setId(long id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public static int getNoId() {
		return NO_ID;
	}

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public int getAmount() {
		return amount;
	}

	public int getCategory() {
		return category;
	}

	public String getMessage() {
		return message;
	}

	public double getPrice() {
		return price;
	}

	public String getImageURL() {
		return imageURL;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		// H.W. Google: What is this >>> ?
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coupon other = (Coupon) obj;
		if (id != other.id)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Coupon [id=" + id + "]";
	}

}
