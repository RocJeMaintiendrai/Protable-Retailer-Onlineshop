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

        if (flag.equals("newProduct"))
            pw.print("<h4 style='color:red'>" + error_msg + "</h4>");
        //show form to create product
        pw.print("<form action='StoreManagerHome' method='post' enctype='multipart/form-data'>");
        pw.print("<table style='width:100%'><tr><td>");

        pw.print("<h4>Product ID</h4></td><td><input type='text' name='id' value='' class='input' required></input>");
        pw.print("</td></tr><tr><td>");

        pw.print("<h4>Product Name</h4></td><td><input type='text' name='name' value='' class='input' required></input>");
        pw.print("</td></tr><tr><td>");

        pw.print("<h4>Product Catalog</h4><td><select name='productCatalog' class='input'>" +
                                 "<option value='Console' selected>Wearable Tech</option>" +
                                 "<option value='Game'>Phone</option>" +
                                 "<option value='Tablet'>Laptop</option>" +
                                 "<option value='Smartspeaker'>Virtual reality</option>" +
                                 "<option value='Accessory'>Accessory</option></select>");
        pw.print("</td></tr></td><tr><td>");

        pw.print("<h4>Price</h4></td><td><input type='text' name='price' value='' class='input' required></input>");
        pw.print("</td></tr><tr><td>");
        pw.print("<h4>Manufacturer</h4></td><td><input type='text' name='manufacturer' value='' class='input' required></input>");
        pw.print("</td></tr><tr><td>");

        pw.print("<h4>Condition</h4><td><select name='condition' class='input'>" +
                                 "<option value='New' selected>New</option>" +
                                 "<option value='Used'>Used</option>" +
                                 "<option value='Refurbished'>Refurbished</option></select>");
        pw.print("</td></tr></td><tr><td>");

        pw.print("<h4>Discount</h4></td><td><input type='text' name='discount' value='' class='input' required></input>");
        pw.print("</td></tr><tr><td>");


        pw.print("<h4>Image</h4></td><td><img id=\"preview\" /><br/><input type='file' name='image' class='input' required></input>");
        pw.print("</td></tr><tr><td>");


        pw.print("<input type='submit' class='btnbuy' value='Create' style='float: right;height: 20px margin: 20px; margin-right: 10px;'></input>");
        pw.print("</td></tr><tr><td></td><td>");
        pw.print("</td></tr></table>");
        pw.print("</form></div></div>");

        pw.print("<div class='post'>");
        pw.print("<h2 class='title meta'>");
        pw.print("<a style='font-size: 24px;'>View Products</a></h2>");
        pw.print("<div class='entry'>");
        pw.print("<table class='gridtable'>");

        if (flag.equals("RemoveUpdateProduct"))
            pw.print("<h4 style='color:red'>" + error_msg + "</h4>");

        //表头
        pw.print("<tr>");
        pw.print("<td>Product Name</td>");
        pw.print("<td>Price</td>");
        pw.print("<td>Manufacturer</td>");
        pw.print("<td>Condition</td>");
        pw.print("<td>Discount</td>");
        pw.print("<td>Catalog</td>");
        pw.print("</tr>");

        //Console
        for (Map.Entry<String, Console> entry : SaxParserDataStore.consoles.entrySet()) {
            Console console = entry.getValue();
            pw.print("<form method='post' action='RemoveUpdateProduct'>");
            pw.print("<tr>");

            pw.print("<td>" + console.getName() + "</td>" +
                                     "<td>" + console.getPrice() + "</td>" +
                                     "<td>" + console.getRetailer() + "</td>" +
                                     "<td>" + console.getCondition() + "</td>" +
                                     "<td>" + console.getDiscount() + "</td>" +
                                     "<td>Wearable Tech</td>");

            pw.print("<input type='hidden' name='productId' value='" + console.getId() + "'>");
            pw.print("<input type='hidden' name='productName' value='" + console.getName() + "'>");
            pw.print("<input type='hidden' name='price' value='" + console.getPrice() + "'>");
            pw.print("<input type='hidden' name='manufacturer' value='" + console.getRetailer() + "'>");
            pw.print("<input type='hidden' name='condition' value='" + console.getCondition() + "'>");
            pw.print("<input type='hidden' name='discount' value='" + console.getDiscount() + "'>");
            pw.print("<input type='hidden' name='catalog' value='Console'>");
            pw.print("<input type='hidden' name='image' value='" + console.getImage() + "'>");
            pw.print("</tr>");

            pw.print("<tr>");
            //pw.print("<td></td>");
            pw.print("<td><div align=\"left\" style=\"float:left\"><input type='submit' name='Product' value='Update' class='btnbuy'></div>");
            pw.print("<div align=\"right\"><input type='submit' name='Product' value='Remove' class='btnbuy'></div></td>");
            pw.print("</tr>");
            pw.print("</form>");
        }

        //Phone
        for (Map.Entry<String, Game> entry : SaxParserDataStore.games.entrySet()) {
            Game game = entry.getValue();
            pw.print("<form method='post' action='RemoveUpdateProduct'>");
            pw.print("<tr>");

            pw.print("<td>" + game.getName() + "</td>" +
                                     "<td>" + game.getPrice() + "</td>" +
                                     "<td>" + game.getRetailer() + "</td>" +
                                     "<td>" + game.getCondition() + "</td>" +
                                     "<td>" + game.getDiscount() + "</td>" +
                                     "<td>Phone</td>");

            pw.print("<input type='hidden' name='productId' value='" + game.getId() + "'>");
            pw.print("<input type='hidden' name='productName' value='" + game.getName() + "'>");
            pw.print("<input type='hidden' name='price' value='" + game.getPrice() + "'>");
            pw.print("<input type='hidden' name='manufacturer' value='" + game.getRetailer() + "'>");
            pw.print("<input type='hidden' name='condition' value='" + game.getCondition() + "'>");
            pw.print("<input type='hidden' name='discount' value='" + game.getDiscount() + "'>");
            pw.print("<input type='hidden' name='catalog' value='Game'>");
            pw.print("<input type='hidden' name='image' value='" + game.getImage() + "'>");
            pw.print("</tr>");

            pw.print("<tr>");
            //pw.print("<td></td>");
            pw.print("<td><div align=\"left\" style=\"float:left\"><input type='submit' name='Product' value='Update' class='btnbuy'></div>");
            pw.print("<div align=\"right\"><input type='submit' name='Product' value='Remove' class='btnbuy'></div></td>");
            pw.print("</tr>");
            pw.print("</form>");
        }

        //Laptop
        for (Map.Entry<String, Tablet> entry : SaxParserDataStore.tablets.entrySet()) {
            Tablet tablet = entry.getValue();
            pw.print("<form method='post' action='RemoveUpdateProduct'>");
            pw.print("<tr>");

            pw.print("<td>" + tablet.getName() + "</td>" +
                                     "<td>" + tablet.getPrice() + "</td>" +
                                     "<td>" + tablet.getRetailer() + "</td>" +
                                     "<td>" + tablet.getCondition() + "</td>" +
                                     "<td>" + tablet.getDiscount() + "</td>" +
                                     "<td>Laptop</td>");

            pw.print("<input type='hidden' name='productId' value='" + tablet.getId() + "'>");
            pw.print("<input type='hidden' name='productName' value='" + tablet.getName() + "'>");
            pw.print("<input type='hidden' name='price' value='" + tablet.getPrice() + "'>");
            pw.print("<input type='hidden' name='manufacturer' value='" + tablet.getRetailer() + "'>");
            pw.print("<input type='hidden' name='condition' value='" + tablet.getCondition() + "'>");
            pw.print("<input type='hidden' name='discount' value='" + tablet.getDiscount() + "'>");
            pw.print("<input type='hidden' name='catalog' value='Tablet'>");
            pw.print("<input type='hidden' name='image' value='" + tablet.getImage() + "'>");
            pw.print("</tr>");

            pw.print("<tr>");
            //pw.print("<td></td>");
            pw.print("<td><div align=\"left\" style=\"float:left\"><input type='submit' name='Product' value='Update' class='btnbuy'></div>");
            pw.print("<div align=\"right\"><input type='submit' name='Product' value='Remove' class='btnbuy'></div></td>");
            pw.print("</tr>");
            pw.print("</form>");
        }

        //Smartspeaker
        for (Map.Entry<String, Smartspeaker> entry : SaxParserDataStore.smartspeakers.entrySet()) {
            Smartspeaker smartspeaker = entry.getValue();
            pw.print("<form method='post' action='RemoveUpdateProduct'>");
            pw.print("<tr>");

            pw.print("<td>" + smartspeaker.getName() + "</td>" +
                                     "<td>" + smartspeaker.getPrice() + "</td>" +
                                     "<td>" + smartspeaker.getRetailer() + "</td>" +
                                     "<td>" + smartspeaker.getCondition() + "</td>" +
                                     "<td>" + smartspeaker.getDiscount() + "</td>" +
                                     "<td>Smart Speaker</td>");

            pw.print("<input type='hidden' name='productId' value='" + smartspeaker.getId() + "'>");
            pw.print("<input type='hidden' name='productName' value='" + smartspeaker.getName() + "'>");
            pw.print("<input type='hidden' name='price' value='" + smartspeaker.getPrice() + "'>");
            pw.print("<input type='hidden' name='manufacturer' value='" + smartspeaker.getRetailer() + "'>");
            pw.print("<input type='hidden' name='condition' value='" + smartspeaker.getCondition() + "'>");
            pw.print("<input type='hidden' name='discount' value='" + smartspeaker.getDiscount() + "'>");
            pw.print("<input type='hidden' name='catalog' value='Smartspeaker'>");
            pw.print("<input type='hidden' name='image' value='" + smartspeaker.getImage() + "'>");
            pw.print("</tr>");

            pw.print("<tr>");
            //pw.print("<td></td>");
            pw.print("<td><div align=\"left\" style=\"float:left\"><input type='submit' name='Product' value='Update' class='btnbuy'></div>");
            pw.print("<div align=\"right\"><input type='submit' name='Product' value='Remove' class='btnbuy'></div></td>");
            pw.print("</tr>");
            pw.print("</form>");
        }

        //Accessory
        for (Map.Entry<String, Accessory> entry : SaxParserDataStore.accessories.entrySet()) {
            Accessory accessory = entry.getValue();
            pw.print("<form method='post' action='RemoveUpdateProduct'>");
            pw.print("<tr>");

            pw.print("<td>" + accessory.getName() + "</td>" +
                                     "<td>" + accessory.getPrice() + "</td>" +
                                     "<td>" + accessory.getRetailer() + "</td>" +
                                     "<td>" + accessory.getCondition() + "</td>" +
                                     "<td>" + accessory.getDiscount() + "</td>" +
                                     "<td>Accessory</td>");

            pw.print("<input type='hidden' name='productId' value='" + accessory.getId() + "'>");
            pw.print("<input type='hidden' name='productName' value='" + accessory.getName() + "'>");
            pw.print("<input type='hidden' name='price' value='" + accessory.getPrice() + "'>");
            pw.print("<input type='hidden' name='manufacturer' value='" + accessory.getRetailer() + "'>");
            pw.print("<input type='hidden' name='condition' value='" + accessory.getCondition() + "'>");
            pw.print("<input type='hidden' name='discount' value='" + accessory.getDiscount() + "'>");
            pw.print("<input type='hidden' name='catalog' value='Accessory'>");
            pw.print("<input type='hidden' name='image' value='" + accessory.getImage() + "'>");
            pw.print("</tr>");

            pw.print("<tr>");
            //pw.print("<td></td>");
            pw.print("<td><div align=\"left\" style=\"float:left\"><input type='submit' name='Product' value='Update' class='btnbuy'></div>");
            pw.print("<div align=\"right\"><input type='submit' name='Product' value='Remove' class='btnbuy'></div></td>");
            pw.print("</tr>");
            pw.print("</form>");
        }

        pw.print("</table>");
        pw.print("</div></div></div>");

    }

}
