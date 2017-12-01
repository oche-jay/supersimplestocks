package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.response.PerformanceToEarningRatio;

import java.io.IOException;
import java.math.BigDecimal;

import static java.net.HttpURLConnection.*;

public class PerformanceToEarningRatioHandler extends SimpleHandler implements HttpHandler {

    PerformanceToEarningRatio peRatioObj;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            params = getAndValidateParams(httpExchange, "GET", 3);
            stockSymbol = params[1];
            marketPriceStr = params[2];

            BigDecimal marketPrice = new BigDecimal(marketPriceStr);
            BigDecimal peRatio = calculator.calculatePriceToEarningRatio(stockSymbol, marketPrice);

            peRatioObj = new PerformanceToEarningRatio(stockSymbol, marketPrice, peRatio);
            responseMsg = new Gson().toJson(peRatioObj);
            setHttpResponseCode(HTTP_OK);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            String errorMsg = String.format("could not convert \"%s\" to a BigDecimal number", marketPriceStr);
            responseMsg = handleException(errorMsg, HTTP_BAD_REQUEST);
        } catch (Exception e) {
            responseMsg = handleException(e, HTTP_INTERNAL_ERROR);
        } finally {
            sendResponse(responseMsg, httpResponseCode);
        }


    }


}
