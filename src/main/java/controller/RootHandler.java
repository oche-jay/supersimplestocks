package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.util.logging.Logger;

import java.io.IOException;
import java.io.OutputStream;

import static java.net.HttpURLConnection.HTTP_OK;


public class RootHandler implements HttpHandler {
    private final Logger logger = Logger.getLogger( this.getClass().getName() );

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
            logger.info("received message");
            String msg = "Super Simple Stocks v1";
            httpExchange.sendResponseHeaders(HTTP_OK, msg.length());
            OutputStream responseBody = httpExchange.getResponseBody();
            responseBody.write(msg.getBytes());
            responseBody.close();
        }
}

