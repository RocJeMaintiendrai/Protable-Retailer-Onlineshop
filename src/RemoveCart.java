import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/RemoveCart")
public class RemoveCart extends HttpServlet{

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();

        /* From the HttpServletRequest variable name,type,maker and acessories information are obtained.*/

        Utilities utility = new Utilities(request, pw);
        String name = request.getParameter("orderName");
        utility.removeItemFromCart(name);
        /* StoreProduct Function stores the Purchased product in Orders HashMap.*/

        response.sendRedirect("Cart");
        return;

    }


}