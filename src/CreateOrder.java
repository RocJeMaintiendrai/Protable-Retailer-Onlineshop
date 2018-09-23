import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.HashMap;

@WebServlet("/CreateOrder")
public class CreateOrder extends HttpServlet{

    private String error_msg;


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                    IOException {

        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();

        String customerName = request.getParameter("username");
        String itemName = request.getParameter("itemName");
        String creditCardNo = request.getParameter("creditCardNo");
        String customerAddress = request.getParameter("customerAddress");


        HashMap<String, User> hm = new HashMap<String, User>();
        String TOMCAT_HOME = System.getProperty("catalina.home");

        try {
            FileInputStream fileInputStream = new FileInputStream(new File(TOMCAT_HOME + "\\webapps\\Tutorial_1\\UserDetails.txt"));
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            hm = (HashMap) objectInputStream.readObject();
        } catch (Exception e) {

        }

        if (!hm.containsKey(customerName))
            error_msg = "Customer doesn't exist.";
        else {
            Utilities utility = new Utilities(request, pw);
            String name = request.getParameter("orderName");
            utility.removeItemFromCart(name);
            /* StoreProduct Function stores the Purchased product in Orders HashMap.*/
            response.sendRedirect("Cart");
            return;

        }


    }

}
