/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wijesekara.testapp;

import static com.google.common.collect.Lists.newArrayList;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.apache.amber.oauth2.client.request.OAuthClientRequest;


public class loginIs extends HttpServlet {


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      
        
       // if(!request.getAttribute("code").equals(null)){
            String consumerKey = "oEUDFtKVUAU7L2knLDstGWUpvOga";
            String consumerSecret = "mZ3yu48ma24_USs4wecr309rSxMa";
            String authzEndpoint = "https://localhost:9443/oauth2/authorize";
            String tokenEndpoint = "https://localhost:9443/oauth2/token";
            String callBackUrl = "http://localhost:8080/testapp/oauth2client";
            String authzGrantType = "code";
            String scope = "openid";

            try{
            OAuthClientRequest authzRequest = OAuthClientRequest
                        .authorizationLocation(authzEndpoint)
                        .setClientId(consumerKey)
                        .setRedirectURI(callBackUrl)
                        .setResponseType(authzGrantType)
                        .setScope(scope)
                        .buildQueryMessage();
                response.sendRedirect(authzRequest.getLocationUri());
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
       // }
        
    
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
