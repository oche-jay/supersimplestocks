package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.response.AllShareIndex;

import java.io.IOException;
import java.math.BigDecimal;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;

public class AllShareIndexHandler extends SimpleHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            params = getAndValidateParams(httpExchange, "GET", 1);

            BigDecimal allShareIndex = calculator.calculateAllShareIndex();
            AllShareIndex allShareIndexObj = new AllShareIndex(allShareIndex);

            setHttpResponseCode(HTTP_OK);;
            responseMsg = new Gson().toJson(allShareIndexObj);

        } catch (Exception e) {
            responseMsg = handleException(e, HTTP_INTERNAL_ERROR);
        } finally {
            sendResponse(responseMsg, httpResponseCode);
        }
    }
}
