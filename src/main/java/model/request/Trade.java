package model.request;

import lombok.Data;
import model.domain.Indicator;
import model.util.NonNegative;
import model.util.PositiveValue;
import model.util.Required;

import java.math.BigDecimal;

@Data
public class Trade implements Comparable<Trade>{

    @Required private String symbol;
    @Required @PositiveValue private int quantity;
    @Required @NonNegative private BigDecimal price;
    @Required private Indicator indicator;

    private Long timestamp;

    public Trade(Long timestamp, String symbol, int quantity, BigDecimal price, Indicator indicator){
        setTimestamp(timestamp);
        setSymbol(symbol);
        setQuantity(quantity);
        setPrice(price);
        setIndicator(indicator);
    }

    public Trade(String symbol, int quantity, BigDecimal price, Indicator indicator){
        setSymbol(symbol);
        setQuantity(quantity);
        setPrice(price);
        setIndicator(indicator);
    }

    @Override
    public int compareTo(Trade t) {
        Long l = (this.timestamp - t.timestamp);
        return l.intValue();
    }

}

