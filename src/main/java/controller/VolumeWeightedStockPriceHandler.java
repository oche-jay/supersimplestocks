package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.response.VolumeWeightedStockPrice;

import java.io.IOException;
import java.math.BigDecimal;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;

public class VolumeWeightedStockPriceHandler extends SimpleHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try{
            params = getAndValidateParams(httpExchange, "GET", 2);
            String stock = params[1];
            BigDecimal vwsp = calculator.calculateVolumeWeightedStockPrice(stock, 15);
            VolumeWeightedStockPrice volumeWeightedStockPrice = new VolumeWeightedStockPrice(stock, vwsp);
            responseMsg = new Gson().toJson(volumeWeightedStockPrice);
            setHttpResponseCode(HTTP_OK);;
        } catch (Exception e){
            handleException(e, HTTP_INTERNAL_ERROR);
        }
        finally {
            sendResponse(responseMsg, httpResponseCode);
        }
    }
}
