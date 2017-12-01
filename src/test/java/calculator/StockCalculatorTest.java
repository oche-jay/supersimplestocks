package calculator;

import model.config.SimpleStocksConfig;
import model.domain.Stock;
import model.domain.StockType;
import model.request.Trade;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static model.domain.Indicator.BUY;
import static model.domain.Indicator.SELL;
import static model.domain.StockType.COMMON;
import static model.domain.StockType.PREFERRED;
import static org.testng.Assert.*;

public class StockCalculatorTest {

    static String DEFAULT_CONFIG = "config.yaml";

    @Test(dataProvider = "defaultStocks")
    public void testDefaultConfig(String symbol, StockType type, Double lastDividend, Double fixedDividend, Double parValue) throws IOException {
        StockCalculator stockCalculator = StockCalculator.getInstance();
        SimpleStocksConfig simpleStocksConfig = stockCalculator.getConfigFromFile(DEFAULT_CONFIG);

        assertEquals(simpleStocksConfig.getStocks().size() , 5);

        Stock stock = simpleStocksConfig.getStocks().stream().filter(k -> k.getStock().equals(symbol)).findFirst().get();
        assertEquals(stock.getStock(), symbol);

        assertEquals(stock.getType(), type);
        if (stock.getType() == COMMON){
            assertNull(stock.getFixedDividend());
        } else if (stock.getType() == PREFERRED){
            assertEquals(stock.getFixedDividend().doubleValue(), fixedDividend);
        }

        assertEquals(stock.getLastDividend().doubleValue(), lastDividend);
        assertEquals(stock.getParValue().doubleValue(), parValue);
    }

    @Test(dataProvider = "dividendYield")
    public void testCalculateDividendYield(String stock, StockType type, Double marketPrice, Double expectedDividendYield) {
        StockCalculator stockCalculator = StockCalculator.getInstance();
        BigDecimal actual = stockCalculator.calculateDividendYield(stock, new BigDecimal(marketPrice));
        BigDecimal expected =  new BigDecimal(expectedDividendYield).setScale(4, RoundingMode.HALF_UP);
        assertEquals( actual, expected);
    }

    @Test(dataProvider = "priceToEarningRatio")
    public void testCalculatePriceToEarningRatio(String stock, StockType type, Double marketPrice, Double expectedDividendYield) {
        StockCalculator stockCalculator = StockCalculator.getInstance();
        BigDecimal actual = stockCalculator.calculatePriceToEarningRatio(stock, new BigDecimal(marketPrice));
        BigDecimal expected =  new BigDecimal(expectedDividendYield).setScale(4, RoundingMode.HALF_UP);
        assertEquals( actual, expected);
    }

    @Test(dataProvider = "sameTrades")
    public void testRecordSameTradesandGetVolumeWeightedStockPrice(Trade trade, double vswp ){
        StockCalculator stockCalculator = StockCalculator.getInstance();
        System.out.println(stockCalculator);
        stockCalculator.recordTrade(trade);
        assertEquals(stockCalculator.calculateVolumeWeightedStockPrice(trade.getStock(), 15), new BigDecimal(vswp).setScale(2, RoundingMode.HALF_UP) );

        System.out.println("All Share Index:" + stockCalculator.calculateAllShareIndex());
    }

    @Test(dataProvider = "diverseTrades")
    public void testRecordDifferentTradesandGetVolumeWeightedStockPrice(Trade trade, double vswp ){
        StockCalculator stockCalculator = StockCalculator.getInstance();
        stockCalculator.recordTrade(trade);
        assertEquals(stockCalculator.calculateVolumeWeightedStockPrice(trade.getStock(), 15), new BigDecimal(vswp).setScale(2, RoundingMode.HALF_UP) );

        System.out.println("All Share Index:" + stockCalculator.calculateAllShareIndex());
    }

    @DataProvider
    public Object[][] diverseTrades(){
        StockCalculator.setInstance(null);
        Long epoch = System.currentTimeMillis();
        return new Object[][]{
                { new Trade(++epoch, "TEA", 1000,      new BigDecimal("50"), BUY ),  50.0},
                { new Trade(++epoch, "TEA", 1000,      new BigDecimal("25"), SELL ), 37.5},
                { new Trade(++epoch, "JOE", 1,         new BigDecimal("50000"), BUY ), 50000.0},
                { new Trade(++epoch, "JOE", 1,         new BigDecimal("50000"), BUY ), 50000.0},
                { new Trade(++epoch, "TEA", 100,       new BigDecimal("50"), BUY ),  38.1},
                { new Trade(++epoch, "TEA", 46,        new BigDecimal("29"), BUY ),  37.9},
                { new Trade(++epoch, "TEA", 99999999,  new BigDecimal("50"), BUY ),  50.0},
                { new Trade(++epoch, "TEA", 99999999,  new BigDecimal("0.000001"), BUY ), 25.0},//0 quantity should be processed
                { new Trade(++epoch, "TEA", 0,         new BigDecimal("5000000000000"), BUY ), 25.0},
                { new Trade(++epoch, "TEA", -1,        new BigDecimal("5000000000000"), BUY ), 25.0}, //should ignore
                { new Trade(++epoch, "TEA", 99999999,  new BigDecimal("0.000001"), BUY ), 16.67},
                { new Trade(++epoch, "TEA", 99999999, new BigDecimal("0"), BUY ), 12.5},
                { new Trade(++epoch, "TEA", 1000000000, new BigDecimal("-1.00"), BUY ), 12.5},

        };
    }

    @Test(dataProvider = "diverseTrades", priority = 1)
    public void testRecordTradesandGetVolumeWeightedStockPrice_Over15MinutesAgo(Trade trade, double vswp ){
        StockCalculator stockCalculator = StockCalculator.getInstance();
        System.out.println(stockCalculator);
        stockCalculator.recordTrade(trade);
        assertEquals(stockCalculator.calculateVolumeWeightedStockPrice(trade.getStock(), 15), new BigDecimal(vswp).setScale(2, RoundingMode.HALF_UP) );
    }

    @DataProvider
    public Object[][] sameTrades(){
        StockCalculator.setInstance(null);
        Long now = System.currentTimeMillis();
        Long fifteenMinutesAgo = now - (15 * 60 * 1000);
        Long tenMinutesAgo =  now - (10 * 60 * 1000);
        return new Object[][]{
                { new Trade(fifteenMinutesAgo, "TEA", 100000, new BigDecimal("10"), BUY ), 0.0}, //should not be used in calculation
                { new Trade(tenMinutesAgo, "TEA", 10, new BigDecimal("50"), BUY ), 50.0}, //should not be used in calculation
                { new Trade(++now, "TEA", 1000, new BigDecimal("50"), BUY ), 50.0},
                { new Trade(++now, "TEA", 1000, new BigDecimal("50"), BUY ), 50.0},
                { new Trade(++now, "TEA", 1000, new BigDecimal("50"), BUY ), 50.0},
                { new Trade(++now, "TEA", 1000, new BigDecimal("50"), BUY ), 50.0},
                { new Trade(++now, "TEA", 1000, new BigDecimal("50"), BUY ), 50.0},
                { new Trade(++now, "TEA", 1000, new BigDecimal("50"), BUY ), 50.0},
                { new Trade(++now, "TEA", 1000, new BigDecimal("50"), BUY ), 50.0},
                { new Trade(++now, "TEA", 1000, new BigDecimal("50"), BUY ), 50.0},
                { new Trade(++now, "TEA", 1000, new BigDecimal("50"), BUY ), 50.0},
                { new Trade(++now, "TEA", 1000, new BigDecimal("50"), BUY ), 50.0},
        };
    }

    @DataProvider
    public Object[][] sameTrades_15minutesAgo(){
        Long epoch = System.currentTimeMillis();
        return new Object[][]{
                { new Trade(epoch - (15 * 60* 1000), "TEA", 100000, new BigDecimal("10"), BUY ), 0.0}, //should not be used in calculation

        };
    }




    @DataProvider
    public Object[][] dividendYield(){
        return new Object[][]{
                //Symbol, Type, marketPrice, expectedDividendYield
//                {"TEA", COMMON,     0.0,  0.0},
                {"TEA", COMMON,     10.0,  0.0},
                {"TEA", COMMON,     5.0,   0.0},
                {"TEA", COMMON,     -1.0,   0.0},
                {"POP", COMMON,     8.0,  1.0},
                {"POP", COMMON,     -8.0,  -1.0},
                {"GIN", PREFERRED,  8.0,   0.25},
                {"GIN", PREFERRED,  25.0,   0.08},
        };
    }

    @DataProvider
    public Object[][] priceToEarningRatio(){
        return new Object[][]{
                //Symbol, Type, marketPrice, p-e-ratio
                {"ALE", COMMON,     23.0,  1.0},
                {"ALE", COMMON,     27.0,  1.1739},
                {"ALE", COMMON,     -9.0,  -0.3913},
                {"ALE", COMMON,     9.0,  0.3913},
                {"ALE", COMMON,     0.0,  0.0},
                {"GIN", PREFERRED,  999.0,   124.875}

        };
    }

    @DataProvider
    public Object[][] defaultStocks(){
        return new Object[][]{
                //Symbol, Type, lastDividend, FixedDividend, parValue
                {"TEA", COMMON,     0.0,  null, 100.0},
                {"POP", COMMON,     8.0,  null, 100.0},
                {"ALE", COMMON,     23.0, null, 60.0},
                {"GIN", PREFERRED,  8.0,  0.02, 100.0},
                {"JOE", COMMON,     13.0, null, 250.0}

        };
    }



}