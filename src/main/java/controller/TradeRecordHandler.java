package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.request.Trade;
import model.util.AnnotatedDeserializer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import static java.net.HttpURLConnection.*;

public class TradeRecordHandler extends SimpleHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            long currentTime = System.currentTimeMillis();
            params = getAndValidateParams(httpExchange, "POST", 1);

            Reader reader = new InputStreamReader(httpExchange.getRequestBody());
            Gson gson = new GsonBuilder().registerTypeAdapter(Trade.class, new AnnotatedDeserializer<Trade>()).create();

            Trade trade = gson.fromJson(reader, Trade.class);
            trade.setTimestamp(currentTime);

            calculator.recordTrade(trade);
            setHttpResponseCode(HTTP_OK);
            ;
            String success = "Successfully recorded trade";
            responseMsg = handleGenericMessage(success, httpResponseCode);

        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            String errorMsg = String.format("could not convert \"%s\" to a BigDecimal number", marketPriceStr);
            responseMsg = handleException(errorMsg, HTTP_BAD_REQUEST);
        } catch (JsonParseException jpe) {
            responseMsg = handleException(jpe, HTTP_BAD_REQUEST);
        } catch (Exception e) {
            responseMsg = handleException(e, HTTP_INTERNAL_ERROR);
        } finally {
            sendResponse(responseMsg, httpResponseCode);
        }
    }
}
