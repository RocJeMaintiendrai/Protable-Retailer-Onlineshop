import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/StoreManage")
public class StoreManager extends HttpServlet{

    private String error_msg;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter pw = response.getWriter();
        displayStoreManager(request, response, pw, "");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }

    private void displayStoreManager(HttpServletRequest request,
                                         HttpServletResponse response, PrintWriter pw, String flag) {

    }

}
