package com.fernandocanabarro.booking_app_backend.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fernandocanabarro.booking_app_backend.services.exceptions.BadRequestException;

public class DateUtils {

    public static Date convertStringParamToDate(String value) {
        try {
            if (value == null || value.isEmpty()) return null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(value);
        }
        catch (ParseException e) {
            throw new BadRequestException("Invalid date format. Expected format: yyyy-MM-dd");
        }
    }

}
