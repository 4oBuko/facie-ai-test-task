package com.chornobuk.trades_api.services;

import com.chornobuk.trades_api.CustomIncorrectHeaderException;
import com.chornobuk.trades_api.entities.Trade;
import com.chornobuk.trades_api.entities.dtos.TradeDto;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradesService {

    private final ProductsService service;

    public String enrich(MultipartFile file) {
        List<TradeDto> dtos = readFromCsv(file);
        List<Trade> trades = getProducts(dtos);
        return convertToCsv(trades);
    }

    private List<TradeDto> readFromCsv(MultipartFile file) {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<TradeDto> csvToBean = new CsvToBeanBuilder<TradeDto>(reader)
                    .withType(TradeDto.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return csvToBean.parse();
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
            throw  new CustomIncorrectHeaderException(e.getMessage());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Trade> getProducts(List<TradeDto> dtos) {
        List<Trade> trades = new LinkedList<>();
        for (TradeDto dto : dtos) {
            if (dto.getDate() != null) {
                Trade trade = Trade.builder()
                        .date(dto.getDate())
                        .currency(dto.getCurrency())
                        .price(dto.getPrice())
                        .build();
                trade.setProduct(service.getById(dto.getProductId()).orElse(null));
                trades.add(trade);
            }
        }
        return trades;
    }

    private String convertToCsv(List<Trade> trades) {
        StringWriter stringWriter = new StringWriter();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        try (CSVWriter csvWriter = new CSVWriter(
                stringWriter,
                ICSVWriter.DEFAULT_SEPARATOR,
                ICSVWriter.NO_QUOTE_CHARACTER,
                ICSVWriter.DEFAULT_ESCAPE_CHARACTER,
                ICSVWriter.DEFAULT_LINE_END)) {

            String[] header = {"date", "productName", "currency", "price"};
            csvWriter.writeNext(header);

            for (Trade t : trades) {
                String[] data = {
                        formatter.format(t.getDate()),
                        t.getProduct() == null ? "Missing Product Name" : t.getProduct().getName(),
                        t.getCurrency(),
                        t.getPrice().stripTrailingZeros().toString()
                };
                csvWriter.writeNext(data);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stringWriter.toString();
    }
}
