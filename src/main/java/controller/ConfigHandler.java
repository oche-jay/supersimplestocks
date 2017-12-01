package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;

public class ConfigHandler extends SimpleHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        try {
            responseMsg = new Gson().toJson(calculator.getConfig());
            setHttpResponseCode(HTTP_OK);
        } catch (Exception e) {
            responseMsg = handleException(e, HTTP_INTERNAL_ERROR);
        } finally {
            sendResponse(responseMsg, httpResponseCode);
        }

    }
}
