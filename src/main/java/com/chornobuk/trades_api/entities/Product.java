package com.chornobuk.trades_api.entities;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @CsvBindByName(column = "productId")
    @NonNull
    private Long id;
    
    @CsvBindByName(column = "productName")
    @NonNull
    private String name;
}