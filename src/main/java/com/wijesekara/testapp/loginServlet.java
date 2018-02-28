/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wijesekara.testapp;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.xdi.oxd.client.CommandClient;
import org.xdi.oxd.common.Command;
import org.xdi.oxd.common.CommandType;
import org.xdi.oxd.common.params.GetAuthorizationUrlParams;
import org.xdi.oxd.common.params.RegisterSiteParams;
import org.xdi.oxd.common.response.GetAuthorizationUrlResponse;
import org.xdi.oxd.common.response.RegisterSiteResponse;
import static com.google.common.collect.Lists.newArrayList;
import org.xdi.oxd.common.params.UpdateSiteParams;
import org.xdi.oxd.common.response.UpdateSiteResponse;

/**
 *
 * @author chiran
 */
public class loginServlet extends HttpServlet {

    public static String currentOxdId="";
    public static String authorizationUrl="";
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
   
        String resStatus="";
        CommandClient client = null;
        
        try {
            client = new CommandClient("localhost",8099);
            final RegisterSiteParams commandParams = new RegisterSiteParams();
            commandParams.setOpHost("https://accounts.google.com");
            commandParams.setAuthorizationRedirectUri("https://client.myexamples.com:8443/testapp/welcome");
            commandParams.setClientId("237882107417-amlljkau52q3ejkt2i7mtuun88qjt88b.apps.googleusercontent.com");
            commandParams.setClientSecret("i0FCRUE0ZyCyNBnNnprpo6Bi");
            //commandParams.setPostLogoutRedirectUri(postLogoutRedirectUrl);
            //commandParams.setClientLogoutUri(Lists.newArrayList(logoutUri));
            commandParams.setScope(newArrayList("openid","profile"));
            
            final Command command = new Command(CommandType.REGISTER_SITE);
            command.setParamsObject(commandParams);
            final RegisterSiteResponse site = client.send(command).dataAsResponse(RegisterSiteResponse.class);
            currentOxdId = site.getOxdId();  
            HttpSession session = request.getSession();
            session.setAttribute("curOxdId", site.getOxdId());
            
            //end of site registration
            
            /*
            //beginnning of update site registration
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);

            final UpdateSiteParams updParams = new UpdateSiteParams();
            updParams.setOxdId(currentOxdId);
            updParams.setClientSecretExpiresAt(calendar.getTime());
            updParams.setScope(newArrayList("openid","profile"));
            updParams.setClientId("237882107417-amlljkau52q3ejkt2i7mtuun88qjt88b.apps.googleusercontent.com");
            updParams.setClientSecret("i0FCRUE0ZyCyNBnNnprpo6Bi");
            
            
            
            final Command updCmd = new Command(CommandType.UPDATE_SITE);
            updCmd.setParamsObject(updParams);
            UpdateSiteResponse resp = client.send(updCmd).dataAsResponse(UpdateSiteResponse.class);
            
            //assertNotNull(resp);
            
            //end of update site registration 
            
            */

         
            //get authorization url
            final GetAuthorizationUrlParams authUrlParams = new GetAuthorizationUrlParams();
            authUrlParams.setOxdId(site.getOxdId());
            authUrlParams.setPrompt("login");
            final Command getUrlCmd = new Command(CommandType.GET_AUTHORIZATION_URL);         
            getUrlCmd.setParamsObject(authUrlParams);
            final GetAuthorizationUrlResponse authUrlResp = client.send(getUrlCmd).dataAsResponse(GetAuthorizationUrlResponse.class);
            authorizationUrl = authUrlResp.getAuthorizationUrl();
            
            //end of getting authorizaiton url
         
        } finally {
            CommandClient.closeQuietly(client);
        }
        
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>login servlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>in the login servlet </br>oxID is :"+currentOxdId+"</h1>");
            out.println("</br>--------------------------------------------------------</br>");
            out.println("<h1>Status of updation:"+resStatus+"</h1>");
            out.println("</br>--------------------------------------------------------</br>");
            out.println("<h1>authoization Url:"+authorizationUrl+"</h1>");          
            out.println("</body>");
            out.println("</html>");
        
                  
            response.sendRedirect(authorizationUrl);
        
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

}
