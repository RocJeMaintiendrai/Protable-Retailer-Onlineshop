import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/UpdateProduct")
public class UpdateProduct extends HttpServlet{

    protected void doPost(HttpServletRequest request, HttpServletResponse
                    response) throws ServletException, IOException {

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

        if (utility.updateProduct(productId, name, price, manufacturer, condition, discount, image, catalog)) {
            pw.print("alert(\"Update successfully!\")");
            response.sendRedirect("StoreManagerHome");
        }
    }
}
