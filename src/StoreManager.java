import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/StoreManage")
public class StoreManager extends HttpServlet{

    private String error_msg;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter pw = response.getWriter();
        displayStoreManager(request, response, pw, "");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();
        Utilities utility = new Utilities(request, pw);

        //Add New product
        Map<String, Object> map = new HashMap<>(); //保存表单提交的数据(新建product)

        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
        ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);
        String catalog;
        try {
            List<FileItem> parseRequest = servletFileUpload.parseRequest(request);
            for (FileItem fileItem : parseRequest) {
                boolean formField = fileItem.isFormField();
                if (formField) {
                    //普通表单项
                    String fieldName = fileItem.getFieldName();
                    String fieldValue = fileItem.getString();
                    map.put(fieldName, fieldValue);
                } else {
                    //图片上传项，获得文件名称和内容

                    catalog = String.valueOf(map.get("productCatalog"));
                    String realPath = utility.getRealPath(catalog);

                    String fileName = fileItem.getName();
                    String path = this.getServletContext().getRealPath(realPath);
                    InputStream inputStream = fileItem.getInputStream();
                    OutputStream outputStream = new FileOutputStream(path + "/" + fileName);
                    IOUtils.copy(inputStream, outputStream);
                    inputStream.close();
                    outputStream.close();
                    fileItem.delete();

                    map.put("image", fileName);
                }
            }

            if (utility.storeNewProduct(map)) {
                //添加成功
                error_msg = "Completed!";
                displayStoreManager(request, response, pw, "newProduct");
            } else {
                //添加失败
                error_msg = "Cannot add new product!";
                displayStoreManager(request, response, pw, "newProduct");
            }

        } catch (FileUploadException e) {
            e.printStackTrace();
        }
    }

    private void displayStoreManager(HttpServletRequest request,
                                         HttpServletResponse response, PrintWriter pw, String flag) {

        Utilities utility = new Utilities(request, pw);
        utility.printHtml("Header.html");
        utility.printHtml("LeftNavigationBar.html");

        pw.print("<div id='content'>");
        pw.print("<div class='post'>");
        pw.print("<h3 class='title'>");
        pw.print("Create New product");
        pw.print("</h3>");
        pw.print("<div class='entry'>");
    }

}
