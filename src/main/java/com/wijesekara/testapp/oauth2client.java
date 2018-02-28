/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wijesekara.testapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.amber.oauth2.client.OAuthClient;
import org.apache.amber.oauth2.client.URLConnectionClient;
import org.apache.amber.oauth2.client.request.OAuthClientRequest;
import org.apache.amber.oauth2.client.response.OAuthAuthzResponse;
import org.apache.amber.oauth2.client.response.OAuthClientResponse;
import org.apache.amber.oauth2.common.message.types.GrantType;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author chiran
 */
public class oauth2client extends HttpServlet {

    public static int flag = 0;

    @Override
    public void init() throws ServletException {
        super.init(); //To change body of generated methods, choose Tools | Templates.

    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String code = "";
        String accessToken = "access";
        String oAuthresp = "oauthresp";
        String userInfo = "";

        try {
            OAuthAuthzResponse authzResponse = OAuthAuthzResponse.oauthCodeAuthzResponse(request);
            code = authzResponse.getCode();

            OAuthClientRequest accessRequest = OAuthClientRequest.
                    tokenLocation("https://localhost:9443/oauth2/token")
                    .setGrantType(GrantType.AUTHORIZATION_CODE)
                    .setClientId("<client-id>")
                    .setClientSecret("<client-secret>")
                    .setRedirectURI("http://localhost:8080/testapp/oauth2client")
                    .setCode(code)
                    .buildBodyMessage();

            //skip ssl certification *******************************************
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, 
                        String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, 
                        String authType) {
                }
            }};
            // Install the all-trusting trust manager
            final SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            //end of skip ssl certification ************************************
            
            //create OAuth client that uses custom http client under the hood
            OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
            OAuthClientResponse oAuthResponse = oAuthClient.accessToken(accessRequest);

            oAuthresp = oAuthResponse.toString();
            // retrieve access token
            accessToken = oAuthResponse.getParam("access_token");

            userInfo = fetchUserData("https://localhost:9443/oauth2/userinfo?schema=openid", accessToken);

        } catch (Exception e) {
            e.printStackTrace();

        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Welcome</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1 align='center'> <font color='green'>Welcome to online hotel reservation !</font></h1><br/>");
        out.println("<h2>Authorization code: " + code + "</h2>");
        out.println("<h2>Access token: " + accessToken + "</h2>");
        out.println("<h2>Hi, You are logged in as, <font color='green'>" + userInfo + "</font></h2>");
        out.println("</body>");
        out.println("</html>");

    }

    public String fetchUserData(String targetURL, String accessTokenIdentifier) {
        try {
            URL myURL = new URL(targetURL);
            URLConnection myURLConnection = myURL.openConnection();
            myURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            myURLConnection.setRequestProperty("Authorization", "Bearer " + accessTokenIdentifier);
            myURLConnection.setRequestProperty("Content-Language", "en-US");
            myURLConnection.setUseCaches(false);
            myURLConnection.setDoInput(true);
            myURLConnection.setDoOutput(true);

            BufferedReader br = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = br.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            br.close();
            return new JSONObject(response.toString()).getString("sub");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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
