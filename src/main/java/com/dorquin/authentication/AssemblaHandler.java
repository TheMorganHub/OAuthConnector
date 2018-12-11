package com.dorquin.authentication;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.json.JSONObject;

public class AssemblaHandler extends OAuthRequestHandler {

    public AssemblaHandler() {
    }

    public AssemblaHandler(String clientId, String clientSecret, AccessTokenCallback callback) {
        super(clientId, clientSecret, callback);
    }

    @Override
    public void onHandle(String string, Request rqst, HttpServletRequest hsr, HttpServletResponse hsr1) {
        if (!rqst.getParameterMap().containsKey("code")) {
            return;
        }
        String code = rqst.getParameter("code");
        requestAccessToken(code);
    }

    @Override
    protected void requestAccessToken(String code) {
        try {
            HttpResponse<String> response = Unirest.post("https://api.assembla.com/token")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body("code=" + code + "&grant_type=authorization_code&client_id=" + clientId + "&client_secret=" + clientSecret)
                    .asString();
            JSONObject json = new JSONObject(response.getBody());
            callback.onAccessTokenSuccess(json.getString("access_token"), json.getString("refresh_token"));
        } catch (UnirestException e) {
            System.err.println(e.getMessage());
        }

    }
    
    @Override
    public void printMessage(HttpServletResponse response) {
        try {
            response.setContentType("text/html; charset=utf-8");
            PrintWriter out = response.getWriter();
            out.println("The Assembla code has been received. You may close this window.");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
