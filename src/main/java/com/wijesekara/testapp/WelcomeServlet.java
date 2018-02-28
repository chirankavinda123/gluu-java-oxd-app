package com.wijesekara.testapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.xdi.oxd.client.CommandClient;
import org.xdi.oxd.common.Command;
import org.xdi.oxd.common.CommandType;
import org.xdi.oxd.common.params.GetTokensByCodeParams;
import org.xdi.oxd.common.response.GetTokensByCodeResponse;

/**
 *
 * @author chiran
 */
public class WelcomeServlet extends HttpServlet {

    public String accessToken="";
    public String idToken="";
    public String errorIs ="";

    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        CommandClient client=null;
        HttpSession session = request.getSession();
 
        try{
        
        client = new CommandClient("localhost",8099);
        final GetTokensByCodeParams commandParams = new GetTokensByCodeParams();
        
        commandParams.setOxdId(session.getAttribute("curOxdId").toString());
        commandParams.setCode(request.getParameter("code"));
        commandParams.setState(request.getParameter("state"));
        
        final Command command = new Command(CommandType.GET_TOKENS_BY_CODE);
        command.setParamsObject(commandParams);

        final GetTokensByCodeResponse resp = client.send(command).dataAsResponse(GetTokensByCodeResponse.class);
        accessToken = resp.getAccessToken();
        idToken = resp.getIdToken();
        
           
        }catch(Exception e){
            e.printStackTrace();
            errorIs=e.getMessage();
        }
        finally{
            
            CommandClient.closeQuietly(client);
        }
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Welcome </title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>welcome ! You have successfully logged in</h1>");
            out.println("<h1>Acess Token:"+accessToken+"</h1>");
            out.println("<h1>id Token:"+idToken+"</h1>");
            out.println("<h1>state:"+request.getParameter("state")+"</h1>");
            out.println("<h1>Error is:"+errorIs+"</h1>");
            out.println("</body>");
            out.println("</html>");
        
    }

   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
