package com.nope.webapp.servlet;

import com.nope.webapp.entity.Product;
import com.nope.webapp.util.ProductUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author zvr
 */
@WebServlet(name = "ShoppingCart", urlPatterns = {"/ShoppingCart"})
public class ShoppingCart extends HttpServlet {

    private final String URL = "ShoppingCart";
    private final String KEY_PRODUCTS = "PRODUCTS";
    
    private final String SEPARATOR = ":";
    
    private final String FIELD_REFERENCE = "reference";
    private final String FIELD_QUANTITY = "quantity";
    
    private final String BUTTON_ADD_NEW = "add_new_button";
    private final String BUTTON_ADD_ONE = "add_button";
    private final String BUTTON_SUB_ONE = "sub_button";
    private final String BUTTON_DELETE_PRODUCT = "del_button";
    private final String BUTTON_DELETE_ALL_PRODUCTS = "del_all_button";
    
    private final int QUANTITY_INCREASE = 1;
    private final int QUANTITY_DECREASE = 1;
    
    private final String MSG_ERROR = "Undefined error";
    private final String MSG_PRODUCT_ADDED = "%s x%d has been added to the cart";
    private final String MSG_PRODUCT_REMOVED = "%s has been removed the cart";
    private final String MSG_ALL_PRODUCTS_DELETED = "all products have been removed from the cart";
    private final String MSG_PRODUCT_QTY_ADD = "%s quantity increased to %d";
    private final String MSG_PRODUCT_QTY_SUB = "%s quantity decreased to %d";
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        
        ProductUtil productUtil = ProductUtil.getInstance();
        ArrayList<Product> products = new ArrayList<>();
        
        // Get the user session or creates it if null
        HttpSession session = request.getSession(true);
        
        // Checks if there are any products associated with this session
        if(session.getAttribute(KEY_PRODUCTS) != null){
            // Add all products to the current list
            products.addAll((List<Product>)session.getAttribute(KEY_PRODUCTS));
        }
        
        // An informative message to be displayed on the page
        String message = "";
        String errorMessage = "";
        
        
        // Iterates throught all parameters sent in the request
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            
            String parameterName = parameterNames.nextElement();

            // If the user attempts to add a new product
            if (parameterName.equals(BUTTON_ADD_NEW)) {
                
                int quantity = Integer.parseInt(request.getParameter(FIELD_QUANTITY));
                String reference = request.getParameter(FIELD_REFERENCE);
                
                // Check if the product exists already
                Product product = productUtil.getProductByReference(products, reference);
                if(product != null){
                    product.increaseQuantityBy(quantity);
                    message = String.format(MSG_PRODUCT_QTY_ADD, reference, product.getQuantity());
                }
                else{
                    products.add(new Product(reference, quantity));
                    message = String.format(MSG_PRODUCT_ADDED, reference, quantity);
                }
            }
            // If the user wants to delete all items
            else if (parameterName.startsWith(BUTTON_DELETE_ALL_PRODUCTS)) {
                    products.clear();
                    message = MSG_ALL_PRODUCTS_DELETED;
            }
            // If the user edits existing cart items
            else {
                // Attempts to extract the product's reference
                String reference = parameterName.substring(parameterName.indexOf(SEPARATOR) + 1);
                // Obtains the product that matches this reference
                Product product = productUtil.getProductByReference(products, reference);
                
                // If there is no product ( the current request param info is not what we need )
                if(product == null){
                    continue;
                }
                // If the user adds 1
                else if (parameterName.startsWith(BUTTON_ADD_ONE)) {
                    product.increaseQuantityBy(QUANTITY_INCREASE);
                    message = String.format(MSG_PRODUCT_QTY_ADD, reference, product.getQuantity());
                }
                // If the user removes 1
                else if (parameterName.startsWith(BUTTON_SUB_ONE)) {
                    product.decreaseQuantityBy(QUANTITY_DECREASE);
                    message = String.format(MSG_PRODUCT_QTY_SUB, reference, product.getQuantity());
                    // If quantity is 0 or less
                    if(product.getQuantity() <= 0){
                        // Removes the current product
                        products.remove(product);
                    }
                }
                // If the user deletes the product
                else if (parameterName.startsWith(BUTTON_DELETE_PRODUCT)) {
                    products.remove(product);
                    message = String.format(MSG_PRODUCT_REMOVED, reference);
                }
            }
        }

        // Updates the list to the session
        session.setAttribute(KEY_PRODUCTS, products);
        
        // Displays the Cart
        try (PrintWriter out = response.getWriter()) {
            out.println(getCartPage(products, message, errorMessage));
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    
    private String getCartPage(List<Product> products, String message, String errorMessage) {

        String page = new String();
        page += "<!DOCTYPE html>\n"
                + "<html>\n"
                + "    <head>\n"
                + "        <title>Shopping Cart</title>\n"
                + "        <meta charset=\"UTF-8\">\n"
                + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                + "    </head>\n"
                + "\n"
                + "    <body>\n"
                + "        <h1>Shopping cart</h1>\n";
        if(!errorMessage.isEmpty()){
             page +="<p style=color:red>"+errorMessage+"</p>";
        }
        if(!message.isEmpty()){
             page +="<p style=\"color:#20a050\">"+message+"</p>";
        }
        page += "        <br/>\n"
                + "        <form action=\""+URL+"\" method=\"post\">\n"
                + "            <input type=\"text\" placeholder=\"reference\" name=\""+FIELD_REFERENCE+"\" required>\n"
                + "            <br/>\n"
                + "            <br/>\n"
                + "            <input type=\"number\" placeholder=\"quantity\" name=\""+FIELD_QUANTITY+"\" required>\n"
                + "            <br/>\n"
                + "            <br/>\n"
                + "            <input class=\"add_new_button\" type=\"submit\" value=\"Add\" name=\""+BUTTON_ADD_NEW+"\"/>\n"
                + "       </form>\n"
                + "\n";
        if(products.isEmpty()){
            page +="          <p style=\"color:red\">Empty cart<p>\n";
        }
        else {
            page += "        <form action=\""+URL+"\" method=\"post\">\n"
                    + "            <table style=\"width:50%\">\n"
                    + "                <tr>\n"
                    + "                    <th>Reference</th>\n"
                    + "                    <th>Quantity</th>\n"
                    + "                    <th></th>\n"
                    + "                    <th></th>\n"
                    + "                    <th></th>\n"
                    + "                </tr>\n";
            
            for(int i = 0; i < products.size(); i++){
                Product product = products.get(i);
                page += "                <tr>\n"
                        + "                    <td>"+product.getReference()+"</td>\n"
                        + "                    <td>"+product.getQuantity()+"</td>\n"
                        + "                    <td><input class=\"add_button\" type=\"submit\" value=\"+1\" name=\""+BUTTON_ADD_ONE+SEPARATOR+product.getReference()+"\"/></td>\n"
                        + "                    <td><input class=\"sub_button\" type=\"submit\" value=\"-1\" name=\""+BUTTON_SUB_ONE+SEPARATOR+product.getReference()+"\"/></td>\n"
                        + "                    <td><input class=\"del_button\" type=\"submit\" value=\"Delete\" name=\""+BUTTON_DELETE_PRODUCT+SEPARATOR+product.getReference()+"\"/></td>\n"
                        + "                </tr>\n";
            }
            page += "            </table>\n"
                    + "            <input class=\"del_all_button\" type=\"submit\" value=\"Delete All\" name=\""+BUTTON_DELETE_ALL_PRODUCTS+"\"/>\n"
                    + "        </form>\n";
        }
        page += "    </body>\n"
                + "\n"
                + "</html>";
        return page;
    }
}