package model.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VolumeWeightedStockPrice {
    private String stock;
    private BigDecimal volumeWeightedStockPrice;

    public VolumeWeightedStockPrice(String stock, BigDecimal vwsp) {
        setStock(stock);
        setVolumeWeightedStockPrice(vwsp);
    }
}
