package calculator;

import com.google.common.io.Resources;
import lombok.Getter;
import lombok.Setter;
import model.config.SimpleStocksConfig;
import model.domain.Stock;
import model.request.Trade;
import model.response.DividendYield;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Consumer;

public class StockCalculator {
    protected @Getter SimpleStocksConfig config;
    protected @Getter HashMap<String, SortedSet<Trade>> groupedTrades;

    private String DEFAULT_CONFIG = "config.yaml";

    private @Setter static volatile StockCalculator instance;
    private static Object mutex = new Object();

    private StockCalculator() throws IOException {
        this.config = getConfigFromFile(DEFAULT_CONFIG);
        groupedTrades = new HashMap<>();
    }

    public static StockCalculator getInstance() {
        StockCalculator result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    try {
                        instance = result = new StockCalculator();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        return result;
    }

    public SimpleStocksConfig getConfigFromFile(String configFile) {
        try {
            return new Yaml().loadAs(Resources.getResource(configFile).openStream(), SimpleStocksConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SimpleStocksConfig getDefaultConfig() {
        return getConfigFromFile(DEFAULT_CONFIG);
    }

    public BigDecimal calculateDividendYield(String symbol, BigDecimal marketPrice) {
        Stock stock = config.getStocks().stream().filter(k -> k.getStock().equals(symbol)).findFirst().get();
        BigDecimal dividendYield;

        switch (stock.getType()) {
            case COMMON:
                dividendYield = stock.getLastDividend().divide(marketPrice, 4, RoundingMode.HALF_UP);
                break;
            case PREFERRED:
                dividendYield = stock.getFixedDividend().multiply(stock.getParValue()).divide(marketPrice, 4, BigDecimal.ROUND_HALF_UP);
                break;
            default:
                throw new RuntimeException("type: " + stock.getType() + " not recognised");
        }
        return dividendYield;
    }

    public DividendYield getDividendYield(String symbol, BigDecimal marketPrice) {
        Stock stock = config.getStocks().stream().filter(k -> k.getStock().equals(symbol)).findFirst().get();
        BigDecimal dividendYield =  calculateDividendYield(symbol, marketPrice);

        return new DividendYield(symbol, marketPrice, dividendYield, stock.getType());
    }

    public BigDecimal calculatePriceToEarningRatio(String symbol, BigDecimal marketPrice) {
        Stock stock = config.getStocks().stream().filter(k -> k.getStock().equals(symbol)).findFirst().get();
        return marketPrice.divide(stock.getLastDividend(), 4, BigDecimal.ROUND_HALF_UP);

    }

    public void recordTrade(Trade trade) {
        BigDecimal price = trade.getPrice();
        BigDecimal volume = new BigDecimal(trade.getQuantity());

        if (price.doubleValue() >= 0 && volume.doubleValue() > 0) {
            SortedSet<Trade> groupSet = groupedTrades.getOrDefault(trade.getStock(), new TreeSet<>());
            groupSet.add(trade);
            groupedTrades.put(trade.getStock(), groupSet);
        } else if (price.doubleValue() < 0) {
            System.out.println("price must not be less than zero");
        } else if (volume.doubleValue() < 1) {
            System.out.println("quanity must not be less than one");
        }

    }

    public BigDecimal calculateVolumeWeightedStockPrice(String symbol, int minutes) {
        BigDecimal vwsp = new BigDecimal("0").setScale(2);
        SortedSet<Trade> trades = groupedTrades.get(symbol);

        if (trades != null && trades.size() > 0) {

            final BigDecimal[] x = {new BigDecimal(0), new BigDecimal(0)};

            Consumer<Trade> tradeConsumer = trade -> {
                BigDecimal price = trade.getPrice();
                BigDecimal volume = new BigDecimal(trade.getQuantity());

                if (price.doubleValue() >= 0 && volume.doubleValue() > 0) {
                    x[0] = x[0].add(price.multiply(volume));
                    x[1] = x[1].add(new BigDecimal(trade.getQuantity()));
                } else if (price.doubleValue() < 0) {
                    System.out.println("price must not be less than zero");
                } else if (volume.doubleValue() < 1) {
                    System.out.println("quantity must not be less than one");
                }

            };

            long rightNow = System.currentTimeMillis();
            long xMinutesAgo = rightNow - (minutes * 60 * 1000);

            trades.stream().filter(k -> k.getTimestamp() >= xMinutesAgo).forEach(tradeConsumer);

            if (x[1].doubleValue() > 0) {
                vwsp = x[0].divide(x[1], 2, RoundingMode.HALF_UP);
            } else {
                System.out.printf("0 total quantity of trades for stock: %s since %s\n", symbol, new Date(xMinutesAgo));
            }

            System.out.printf("volume weighted stock prices for stock: %s : %f\n ", symbol, vwsp);

            return vwsp;
        } else {
            throw new RuntimeException(String.format("No trades for stock : %s have been recorded", symbol));
        }
    }

    public BigDecimal calculateAllShareIndex(){
        double product = 1;
        double count =  0;

        final double[] x = { product, count };

        Consumer<Trade> geometricMeanConsumer = trade -> {
            x[0]= x[0] * trade.getPrice().doubleValue(); //product of price
            x[1] = x[1] + 1; //count of trades
        };

        Iterator it = groupedTrades.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, SortedSet<Trade>> pair = (Map.Entry) it.next();
            SortedSet<Trade> trades = pair.getValue();
            trades.stream().filter(trade -> trade.getPrice().doubleValue() > 0 ).forEach(geometricMeanConsumer);
        }
        count = x[1];
        product = x[0] ;
        double power = 1 / count;
        double geometricMean = Math.pow(product, power);

        return new BigDecimal(geometricMean).setScale(4, RoundingMode.HALF_UP);
    }
}
