package com.chornobuk.trades_api.entities.dtos;

import com.chornobuk.trades_api.utils.CustomDateParser;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeDto {

    @CsvCustomBindByName(column = "date", converter = CustomDateParser.class, required = true)
    private LocalDate date;

    @CsvBindByName(column = "productId", required = true)
    private Long productId;

    @CsvBindByName(column = "currency", required = true)
    private String currency;

    @CsvBindByName(column = "price", required = true)
    private BigDecimal price;

}
