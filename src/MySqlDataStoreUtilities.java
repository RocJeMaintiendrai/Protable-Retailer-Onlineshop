import java.sql.*;
import java.util.*;

public class MySqlDataStoreUtilities {

    static Connection conn = null;

    public static void getConnection() {

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CSP584?useUnicode=true&characterEncoding=utf8", "root", "admin123");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static boolean deleteOrder(int orderId) {

        try {
            getConnection();
            String deleteOrderQuery = "Delete from orders where OrderId=?";
            PreparedStatement pst = conn.prepareStatement(deleteOrderQuery);
            pst.setInt(1, orderId);
            pst.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;

    }

    public static void insertOrder(int orderId,String userName, String orderName, double orderPrice, String userAddress, String creditCardNo) {

        try{
            getConnection();
            String insertIntoCustomerOrderQuery = "insert into orders (orderID, userName, orderName, orderPrice, userAddress, creditCardNo) VALUES (?,?,?,?,?,?);";
            PreparedStatement pst = conn.prepareStatement(insertIntoCustomerOrderQuery);
            pst.setInt(1, orderId);
            pst.setString(2, userName);
            pst.setString(3, orderName);
            pst.setDouble(4, orderPrice);
            pst.setString(5, userAddress);
            pst.setString(6, creditCardNo);
            pst.execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static HashMap<Integer,ArrayList<OrderPayment>> selectOrder() {

        HashMap<Integer, ArrayList<OrderPayment>> orderPayments = new HashMap<Integer, ArrayList<OrderPayment>>();

        try {
            getConnection();
            //select the table
            String selectOrderQuery = "select * from orders";
            PreparedStatement pst = conn.prepareStatement(selectOrderQuery);
            ResultSet rs = pst.executeQuery();
            ArrayList<OrderPayment> orderList = new ArrayList<OrderPayment>();
            while (rs.next()) {
                if (!orderPayments.containsKey(rs.getInt("OrderId"))) {
                    ArrayList<OrderPayment> arr = new ArrayList<OrderPayment>();
                    orderPayments.put(rs.getInt("orderId"), arr);
                }
                ArrayList<OrderPayment> listOrderPayment = orderPayments.get(rs.getInt("OrderId"));
                System.out.println("data is" + rs.getInt("OrderId") + orderPayments.get(rs.getInt("OrderId")));

                //add to orderpayment hashmap
                OrderPayment order = new OrderPayment(rs.getInt("OrderId"), rs.getString("userName"), rs.getString("orderName"), rs.getDouble("orderPrice"), rs.getString("userAddress"), rs.getString("creditCardNo"));
                listOrderPayment.add(order);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
        return orderPayments;

    }

    public static boolean insertUser(String username, String password, String rePassword, String userType) {
        try {

            getConnection();
            String insertIntoCustomerRegisterQuery = "INSERT INTO user(username,password,repassword,usertype) "
                            + "VALUES (?,?,?,?);";

            PreparedStatement pst = conn.prepareStatement(insertIntoCustomerRegisterQuery);
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, rePassword);
            pst.setString(4, userType);
            pst.execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    public static HashMap<String, User> selectUser() {
        HashMap<String, User> hm = new HashMap<String, User>();
        try {
            getConnection();
            Statement stmt = conn.createStatement();
            String selectCustomerQuery = "select * from user";
            ResultSet rs = stmt.executeQuery(selectCustomerQuery);
            while (rs.next()) {
                User user = new User(rs.getString("username"), rs.getString("password"), rs.getString("usertype"));
                hm.put(rs.getString("username"), user);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return hm;
    }

    public static HashMap<String, Product> selectInventory() {
        HashMap<String, Product> hm = new HashMap<String, Product>();
        try {
            getConnection();

            String selectAcc = "select * from Productdetails";
            PreparedStatement pst = conn.prepareStatement(selectAcc);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Product product = new Product(rs.getString("productName"), rs.getDouble("productPrice"), Integer.parseInt(rs.getString("inventory")));
                hm.put(rs.getString("Id"), product);
                product.setId(rs.getString("Id"));
            }
        } catch (Exception e) {
        }
        return hm;
    }

    public static HashMap<String, Product> selectOnSale() {
        HashMap<String, Product> hm = new HashMap<String, Product>();
        try {
            getConnection();

            String selectAcc = "select * from Productdetails where productCondition = ?";
            PreparedStatement pst = conn.prepareStatement(selectAcc);
            pst.setString(1, "1");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Product product = new Product(rs.getString("productName"), rs.getDouble("productPrice"), Integer.parseInt(rs.getString("inventory")));
                hm.put(rs.getString("Id"), product);
                product.setId(rs.getString("Id"));
            }
        } catch (Exception e) {
        }
        return hm;
    }

    public static HashMap<String, Product> selectRebate() {
        HashMap<String, Product> hm = new HashMap<String, Product>();
        try {
            getConnection();

            String selectAcc = "select * from Productdetails where productDiscount > 0";
            PreparedStatement pst = conn.prepareStatement(selectAcc);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Product product = new Product(rs.getString("productName"), rs.getDouble("productPrice"), Double.parseDouble(rs.getString("productDiscount")));
                hm.put(rs.getString("Id"), product);
                product.setId(rs.getString("Id"));
            }
        } catch (Exception e) {
        }
        return hm;
    }

    public static HashMap<String, OrderPayment> selectSaleAmount() {
        HashMap<String, OrderPayment> hm = new HashMap<String, OrderPayment>();
        try {
            getConnection();

            String selectAcc = "select DISTINCT(temp.orderName),temp.saleAmount,orders.orderPrice from orders, (select orderName, count(orderName) as saleAmount from orders group by orderName) as temp where orders.orderName = temp.orderName";
            PreparedStatement pst = conn.prepareStatement(selectAcc);
            ResultSet rs = pst.executeQuery();

            int i = 0;
            while (rs.next()) {
                OrderPayment orderPayment = new OrderPayment(rs.getString("orderName"), rs.getDouble("orderPrice"), rs.getInt("saleAmount"));
                i++;
                hm.put(String.valueOf(i), orderPayment);
                //orderPayment.setOrderId(Integer.parseInt(rs.getString("Id")));
            }
        } catch (Exception e) {
        }
        return hm;
    }

    public static HashMap<String, OrderPayment> selectDailyTransaction() {
        HashMap<String, OrderPayment> hm = new HashMap<String, OrderPayment>();
        try {
            getConnection();

            String selectAcc = "SELECT count(orderTime) as soldAmount, orderTime from orders group by orderTime";
            PreparedStatement pst = conn.prepareStatement(selectAcc);
            ResultSet rs = pst.executeQuery();

            int i = 0;
            while (rs.next()) {
                OrderPayment orderPayment = new OrderPayment(rs.getInt("soldAmount"), rs.getDate("orderTime"));
                i++;
                hm.put(String.valueOf(i), orderPayment);
                //orderPayment.setId(rs.getString("Id"));
            }
        } catch (Exception e) {
        }
        return hm;
    }

    public static ArrayList<OrderPayment> selectDailyTransactionForChart() {
        ArrayList<OrderPayment> orderPaymentArrayList = new ArrayList<OrderPayment>();
        try {
            getConnection();

            String selectAcc = "SELECT count(orderTime) as soldAmount, orderTime from orders group by orderTime";
            PreparedStatement pst = conn.prepareStatement(selectAcc);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                OrderPayment orderPayment = new OrderPayment(rs.getInt("soldAmount"), rs.getDate("orderTime"));
                orderPaymentArrayList.add(orderPayment);
            }
        } catch (Exception e) {
        }
        return orderPaymentArrayList;
    }



}
