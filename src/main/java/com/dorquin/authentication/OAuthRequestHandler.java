package com.dorquin.authentication;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public abstract class OAuthRequestHandler extends AbstractHandler {

    protected AccessTokenCallback callback;
    protected String clientId;
    protected String clientSecret;

    public OAuthRequestHandler() {
    }

    public OAuthRequestHandler(String clientId, String clientSecret, AccessTokenCallback callback) {
        this.callback = callback;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public void handle(String string, Request rqst, HttpServletRequest hsr, HttpServletResponse hsr1) throws IOException, ServletException {
        onHandle(string, rqst, hsr, hsr1);
        printMessage(hsr1);
        hsr1.setStatus(HttpServletResponse.SC_OK);
        rqst.setHandled(true);
        shutdownServer();
    }

    private void shutdownServer() {
        try {
            Thread shutdownThread = new Thread(() -> {
                try {
                    Thread.sleep(2000); //we wait for printout to return message then we shutdown
                    getServer().stop();
                } catch (Exception ex) {
                }
            });
            shutdownThread.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void printMessage(HttpServletResponse response) {
    }

    protected abstract void requestAccessToken(String code);

    public abstract void onHandle(String string, Request rqst, HttpServletRequest hsr, HttpServletResponse hsr1);

    public static interface AccessTokenCallback {

        void onAccessTokenSuccess(String accessToken, String refreshToken);
    }
}
