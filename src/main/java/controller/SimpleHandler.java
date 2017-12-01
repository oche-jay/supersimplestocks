package controller;

import calculator.StockCalculator;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import lombok.Setter;
import model.response.GenericResponse;

import java.io.IOException;
import java.io.OutputStream;

import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;


public abstract class SimpleHandler {

    @Setter
    protected int httpResponseCode = 0;
    protected String responseMsg;
    protected String stockSymbol;
    protected String[] params;
    protected String marketPriceStr;
    protected StockCalculator calculator = StockCalculator.getInstance();
    protected HttpExchange httpExchange;


    protected String[] getAndValidateParams(HttpExchange httpExchange, String allowedMethod, int expectdPathParams) {
        this.httpExchange = httpExchange;
        String httpMethod = httpExchange.getRequestMethod().toUpperCase();
        String path = httpExchange.getRequestURI().getPath();
        String[] params = path.substring(1, path.length()).split("/");

        if (!httpMethod.equals(allowedMethod)) {
            setHttpResponseCode(HTTP_BAD_METHOD);
            responseMsg = String.format("Only %s  allowed at this endpoint", allowedMethod);
            throw new RuntimeException(responseMsg);
        } else if (params.length != expectdPathParams) {
            setHttpResponseCode(HTTP_BAD_REQUEST);
            responseMsg = String.format("%s request path is in unexpected format", allowedMethod);
            throw new RuntimeException(responseMsg);
        }
        return params;
    }


    protected void sendResponse(String responseMsg, int httpResponseCode) {
        try {
            Headers headers = httpExchange.getResponseHeaders();
            headers.set("Content-Type", "application/json");
            httpExchange.sendResponseHeaders(httpResponseCode, responseMsg.length());
            OutputStream responseBody = httpExchange.getResponseBody();
            responseBody.write(responseMsg.getBytes());
            responseBody.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String handleGenericMessage(String genericMessage, int httpStatus) {
        setHttpResponseCode(httpStatus);
        GenericResponse genericResponse = new GenericResponse(genericMessage, httpResponseCode);
        return new Gson().toJson(genericResponse);
    }

    protected String handleException(String errorMsg, int errorStatus) {
        setHttpResponseCode(errorStatus);
        GenericResponse genericResponse = new GenericResponse(errorMsg, httpResponseCode);
        return new Gson().toJson(genericResponse);
    }

    protected String handleException(Exception e, int errorStatus) {
        e.printStackTrace();
        setHttpResponseCode(errorStatus);
        String errorMsg = e.getClass().getSimpleName() + ": " + e.getMessage();
        return handleException(errorMsg, httpResponseCode);
    }
}
