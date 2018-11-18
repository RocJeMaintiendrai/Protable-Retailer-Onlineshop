import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

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
            //生成日期对象
            Date current_date = new Date();
            //设置日期格式化样式为：yyyy-MM-dd
            SimpleDateFormat SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            getConnection();
            String insertIntoCustomerOrderQuery = "insert into orders (orderID, userName, orderName, orderPrice, userAddress, creditCardNo, orderTime) VALUES (?,?,?,?,?,?,?);";
            PreparedStatement pst = conn.prepareStatement(insertIntoCustomerOrderQuery);
            pst.setInt(1, orderId);
            pst.setString(2, userName);
            pst.setString(3, orderName);
            pst.setDouble(4, orderPrice);
            pst.setString(5, userAddress);
            pst.setString(6, creditCardNo);
            pst.setString(7, SimpleDateFormat.format(current_date.getTime()));
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

    public static String addproducts(String producttype, String productId, String productName, double productPrice, String productImage, String productManufacturer, String productCondition, double productDiscount, String prod) {
        String msg = "Product is added successfully";
        try {

            getConnection();
            String addProductQurey = "INSERT INTO Productdetails(ProductType,Id,productName,productPrice,productImage,productManufacturer,productCondition,productDiscount)" +
                            "VALUES (?,?,?,?,?,?,?,?);";

            String name = producttype;

            PreparedStatement pst = conn.prepareStatement(addProductQurey);
            pst.setString(1, name);
            pst.setString(2, productId);
            pst.setString(3, productName);
            pst.setDouble(4, productPrice);
            pst.setString(5, productImage);
            pst.setString(6, productManufacturer);
            pst.setString(7, productCondition);
            pst.setDouble(8, productDiscount);

            pst.executeUpdate();
            try {
                if (!prod.isEmpty()) {
                    String addaprodacc = "INSERT INTO  Product_accessories(productName,accessoriesName)" +
                                    "VALUES (?,?);";
                    PreparedStatement pst1 = conn.prepareStatement(addaprodacc);
                    pst1.setString(1, prod);
                    pst1.setString(2, productId);
                    pst1.executeUpdate();

                }
            } catch (Exception e) {
                msg = "Error while adding the product";
                e.printStackTrace();

            }


        } catch (Exception e) {
            msg = "Error while adding the product";
            e.printStackTrace();

        }
        return msg;
    }

    public static String deleteproducts(String productId) {
        String msg = "Product is deleted successfully";
        try {

            getConnection();
            String deleteproductsQuery = "Delete from Productdetails where Id=?";
            PreparedStatement pst = conn.prepareStatement(deleteproductsQuery);
            pst.setString(1, productId);

            pst.executeUpdate();
        } catch (Exception e) {
            msg = "Product cannot be deleted";
        }
        return msg;
    }

    public static String updateproducts(String producttype, String productId, String productName, double productPrice, String productImage, String productManufacturer, String productCondition, double productDiscount) {
        String msg = "Product is updated successfully";
        try {

            getConnection();
            String updateProductQurey = "UPDATE Productdetails SET productName=?,productPrice=?,productImage=?,productManufacturer=?,productCondition=?,productDiscount=? where Id =?;";


            PreparedStatement pst = conn.prepareStatement(updateProductQurey);

            pst.setString(1, productName);
            pst.setDouble(2, productPrice);
            pst.setString(3, productImage);
            pst.setString(4, productManufacturer);
            pst.setString(5, productCondition);
            pst.setDouble(6, productDiscount);
            pst.setString(7, productId);
            pst.executeUpdate();


        } catch (Exception e) {
            msg = "Product cannot be updated";
            e.printStackTrace();

        }
        return msg;
    }

    public static HashMap<String, Accessory> getAccessories() {
        HashMap<String, Accessory> hm = new HashMap<String, Accessory>();
        try {
            getConnection();

            String selectAcc = "select * from Productdetails where ProductType=?";
            PreparedStatement pst = conn.prepareStatement(selectAcc);
            pst.setString(1, "accessories");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Accessory acc = new Accessory(rs.getString("productName"), rs.getDouble("productPrice"), rs.getString("productImage"), rs.getString("productManufacturer"), rs.getString("productCondition"), rs.getDouble("productDiscount"));
                hm.put(rs.getString("Id"), acc);
                acc.setId(rs.getString("Id"));
            }
        } catch (Exception e) {
        }
        return hm;
    }

    public static HashMap<String, Game> getGames() {
        HashMap<String, Game> hm = new HashMap<String,Game>();
        try {
            getConnection();

            String selectGame = "select * from  Productdetails where ProductType=?";
            PreparedStatement pst = conn.prepareStatement(selectGame);
            pst.setString(1, "Games");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Game game = new Game(rs.getString("productName"), rs.getDouble("productPrice"), rs.getString("productImage"), rs.getString("productManufacturer"), rs.getString("productCondition"), rs.getDouble("productDiscount"));
                hm.put(rs.getString("Id"), game);
                game.setId(rs.getString("Id"));
            }
        } catch (Exception e) {
        }
        return hm;
    }

    public static HashMap<String, Console> getConsoles() {
        HashMap<String, Console> hm = new HashMap<String, Console>();
        try {
            getConnection();

            String selectConsole = "select * from  Productdetails where ProductType=?";
            PreparedStatement pst = conn.prepareStatement(selectConsole);
            pst.setString(1, "Phone");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Console console = new Console(rs.getString("productName"), rs.getDouble("productPrice"), rs.getString("productImage"), rs.getString("productManufacturer"), rs.getString("productCondition"), rs.getDouble("productDiscount"));
                hm.put(rs.getString("Id"), console);
                console.setId(rs.getString("Id"));
                System.out.println(rs.getString("Id"));
                try {
                    String selectaccessory = "Select * from Product_accessories where productName=?";
                    PreparedStatement pstacc = conn.prepareStatement(selectaccessory);
                    pstacc.setString(1, rs.getString("Id"));
                    ResultSet rsacc = pstacc.executeQuery();
                    //System.out.print("assccececeec" + rsacc.getString("accessoriesName"));
                    HashMap<String, String> acchashmap = new HashMap<String, String>();
                    while (rsacc.next()) {
                        if (rsacc.getString("accessoriesName") != null) {
                            System.out.print("acc");
                            acchashmap.put(rsacc.getString("accessoriesName"), rsacc.getString("accessoriesName"));
                            console.setAccessories(acchashmap);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
        }
        return hm;

    }

    public static HashMap<String, Tablet> getTablets() {
        HashMap<String, Tablet> hm = new HashMap<String, Tablet>();
        try {
            getConnection();

            String selectTablet= "select * from  Productdetails where ProductType=?";
            PreparedStatement pst = conn.prepareStatement(selectTablet);
            pst.setString(1, "Tablets");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Tablet tablet = new Tablet(rs.getString("productName"), rs.getDouble("productPrice"), rs.getString("productImage"), rs.getString("productManufacturer"), rs.getString("productCondition"), rs.getDouble("productDiscount"));
                hm.put(rs.getString("Id"), tablet);
                tablet.setId(rs.getString("Id"));
            }
        } catch (Exception e) {
        }
        return hm;
    }

    public static HashMap<String, Smartspeaker> getSmartspeakers() {
        HashMap<String, Smartspeaker> hm = new HashMap<String, Smartspeaker>();
        try {
            getConnection();

            String selectSmartspeaker = "select * from  Productdetails where ProductType=?";
            PreparedStatement pst = conn.prepareStatement(selectSmartspeaker);
            pst.setString(1, "Smartspeakers");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Smartspeaker smartspeaker = new Smartspeaker(rs.getString("productName"), rs.getDouble("productPrice"), rs.getString("productImage"), rs.getString("productManufacturer"), rs.getString("productCondition"), rs.getDouble("productDiscount"));
                hm.put(rs.getString("Id"), smartspeaker);
                smartspeaker.setId(rs.getString("Id"));
            }
        } catch (Exception e) {
        }
        return hm;
    }

    public static void insertProducts() {

        try {
            getConnection();
            String insertProductQuery = "INSERT INTO  Productdetails(ProductType,Id,productName,productPrice,productImage,productManufacturer,productCondition,productDiscount)" +
                            "VALUES (?,?,?,?,?,?,?,?);";
            for (Map.Entry<String, Accessory> entry : SaxParserDataStore.accessories.entrySet()) {
                String name = "accessories";
                Accessory acc = entry.getValue();
                PreparedStatement pst = conn.prepareStatement(insertProductQuery);
                pst.setString(1, name);
                pst.setString(2, acc.getId());
                pst.setString(3, acc.getName());
                pst.setDouble(4, acc.getPrice());
                pst.setString(5, acc.getImage());
                pst.setString(6, acc.getRetailer());
                pst.setString(7, acc.getCondition());
                pst.setDouble(8, acc.getDiscount());

                pst.executeUpdate();
            }

            for (Map.Entry<String, Console> entry : SaxParserDataStore.consoles.entrySet()) {
                String name = "Console";
                Console  console = entry.getValue();
                PreparedStatement pst = conn.prepareStatement(insertProductQuery);
                pst.setString(1, name);
                pst.setString(2, console.getId());
                pst.setString(3, console.getName());
                pst.setDouble(4, console.getPrice());
                pst.setString(5, console.getImage());
                pst.setString(6, console.getRetailer());
                pst.setString(7, console.getCondition());
                pst.setDouble(8, console.getDiscount());

                pst.executeUpdate();
            }

            for (Map.Entry<String, Game> entry : SaxParserDataStore.games.entrySet()) {
                String name = "Game";
                Game game = entry.getValue();
                PreparedStatement pst = conn.prepareStatement(insertProductQuery);
                pst.setString(1, name);
                pst.setString(2, game.getId());
                pst.setString(3, game.getName());
                pst.setDouble(4, game.getPrice());
                pst.setString(5, game.getImage());
                pst.setString(6, game.getRetailer());
                pst.setString(7, game.getCondition());
                pst.setDouble(8, game.getDiscount());

                pst.executeUpdate();
            }

            for (Map.Entry<String, Tablet> entry : SaxParserDataStore.tablets.entrySet()) {
                String name = "Tablet";
                Tablet tablet = entry.getValue();
                PreparedStatement pst = conn.prepareStatement(insertProductQuery);
                pst.setString(1, name);
                pst.setString(2, tablet.getId());
                pst.setString(3, tablet.getName());
                pst.setDouble(4, tablet.getPrice());
                pst.setString(5, tablet.getImage());
                pst.setString(6, tablet.getRetailer());
                pst.setString(7, tablet.getCondition());
                pst.setDouble(8, tablet.getDiscount());

                pst.executeUpdate();
            }

            for (Map.Entry<String, Smartspeaker> entry : SaxParserDataStore.smartspeakers.entrySet()) {
                String name = "Smartspeaker";
                Smartspeaker smartspeaker = entry.getValue();
                PreparedStatement pst = conn.prepareStatement(insertProductQuery);
                pst.setString(1, name);
                pst.setString(2, smartspeaker.getId());
                pst.setString(3, smartspeaker.getName());
                pst.setDouble(4, smartspeaker.getPrice());
                pst.setString(5, smartspeaker.getImage());
                pst.setString(6, smartspeaker.getRetailer());
                pst.setString(7, smartspeaker.getCondition());
                pst.setDouble(8, smartspeaker.getDiscount());

                pst.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static HashMap<String, Product> getData() {
        HashMap<String, Product> hm = new HashMap<String, Product>();
        try {
            getConnection();
            Statement stmt = conn.createStatement();
            String selectCustomerQuery = "select * from Productdetails";
            ResultSet rs = stmt.executeQuery(selectCustomerQuery);
            while (rs.next()) {
                Product p = new Product(rs.getString("Id"), rs.getString("productName"), rs.getDouble("productPrice"), rs.getString("productImage"), rs.getString("productManufacturer"), rs.getString("productCondition"), rs.getString("ProductType"), rs.getDouble("productDiscount"));
                hm.put(rs.getString("Id"), p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hm;
    }


}
