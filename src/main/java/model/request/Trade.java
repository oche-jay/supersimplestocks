package model.request;

import lombok.Data;
import model.domain.Indicator;
import model.util.NonNegative;
import model.util.PositiveValue;
import model.util.Required;

import java.math.BigDecimal;

@Data
public class Trade implements Comparable<Trade> {

    @Required
    private String stock;
    @Required
    @PositiveValue
    private int quantity;
    @Required
    @NonNegative
    private BigDecimal price;
    @Required
    private Indicator indicator;

    private Long timestamp;

    public Trade(Long timestamp, String stock, int quantity, BigDecimal price, Indicator indicator) {
        setTimestamp(timestamp);
        setStock(stock);
        setQuantity(quantity);
        setPrice(price);
        setIndicator(indicator);
    }

    public Trade(String stock, int quantity, BigDecimal price, Indicator indicator) {
        setStock(stock);
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

