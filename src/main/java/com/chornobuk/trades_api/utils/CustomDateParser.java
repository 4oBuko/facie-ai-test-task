package com.chornobuk.trades_api.utils;

import com.opencsv.bean.AbstractBeanField;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
public class CustomDateParser extends AbstractBeanField<LocalDate, String> {
    private final static String FORMAT = "yyyyMMdd";
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT);

    @Override
    protected Object convert(String s){
        try {
            return LocalDate.parse(s, formatter);
        } catch (DateTimeParseException e) {
            log.warn(e.getMessage());
            return null;
        }
    }
}
