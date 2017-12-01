package model.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Stock {

    private String stock;
    private String currency = "GBX"; //default to GBp
    private StockType type;
    private BigDecimal lastDividend;
    private BigDecimal fixedDividend;
    private BigDecimal parValue;

}
