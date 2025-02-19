package com.chornobuk.trades_api.entities;

import com.opencsv.bean.CsvBindByName;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@Builder
public class Trade {
    private LocalDate date;
    @CsvBindByName(column = "productName")
    private Product product;
    private String currency;
    private BigDecimal price;

}
