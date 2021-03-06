package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.response.DividendYield;

import java.io.IOException;
import java.math.BigDecimal;

import static java.net.HttpURLConnection.*;

/**
 * Given a market price as input, calculate the dividend yield
 */
public class DividendYieldHandler extends SimpleHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            params = getAndValidateParams(httpExchange, "GET", 3);
            stockSymbol = params[1];
            marketPriceStr = params[2];
            BigDecimal marketPrice = new BigDecimal(marketPriceStr);
            DividendYield dividendYield = calculator.getDividendYield(stockSymbol, marketPrice);
            setHttpResponseCode(HTTP_OK);
            ;
            responseMsg = new Gson().toJson(dividendYield);
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