package com.dorquin.authentication;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.jetty.server.Server;

public class OAuthInstance {

    private Server server;
    public String url;
    public static final int ASSEMBLA = 1;

    private OAuthInstance() {
    }

    public static class Builder {

        private int port = 8080;
        private OAuthRequestHandler handler;
        private String clientId;
        private String clientSecret;
        private int providerId;
        private String url;        
        private OAuthRequestHandler.AccessTokenCallback onTokenSuccessCallback;

        public Builder at(int port) {
            this.port = port;
            return this;
        }

        public Builder handleWith(OAuthRequestHandler handler) {
            this.handler = handler;
            return this;
        }

        public Builder fromProvider(int providerId) {
            this.providerId = providerId;
            return this;
        }

        public Builder fromUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder withClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder withClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder onAccessTokenSuccess(OAuthRequestHandler.AccessTokenCallback onTokenSuccessCallback) {
            this.onTokenSuccessCallback = onTokenSuccessCallback;
            return this;
        }

        private String getProviderUrl() {
            switch (providerId) {
                case ASSEMBLA:
                    return "https://api.assembla.com/authorization?client_id=" + clientId + "&response_type=code";
            }
            return null;
        }

        private OAuthRequestHandler getHandlerForProvider() {
            switch (providerId) {
                case ASSEMBLA:
                    return new AssemblaHandler(clientId, clientSecret, onTokenSuccessCallback);
            }
            return null;
        }

        public OAuthInstance build() {
            try {
                String providerUrl = getProviderUrl();
                Desktop.getDesktop().browse(new URI(providerUrl == null ? url : providerUrl));
                OAuthInstance oAuthInstance = new OAuthInstance();

                oAuthInstance.server = new Server(this.port);
                OAuthRequestHandler handlerForProvider = getHandlerForProvider();
                oAuthInstance.server.setHandler(handlerForProvider == null ? handler : handlerForProvider);
                return oAuthInstance;
            } catch (IOException | URISyntaxException e) {
                System.err.println(e);
            }
            return null;
        }
    }

    public void start() {
        Thread t = new Thread(() -> {
            try {
                server.start();
                server.join();
            } catch (Exception e) {
            }
        });
        t.start();
    }

    public void stop() {
        Thread t = new Thread(() -> {
            try {
                server.stop();
            } catch (Exception e) {
            }
        });
        t.start();
    }
}
