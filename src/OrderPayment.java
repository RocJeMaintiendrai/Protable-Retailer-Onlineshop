import java.io.IOException;
import java.io.*;
import java.util.Date;


/* 
	OrderPayment class contains class variables username,ordername,price,image,address,creditcardno.
	OrderPayment  class has a constructor with Arguments username,ordername,price,image,address,creditcardno

	OrderPayment  class contains getters and setters for username,ordername,price,image,address,creditcardno
*/

public class OrderPayment implements Serializable{
	private int orderId;
	private String userName;
	private String orderName;
	private double orderPrice;
	private String userAddress;
	private String creditCardNo;
	private int saleAmount;
	private Date orderTime;


	public OrderPayment(int orderId, String userName, String orderName, double orderPrice, String userAddress, String creditCardNo) {
		this.orderId = orderId;
		this.userName = userName;
		this.orderName = orderName;
		this.orderPrice = orderPrice;
		this.userAddress = userAddress;
		this.creditCardNo = creditCardNo;
	}

	public OrderPayment(int orderId, String orderName, double orderPrice) {
		this.orderId = orderId;
		this.orderName = orderName;
		this.orderPrice = orderPrice;
	}

	public OrderPayment(int orderId, String orderName, double orderPrice, int saleAmount) {
		this.orderId = orderId;
		this.orderName = orderName;
		this.orderPrice = orderPrice;
		this.saleAmount = saleAmount;
	}

	public OrderPayment(String orderName, double orderPrice, int saleAmount) {
		this.orderName = orderName;
		this.orderPrice = orderPrice;
		this.saleAmount = saleAmount;
	}

	public OrderPayment(int saleAmount, Date orderTime) {
		this.saleAmount = saleAmount;
		this.orderTime = orderTime;
	}

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public int getSaleAmount() {
		return saleAmount;
	}

	public void setSaleAmount(int saleAmount) {
		this.saleAmount = saleAmount;
	}

	public String getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}

	public String getCreditCardNo() {
		return creditCardNo;
	}

	public void setCreditCardNo(String creditCardNo) {
		this.creditCardNo = creditCardNo;
	}

	public String getOrderName() {
		return orderName;
	}

	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}


	public double getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(double orderPrice) {
		this.orderPrice = orderPrice;
	}

}