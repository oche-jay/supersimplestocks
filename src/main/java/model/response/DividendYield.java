package model.response;

import lombok.Data;
import model.domain.StockType;

import java.math.BigDecimal;

@Data
public class DividendYield {
    private String stock;
    private BigDecimal marketPrice;
    private BigDecimal dividendYield;
    private StockType type;

    public DividendYield(String stock, BigDecimal marketPrice, BigDecimal dividendYield) {
        setStock(stock);
        setMarketPrice(marketPrice);
        setDividendYield(dividendYield);
    }

    public DividendYield(String stock, BigDecimal marketPrice, BigDecimal dividendYield, StockType type) {
        setStock(stock);
        setMarketPrice(marketPrice);
        setDividendYield(dividendYield);
        setType(type);
    }

    public DividendYield() {
    }
}
