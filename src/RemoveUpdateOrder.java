import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/RemoveUpdateOrder")
public class RemoveUpdateOrder extends HttpServlet {

    HttpSession session;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();

        Utilities utility = new Utilities(request, pw);

        int orderId = Integer.parseInt(request.getParameter("orderId"));
        String username = request.getParameter("username");
        String productName = request.getParameter("productName");
        String price = request.getParameter("price");
        String address = request.getParameter("address");
        String creditCard = request.getParameter("creditCard");
        String userType = request.getParameter("userType");


        if (request.getParameter("Order") != null && request.getParameter("Order").equals("Cancel")) {
            //Cancel Order
            utility.removeOldOrder(orderId, productName, username);

            if (userType.equals("customer")) {
                response.sendRedirect("Account");
            } else {

                response.sendRedirect("SalesmanHome");
            }
        } else if(request.getParameter("Order") != null && request.getParameter("Order").equals("Update")) {
            //Update Order

            utility.printHtml("Header.html");
            utility.printHtml("LeftNavigationBar.html");

            pw.print("<div id='content'>");
            pw.print("<div class='post'>");
            pw.print("<h3 class='title'>");
            pw.print("Update Order");
            pw.print("</h3>");
            pw.print("<div class='entry'>");

            //显示更新order的表格
            pw.print("<form action='UpdateOrder' method='post'");
            pw.print("<table style='width:100%'>");

            pw.print("<tr>");
            pw.print("<td><h4>Order ID: " + orderId + "</h4></td>");
            pw.print("</tr>");

            pw.print("<tr>");
            pw.print("<td><h4>Customer Name: " + username + "</h4></td>");
            pw.print("</tr><tr><td>");

            pw.print("<tr>");
            pw.print("<td><h4>Product Name: " + productName + "</h4></td>");
            pw.print("</tr><tr><td>");

            pw.print("<tr>");
            pw.print("<td><h4>Price: " + price + "</h4></td>");
            pw.print("</tr><tr><td>");

            pw.print("<h4>Address</h4><td><input type='text' name='address' value='" + address + "' class='input' required></input>");
            pw.print("</td></tr></td><tr><td>");

            pw.print("<h4>Credit Card</h4></td><td><input type='text' name='creditCard' value='" + creditCard + "' class='input' required></input>");
            pw.print("</td></tr><tr><td>");


            pw.print("<input type='hidden' name='orderId' value='" + orderId + "'>");
            pw.print("<input type='hidden' name='customerName' value='" + username + "'>");
            pw.print("<input type='hidden' name='productName' value='" + productName + "'>");
            pw.print("<input type='hidden' name='price' value='" + price + "'>");
            pw.print("<input type='hidden' name='address' value='" + address + "'>");
            pw.print("<input type='hidden' name='creditCard' value='" + creditCard + "'>");

            pw.print("<input type='submit' class='btnbuy' value='Update' style='float: right;height: 20px margin: 20px; margin-right: 10px;'></input>");
            pw.print("</td></tr><tr><td></td><td>");
            pw.print("</td></tr></table>");
            pw.print("</form></div></div></div>");
        }
    }

}