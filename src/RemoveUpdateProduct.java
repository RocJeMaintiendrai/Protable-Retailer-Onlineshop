import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * store manager remove || update product
 */
@WebServlet("/RemoveUpdateProduct")
public class RemoveUpdateProduct extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();
        Utilities utility = new Utilities(request, pw);

        String productId = request.getParameter("productId");
        String name = request.getParameter("productName");
        String price = request.getParameter("price");
        String manufacturer = request.getParameter("manufacturer");
        String condition = request.getParameter("condition");
        String discount = request.getParameter("discount");
        String catalog = request.getParameter("catalog");
        String image = request.getParameter("image");

        if (request.getParameter("Product") != null && request.getParameter("Product").equals("Remove")) {
            //Remove Product
            if (utility.removeProduct(productId, catalog) && AjaxUtility.deleteProduct(productId)) {
                response.sendRedirect("StoreManagerHome");
            }
        } else if (request.getParameter("Product") != null && request.getParameter("Product").equals("Update")) {
            //Update Product

            utility.printHtml("Header.html");
            utility.printHtml("LeftNavigationBar.html");

            pw.print("<div id='content'>");
            pw.print("<div class='post'>");
            pw.print("<h3 class='title'>");
            pw.print("Update product");
            pw.print("</h3>");
            pw.print("<div class='entry'>");

            //显示更新product的表格
            pw.print("<form action='UpdateProduct' method='post'");
            pw.print("<table style='width:100%'><tr><td>");

            pw.print("<h4>Product ID: " + productId + "</h4></td>");
            pw.print("</tr><tr><td>");
            pw.print("<input type='hidden' name='productId' value='" + productId + "'>");
            pw.print("<input type='hidden' name='catalog' value='" + catalog + "'>");
            pw.print("<input type='hidden' name='image' value='" + image + "'>");

            pw.print("<h4>Product Name</h4></td><td><input type='text' name='productName' value='" + name + "' class='input' required></input>");
            pw.print("</td></tr><tr><td>");

            pw.print("<h4>Product Catalog</h4><td><select id='catalog' name='productCatalog' class='input'>" +
                                     "<option value='Console' selected>Wearable Tech</option>" +
                                     "<option value='Game'>Phone</option>" +
                                     "<option value='Tablet'>Laptop</option>" +
                                     "<option value='Smartspeaker'>Smart speaker</option>" +
                                     "<option value='Accessory'>Accessory</option></select>");
            pw.print("</td></tr></td><tr><td>");


            pw.print("<h4>Price</h4></td><td><input type='text' name='price' value='" + price + "' class='input' required></input>");
            pw.print("</td></tr><tr><td>");
            pw.print("<h4>Manufacturer</h4></td><td><input type='text' name='manufacturer' value='" + manufacturer + "' class='input' required></input>");
            pw.print("</td></tr><tr><td>");

            pw.print("<h4>Condition</h4><td><select name='condition' class='input'>" +
                                     "<option value='New' selected>New</option>" +
                                     "<option value='Used'>Used</option>" +
                                     "<option value='Refurbished'>Refurbished</option></select>");
            pw.print("</td></tr></td><tr><td>");

            pw.print("<h4>Discount</h4></td><td><input type='text' name='discount' value='" + discount + "' class='input' required></input>");
            pw.print("</td></tr><tr><td>");

            pw.print("<input type='submit' class='btnbuy' value='Update' style='float: right;height: 20px margin: 20px; margin-right: 10px;'></input>");
            pw.print("</td></tr><tr><td></td><td>");
            pw.print("</td></tr></table>");
            pw.print("</form></div></div></div>");
        }

    }
}
