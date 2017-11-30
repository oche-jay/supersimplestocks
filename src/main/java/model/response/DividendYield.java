package model.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DividendYield {
    private String stock;
    private BigDecimal marketPrice;
    private BigDecimal dividendYield;

    public DividendYield(String stock, BigDecimal marketPrice, BigDecimal dividendYield){
        setStock(stock);
        setMarketPrice(marketPrice);
        setDividendYield(dividendYield);
    }

    public DividendYield() {
    }
}
