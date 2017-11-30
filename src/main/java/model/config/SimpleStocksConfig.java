package model.config;

import lombok.Data;
import model.domain.Stock;

import java.util.List;

@Data
public class SimpleStocksConfig {
    private List<Stock> stocks;
}
