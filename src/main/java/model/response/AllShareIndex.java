package model.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AllShareIndex {
    private BigDecimal allShareIndex;

    public AllShareIndex(BigDecimal allShareIndex) {
        setAllShareIndex(allShareIndex);
    }
}
