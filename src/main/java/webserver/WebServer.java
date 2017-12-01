package webserver;

import com.sun.net.httpserver.HttpServer;
import controller.*;
import lombok.Setter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class WebServer {
    private final static Logger logger = Logger.getLogger(WebServer.class.getName());
    @Setter
    static int port = 8000;

    public static void main(String[] args) throws Exception {
        WebServer webServer = new WebServer();
        startServer();
    }

    public static void startServer() throws IOException {
        logger.info("Starting message server on port " + port);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new RootHandler());
        server.createContext("/config", new ConfigHandler());
        server.createContext("/dividend-yield", new DividendYieldHandler());
        server.createContext("/pe-ratio", new PerformanceToEarningRatioHandler());
        server.createContext("/trade", new TradeRecordHandler());
        server.createContext("/volume-weighted-stockprice", new VolumeWeightedStockPriceHandler());
        server.createContext("/all-share-index", new AllShareIndexHandler());
        server.setExecutor(null);
        server.start();


//        1.	Provide working source code that will :-
//                a.	For a given stock,
//                i.	Given a market price as input, calculate the dividend yield
//        Get dividendyield/TEA/{price}
//
//        ii.	Given a market price as input,  calculate the P/E Ratio
//        GET pricetoearningratio/TEA
//
//        iii.	Record a trade, with timestamp, quantity of shares, buy or sell indicator and trade price
//        POST
//
//        iv.	Calculate Volume Weighted Stock Price based on trades in past 15 minutes
//
//        GET VWSP
//
//        b.	Calculate the GBCE All Share Index using the geometric mean of prices for all stocks

    }
}
