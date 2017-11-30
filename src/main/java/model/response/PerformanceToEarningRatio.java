package model.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class PerformanceToEarningRatio {

    @NonNull private String stock;
    @NonNull private BigDecimal marketPrice;
    @NonNull private BigDecimal peRatio;

    public PerformanceToEarningRatio(String stock, BigDecimal marketPrice, BigDecimal peRatio){
        setStock(stock);
        setMarketPrice(marketPrice);
        setPeRatio(peRatio);
    }

}
