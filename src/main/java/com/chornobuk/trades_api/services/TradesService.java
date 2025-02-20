package com.chornobuk.trades_api.services;

import com.chornobuk.trades_api.exceptions.CustomIncorrectHeaderException;
import com.chornobuk.trades_api.entities.Product;
import com.chornobuk.trades_api.entities.Trade;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradesService {

    private final ProductsService service;

    public String enrich(MultipartFile file) {
        List<Trade> trades = readFromCsv(file);
        return convertToCsv(trades);
    }

    private List<Trade> readFromCsv(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String[] headers = {"date", "productId", "currency", "price"};
            String[] fHeaders = reader.readLine().split(",");
            if (!Arrays.equals(headers, fHeaders)) {
                log.debug("required headers: {}  headers from file: {}", Arrays.toString(headers), Arrays.toString(fHeaders));
                throw new CustomIncorrectHeaderException("Different headers");
            }
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            Stream<String> lines = reader.lines();
            List<CompletableFuture<Trade>> futures = lines
                    .map(line -> line.split(","))
                    .map(data -> CompletableFuture.supplyAsync(() -> {
                        Trade trade = new Trade();
                        try {
                            trade.setDate(LocalDate.parse(data[0], DateTimeFormatter.ofPattern("yyyyMMdd")));
                        } catch (DateTimeParseException e) {
                            log.warn("invalid date: \"{}\" ", data[0]);
                            return null; // incorrect data format ignoring the row
                        }
                        Product product = service.getById(Long.valueOf(data[1])).orElse(null);
                        trade.setProduct(product);
                        trade.setCurrency(data[2]);
                        trade.setPrice(new BigDecimal(data[3]));
                        return trade;
                    }, executorService))
                    .toList();
            return futures.stream()
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                        t.getPrice().stripTrailingZeros().toPlainString()
                };
                csvWriter.writeNext(data);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stringWriter.toString();
    }
}
