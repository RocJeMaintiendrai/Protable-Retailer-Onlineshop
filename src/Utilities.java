import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/Utilities")

/*
	Utilities class contains class variables of type HttpServletRequest, PrintWriter,String and HttpSession.
	Utilities class has a constructor with  HttpServletRequest, PrintWriter variables.
*/

public class Utilities extends HttpServlet{
	HttpServletRequest req;
	PrintWriter pw;
	String url;
	HttpSession session;
	public Utilities(HttpServletRequest req, PrintWriter pw) {
		this.req = req;
		this.pw = pw;
		this.url = this.getFullURL();
		this.session = req.getSession(true);
	}



	/*  Printhtml Function gets the html file name as function Argument,
		If the html file name is Header.html then It gets Username from session variables.
		Account ,Cart Information ang Logout Options are Displayed*/

	public void printHtml(String file) {
		String result = HtmlToString(file);
		//to print the right navigation in header of username cart and logout etc
		if (file == "Header.html") {
			result=result+"<div id='menu' style='float: right;'><ul>";
			if (session.getAttribute("username")!=null){
				String username = session.getAttribute("username").toString();
				username = Character.toUpperCase(username.charAt(0)) + username.substring(1);
				result = result + "<li><a href='ViewOrder'><span class='glyphicon'>ViewOrder</span></a></li>"
								+ "<li><a><span class='glyphicon'>Hello,"+username+"</span></a></li>"
								+ "<li><a href='Account'><span class='glyphicon'>Account</span></a></li>"
								+ "<li><a href='Logout'><span class='glyphicon'>Logout</span></a></li>";
			}
			else
				result = result +"<li><a href='ViewOrder'><span class='glyphicon'>View Order</span></a></li>"+ "<li><a href='Login'><span class='glyphicon'>Login</span></a></li>";
			result = result +"<li><a href='Cart'><span class='glyphicon'>Cart("+CartCount()+")</span></a></li></ul></div></div><div id='page'>";
			pw.print(result);
		} else
			pw.print(result);
	}


	/*  getFullURL Function - Reconstructs the URL user request  */

	public String getFullURL() {
		String scheme = req.getScheme();
		String serverName = req.getServerName();
		int serverPort = req.getServerPort();
		String contextPath = req.getContextPath();
		StringBuffer url = new StringBuffer();
		url.append(scheme).append("://").append(serverName);

		if ((serverPort != 80) && (serverPort != 443)) {
			url.append(":").append(serverPort);
		}
		url.append(contextPath);
		url.append("/");
		return url.toString();
	}

	/*  HtmlToString - Gets the Html file and Converts into String and returns the String.*/
	public String HtmlToString(String file) {
		String result = null;
		try {
			String webPage = url + file;
			URL url = new URL(webPage);
			URLConnection urlConnection = url.openConnection();
			InputStream is = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);

			int numCharsRead;
			char[] charArray = new char[1024];
			StringBuffer sb = new StringBuffer();
			while ((numCharsRead = isr.read(charArray)) > 0) {
				sb.append(charArray, 0, numCharsRead);
			}
			result = sb.toString();
		}
		catch (Exception e) {
		}
		return result;
	}

	/*  logout Function removes the username , usertype attributes from the session variable*/

	public void logout(){
		session.removeAttribute("username");
		session.removeAttribute("usertype");
	}

	/*  logout Function checks whether the user is loggedIn or Not*/

	public boolean isLoggedin(){
		if (session.getAttribute("username")==null)
			return false;
		return true;
	}

	/*  username Function returns the username from the session variable.*/

	public String username(){
		if (session.getAttribute("username")!=null)
			return session.getAttribute("username").toString();
		return null;
	}

	/*  usertype Function returns the usertype from the session variable.*/
	public String usertype(){
		if (session.getAttribute("usertype")!=null)
			return session.getAttribute("usertype").toString();
		return null;
	}

	/*  getUser Function checks the user is a customer or retailer or manager and returns the user class variable.*/
	public User getUser(){
		String usertype = usertype();
		HashMap<String, User> hm=new HashMap<String, User>();
		String TOMCAT_HOME = System.getProperty("catalina.home");
		try
		{
			FileInputStream fileInputStream=new FileInputStream(new File(TOMCAT_HOME+"\\webapps\\Tutorial_1\\UserDetails.txt"));
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			hm= (HashMap)objectInputStream.readObject();
		}
		catch(Exception e)
		{
		}
		User user = hm.get(username());
		return user;
	}

	/*  getCustomerOrders Function gets  the Orders for the user*/
	public ArrayList<OrderItem> getCustomerOrders(){
		ArrayList<OrderItem> order = new ArrayList<OrderItem>();
		if(OrdersHashMap.orders.containsKey(username()))
			order= OrdersHashMap.orders.get(username());
		return order;
	}

	/*  getOrdersPaymentSize Function gets  the size of OrderPayment */
	public int getOrderPaymentSize(){
		HashMap<Integer, ArrayList<OrderPayment>> orderPayments = new HashMap<Integer, ArrayList<OrderPayment>>();
		String TOMCAT_HOME = System.getProperty("catalina.home");
		try
		{
			FileInputStream fileInputStream = new FileInputStream(new File(TOMCAT_HOME+"\\webapps\\Tutorial_1\\PaymentDetails.txt"));
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			orderPayments = (HashMap)objectInputStream.readObject();
		}
		catch(Exception e)
		{

		}
		int size=0;
		for(Map.Entry<Integer, ArrayList<OrderPayment>> entry : orderPayments.entrySet()){
			size=size + 1;

		}
		return size;
	}

	/*  CartCount Function gets  the size of User Orders*/
	public int CartCount(){
		if(isLoggedin())
			return getCustomerOrders().size();
		return 0;
	}

	/* StoreProduct Function stores the Purchased product in Orders HashMap according to the User Names.*/

	public void storeProduct(String name,String type,String maker, String acc){
		if(!OrdersHashMap.orders.containsKey(username())){
			ArrayList<OrderItem> arr = new ArrayList<OrderItem>();
			OrdersHashMap.orders.put(username(), arr);
		}
		ArrayList<OrderItem> orderItems = OrdersHashMap.orders.get(username());
		if(type.equals("consoles")){
			Console console;
			console = SaxParserDataStore.consoles.get(name);
			OrderItem orderitem = new OrderItem(console.getName(), console.getPrice(), console.getImage(), console.getRetailer());
			orderItems.add(orderitem);
		}
		if(type.equals("games")){
			Game game = null;
			game = SaxParserDataStore.games.get(name);
			OrderItem orderitem = new OrderItem(game.getName(), game.getPrice(), game.getImage(), game.getRetailer());
			orderItems.add(orderitem);
		}
		if(type.equals("tablets")){
			Tablet tablet = null;
			tablet = SaxParserDataStore.tablets.get(name);
			OrderItem orderitem = new OrderItem(tablet.getName(), tablet.getPrice(), tablet.getImage(), tablet.getRetailer());
			orderItems.add(orderitem);
		}
		if(type.equals("smartspeakers")){
			Smartspeaker smartspeaker = null;
			smartspeaker = SaxParserDataStore.smartspeakers.get(name);
			OrderItem orderitem = new OrderItem(smartspeaker.getName(), smartspeaker.getPrice(), smartspeaker.getImage(), smartspeaker.getRetailer());
			orderItems.add(orderitem);
		}
		if(type.equals("accessories")){
			Accessory accessory = SaxParserDataStore.accessories.get(name);
			OrderItem orderitem = new OrderItem(accessory.getName(), accessory.getPrice(), accessory.getImage(), accessory.getRetailer());
			orderItems.add(orderitem);
		}

	}
	// store the payment details for orders
	public void storePayment(int orderId,
	                         String orderName,double orderPrice,String userAddress,String creditCardNo){
		HashMap<Integer, ArrayList<OrderPayment>> orderPayments= new HashMap<Integer, ArrayList<OrderPayment>>();
		String TOMCAT_HOME = System.getProperty("catalina.home");
		// get the payment details file
		try
		{
			FileInputStream fileInputStream = new FileInputStream(new File(TOMCAT_HOME+"\\webapps\\Tutorial_1"
							                                                               + "\\PaymentDetails.txt"));
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			orderPayments = (HashMap)objectInputStream.readObject();
		}
		catch(Exception e)
		{

		}
		if(orderPayments==null)
		{
			orderPayments = new HashMap<Integer, ArrayList<OrderPayment>>();
		}
		// if there exist order id already add it into same list for order id or create a new record with order id

		if(!orderPayments.containsKey(orderId)){
			ArrayList<OrderPayment> arr = new ArrayList<OrderPayment>();
			orderPayments.put(orderId, arr);
		}
		ArrayList<OrderPayment> listOrderPayment = orderPayments.get(orderId);
		OrderPayment orderpayment = new OrderPayment(orderId,username(),orderName,orderPrice,userAddress,creditCardNo);
		listOrderPayment.add(orderpayment);

		// add order details into file

		try
		{
			FileOutputStream fileOutputStream = new FileOutputStream(new File(TOMCAT_HOME+"\\webapps\\Tutorial_1"
							                                                                  + "\\PaymentDetails.txt"));
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(orderPayments);
			objectOutputStream.flush();
			objectOutputStream.close();
			fileOutputStream.close();
		}
		catch(Exception e)
		{
			System.out.println("inside exception file not written properly");
		}
	}


	/* getConsoles Functions returns the Hashmap with all consoles in the store.*/

	public HashMap<String, Console> getConsoles(){
		HashMap<String, Console> hm = new HashMap<String, Console>();
		hm.putAll(SaxParserDataStore.consoles);
		return hm;
	}

	/* getGames Functions returns the  Hashmap with all Games in the store.*/

	public HashMap<String, Game> getGames(){
		HashMap<String, Game> hm = new HashMap<String, Game>();
		hm.putAll(SaxParserDataStore.games);
		return hm;
	}

	/* getTablets Functions returns the Hashmap with all Tablet in the store.*/

	public HashMap<String, Tablet> getTablets(){
		HashMap<String, Tablet> hm = new HashMap<String, Tablet>();
		hm.putAll(SaxParserDataStore.tablets);
		return hm;
	}

	/* getProducts Functions returns the Arraylist of consoles in the store.*/

	public ArrayList<String> getProducts(){
		ArrayList<String> ar = new ArrayList<String>();
		for(Map.Entry<String, Console> entry : getConsoles().entrySet()){
			ar.add(entry.getValue().getName());
		}
		return ar;
	}

	/* getProducts Functions returns the Arraylist of games in the store.*/

	public ArrayList<String> getProductsGame(){
		ArrayList<String> ar = new ArrayList<String>();
		for(Map.Entry<String, Game> entry : getGames().entrySet()){
			ar.add(entry.getValue().getName());
		}
		return ar;
	}

	/* getProducts Functions returns the Arraylist of Tablets in the store.*/

	public ArrayList<String> getProductsTablets(){
		ArrayList<String> ar = new ArrayList<String>();
		for(Map.Entry<String, Tablet> entry : getTablets().entrySet()){
			ar.add(entry.getValue().getName());
		}
		return ar;
	}

	//remove item from cart
	public void removeItemFromCart(String itemName){

		ArrayList<OrderItem> orderItems = OrdersHashMap.orders.get(username());
		int index = 0;
		//traverse orderItem to find the index of item to be removed
		for(OrderItem oi : orderItems) {
			if(oi.getName().equals(itemName)) {
				break;
			} else index++;
		}
		orderItems.remove(index);

	}

	public void removeOldOrder(int orderId, String orderName, String customerName) {
		String TOMCAT_HOME = System.getProperty("catalina.home");
		HashMap<Integer, ArrayList<OrderPayment>> orderPayments = new HashMap<Integer, ArrayList<OrderPayment>>();
		ArrayList<OrderPayment> ListOrderPayment = new ArrayList<OrderPayment>();

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(TOMCAT_HOME + "\\webapps\\Tutorial_1\\PaymentDetails.txt"));
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			orderPayments = (HashMap) objectInputStream.readObject();
		} catch (Exception e) {

		}
		//get the exact order with same ordername and add it into cancel list to remove it later
		for (OrderPayment oi : orderPayments.get(orderId)) {
			if (oi.getOrderName().equals(orderName) && oi.getUserName().equals(customerName)) {
				ListOrderPayment.add(oi);
			}
		}
		//remove all the orders from hashmap that exist in cancel list
		orderPayments.get(orderId).removeAll(ListOrderPayment);
		if (orderPayments.get(orderId).size() == 0) {
			orderPayments.remove(orderId);
		}

		//save the updated hashmap with removed order to the file
		updateOrderFile(orderPayments);
	}

	//将更新后的数据保存到文件中
	public boolean updateOrderFile(HashMap<Integer, ArrayList<OrderPayment>> orderPayments) {
		String TOMCAT_HOME = System.getProperty("catalina.home");

		try {
			FileOutputStream fileOutputStream = new FileOutputStream(new File(TOMCAT_HOME + "\\webapps\\Tutorial_1"
							                                                                  + "\\PaymentDetails.txt"));
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(orderPayments);
			objectOutputStream.flush();
			objectOutputStream.close();
			fileOutputStream.close();
		} catch (Exception e) {

		}
		return true;
	}

	//salesman
	public boolean isContainsStr(String string) {
		String regex = ".*[a-zA-Z]+.*";
		Matcher m = Pattern.compile(regex).matcher(string);
		return m.matches();
	}

	public boolean isItemExist(String itemCatalog, String itemName) {
		HashMap<String, Object> hm = new HashMap<String, Object>();

		switch(itemCatalog) {
			case "Console":
				hm.putAll(SaxParserDataStore.consoles);
				break;
			case "Game":
				hm.putAll(SaxParserDataStore.games);
				break;
			case "Tablet":
				hm.putAll(SaxParserDataStore.tablets);
				break;
			case "Samrtspeaker":
				hm.putAll(SaxParserDataStore.smartspeakers);
				break;
			case "Accessory":
				hm.putAll(SaxParserDataStore.accessories);
				break;
		}
		return true;
	}

	public void storeNewOrder(int orderId, String orderName, String customerName, double orderPrice, String userAddress, String creditCardNo) {
		HashMap<Integer, ArrayList<OrderPayment>> orderPayments = new HashMap<Integer, ArrayList<OrderPayment>>();
		String TOMCAT_HOME = System.getProperty("catalina.home");
		try {
			FileInputStream fileInputStream = new FileInputStream(new File(TOMCAT_HOME + "\\webapps\\Tutorial_1\\PaymentDetails.txt"));
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			orderPayments = (HashMap) objectInputStream.readObject();
		} catch (Exception ignored) {

		}
		if (orderPayments == null) {
			orderPayments = new HashMap<Integer, ArrayList<OrderPayment>>();
		}
		// if there exist order id already add it into same list for order id or create a new record with order id
		if (!orderPayments.containsKey(orderId)) {
			ArrayList<OrderPayment> arr = new ArrayList<OrderPayment>();
			orderPayments.put(orderId, arr);
		}
		ArrayList<OrderPayment> listOrderPayment = orderPayments.get(orderId);

		OrderPayment orderpayment = new OrderPayment(orderId, customerName, orderName, orderPrice, userAddress, creditCardNo);
		listOrderPayment.add(orderpayment);
		// add order details into file
		updateOrderFile(orderPayments);

	}

	//sotremanager
	public String getRealPath(String catalog) {
		String realPath = "images";

		switch (catalog) {
			case "Console":
				realPath = realPath + "/consoles";
				break;
			case "Game":
				realPath = realPath + "/games";
				break;
			case "Tablet":
				realPath = realPath + "/tablets";
				break;
			case "Smartspeaker":
				realPath = realPath + "/smartspeaker";
				break;
			case "Accessory":
				realPath = realPath + "/accessory";
				break;
		}
		return realPath;
	}

	public boolean storeNewProduct(Map<String, Object> map) {
		String id = String.valueOf(map.get("id"));
		String name = String.valueOf(map.get("name"));
		double price = Double.parseDouble(String.valueOf(map.get("price")));
		String image = String.valueOf(map.get("image"));
		String retailer = String.valueOf(map.get("manufacturer"));
		String condition = String.valueOf(map.get("condition"));
		double discount = Double.parseDouble(String.valueOf(map.get("discount")));
		String catalog = String.valueOf(map.get("productCatalog"));

		switch (catalog) {
			case "Console":
				Console console = new Console();
				console.setId(id);
				console.setName(name);
				console.setPrice(price);
				console.setImage(image);
				console.setRetailer(retailer);
				console.setCondition(condition);
				console.setDiscount(discount);
				SaxParserDataStore.consoles.put(id, console);
				return true;
			case "Game":
				Game game = new Game();
				game.setId(id);
				game.setName(name);
				game.setPrice(price);
				game.setImage(image);
				game.setRetailer(retailer);
				game.setCondition(condition);
				game.setDiscount(discount);
				SaxParserDataStore.games.put(id, game);
				return true;
			case "Tablet":
				Tablet tablet = new Tablet();
				tablet.setId(id);
				tablet.setName(name);
				tablet.setPrice(price);
				tablet.setImage(image);
				tablet.setRetailer(retailer);
				tablet.setCondition(condition);
				tablet.setDiscount(discount);
				SaxParserDataStore.tablets.put(id, tablet);
				return true;
			case "Smartspeaker":
				Smartspeaker smartspeaker = new Smartspeaker();
				smartspeaker.setId(id);
				smartspeaker.setName(name);
				smartspeaker.setPrice(price);
				smartspeaker.setImage(image);
				smartspeaker.setRetailer(retailer);
				smartspeaker.setCondition(condition);
				smartspeaker.setDiscount(discount);
				SaxParserDataStore.smartspeakers.put(id, smartspeaker);
				return true;
			case "Accessory":
				Accessory accessory = new Accessory();
				accessory.setId(id);
				accessory.setName(name);
				accessory.setPrice(price);
				accessory.setImage(image);
				accessory.setRetailer(retailer);
				accessory.setCondition(condition);
				accessory.setDiscount(discount);
				SaxParserDataStore.accessories.put(id, accessory);
				return true;
		}
		return false;
	}





}