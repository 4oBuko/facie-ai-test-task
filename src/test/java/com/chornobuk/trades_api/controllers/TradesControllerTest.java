package com.chornobuk.trades_api.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TradesControllerTest {
    private final static String PATH = "trades.csv";
    private final static String ENDPOINT_PATH = "/api/enrich";
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void enrichData() throws Exception {
        String enrichedData = """
                date,productName,currency,price
                20230101,Commodity Swaps 1,USD,100.25
                20230101,Commodity Swaps,EUR,200.45
                20230101,FX Forward,GBP,300.5
                20230102,Government Bonds Domestic,USD,150.75
                20230102,Convertible Bonds Domestic,EUR,250.85
                20230103,Corporate Bonds International,GBP,350.95
                20230103,Interest Rate Futures,USD,400.1
                20230104,Overnight Index Swaps,EUR,450.2
                20230104,Credit Default Swaps,GBP,500.3
                20230105,Inflation-Linked Bonds,USD,550.4
                20230105,Missing Product Name,EUR,600.5
                20230106,Commodity Swaps,USD,700.6
                20230106,FX Forward,EUR,800.7
                20230107,Missing Product Name,GBP,900.8
                20230107,Missing Product Name,USD,1000.9
                20230108,Commodity Swaps 1,EUR,1100.1
                20230108,Government Bonds Domestic,GBP,1200.2
                20230109,Convertible Bonds Domestic,USD,1300.3
                20230109,Corporate Bonds International,EUR,1400.4
                20230110,Interest Rate Futures,GBP,1500.5
                20131231,Inflation-Linked Bonds,USD,1600.6
                20230111,Convertible Bonds Domestic,GBP,1800.8
                """;
        ClassPathResource resource = new ClassPathResource(PATH);
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "trades.csv",
                "text/csv",
                resource.getInputStream()
        );
        mockMvc.perform(MockMvcRequestBuilders.multipart(ENDPOINT_PATH)
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(content().string(enrichedData));

    }

    @Test
    public void enrichDataEmptyFile() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "trades.csv",
                "text/csv",
                InputStream.nullInputStream()
        );
        mockMvc.perform(MockMvcRequestBuilders.multipart(ENDPOINT_PATH)
                        .file(mockFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void enrichDataIncorrectFile() throws Exception {
        String incorrectData = """
                date, randomField, currency, price
                20230101,Commodity Swaps 1,USD,100.25
                20230101,Commodity Swaps,EUR,200.45
                20230101,FX Forward,GBP,300.5
                20230102,Government Bonds Domestic,USD,150.75
                """;
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "sus-csv",
                "text/csv",
                new ByteArrayInputStream(incorrectData.getBytes())
                );
        mockMvc.perform(MockMvcRequestBuilders.multipart(ENDPOINT_PATH)
                .file(mockFile))
                .andExpect(status().isBadRequest());
    }
}