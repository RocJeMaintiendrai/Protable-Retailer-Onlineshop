import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AccessoryList")

public class AccessoryList extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();

        String name = null;
        String CategoryName = request.getParameter("maker");

        HashMap<String, Accessory> hm = new HashMap<String, Accessory>();
        if (CategoryName == null) {
            hm.putAll(SaxParserDataStore.accessories);
            name = "";
        } else {
            if (CategoryName.equals("phone")) {
                for (Map.Entry<String, Accessory> entry : SaxParserDataStore.accessories.entrySet()) {
                    if (entry.getValue().getRetailer().equals("Phone")) {
                        hm.put(entry.getValue().getId(), entry.getValue());
                    }
                }
                name = "Phone";
            } else if (CategoryName.equals("fitness")) {
                for (Map.Entry<String, Accessory> entry : SaxParserDataStore.accessories.entrySet()) {
                    if (entry.getValue().getRetailer().equals("Fitness")) {
                        hm.put(entry.getValue().getId(), entry.getValue());
                    }
                }
            }
            name = "Laptop";
        }

        Utilities utility = new Utilities(request, pw);
        utility.printHtml("Header.html");
        utility.printHtml("LeftNavigationBar.html");
        pw.print("<div id='content'><div class='post'><h2 class='title meta'>");
        pw.print("<a style='font-size: 24px;'>" + name + " Accessories</a>");
        pw.print("</h2><div class='entry'><table id='bestseller'>");
        int i = 1;
        int size = hm.size();
        for (Map.Entry<String, Accessory> entry : hm.entrySet()) {
            Accessory accessory = entry.getValue();
            if (i % 3 == 1) {
                pw.print("<tr>");
            }
            pw.print("<td><div id='shop_item'>");
            pw.print("<h3>" + accessory.getName() + "</h3>");
            pw.print("<strong>" + "$" + accessory.getPrice() + "</strong><ul>");
            pw.print("<li id='item'><img src='images/accessory/" + accessory.getImage() + "' alt='' /></li>");
            pw.print("<li><form method='post' action='Cart'>" +
                                     "<input type='hidden' name='name' value='" + entry.getKey() + "'>" +
                                     "<input type='hidden' name='type' value='accessories'>" +
                                     "<input type='hidden' name='maker' value='" + CategoryName + "'>"
                                     + "<input type='hidden' name='access' value=' " + accessory.getName() + " '>"
                                     + "<input type='submit' class='btnbuy' value='Buy Now'></form></li>");
            pw.print("<li><form method='post' action='WriteReview'>" + "<input type='hidden' name='name' value='"
                                     + entry.getKey() + "'>"
                                     + "<input type='hidden' name='type' value='accessories'>"
                                     + "<input type='hidden' name='maker' value='" + accessory.getRetailer() + "'>"
                                     + "<input type='hidden' name='access' value=' " + accessory.getName()+"'>"
                                     + "<input type='hidden' name='price' value='" + accessory.getPrice() + "'>"
                                     + "<input type='submit' value='WriteReview' class='btnreview'></form></li>");
            pw.print("<li><form method='post' action='ViewReview'>" + "<input type='hidden' name='name' value='" + entry
                            .getKey() + "'>" + "<input type='hidden' name='type' value='accessories'>"
                                     + "<input type='hidden' name='maker' value='" + CategoryName + "'>"
                                     + "<input type='hidden' name='access' value=' " + accessory.getName() +" '>"
                                     + "<input type='submit' value='ViewReview' class='btnreview'></form></li>");
            pw.print("</ul></div></td>");
            if (i % 3 == 0 || i == size) {
                pw.print("</tr>");
            }
            i++;
        }
        pw.print("</table></div></div></div>");
        utility.printHtml("Footer.html");
    }
}