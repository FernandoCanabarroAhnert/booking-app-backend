package com.fernandocanabarro.booking_app_backend.services.jasper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fernandocanabarro.booking_app_backend.services.exceptions.BadRequestException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Service
public class JasperService {

    @Autowired
    private Connection connection;

    private Map<String,Object> params = new HashMap<>();

    public static final String HOTELS = "hotels";
    public static final String ROOMS = "rooms";
    public static final String BOOKINGS = "bookings";
    public static final String USERS = "users";
    public static final String BOLETO = "boleto";
    public static final String BOOKING_SUMMARY = "booking-summary";
    public static final String ROOMS_GROUP_BY_HOTEL = "rooms-group-by-hotel";

    public JasperService() {
        params.put("IMAGES_DIR", getClass().getClassLoader().getResource("jasper/").toString());
        params.put("SUB_REPORT_DIR", getClass().getClassLoader().getResource("jasper/").toString());
    }

    public void addParams(String key, Object value) {
        params.put(key, value);
    }

    public void exportToPdf(HttpServletResponse response, String fileName) {
        try {
            byte[] bytes;
            InputStream jasperStream = getClass().getClassLoader().getResourceAsStream("jasper/" + fileName + ".jasper");
            if (jasperStream == null) {
                throw new ResourceNotFoundException("Jasper file not found: " + fileName);
            }
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperStream, params, connection);
            bytes = JasperExportManager.exportReportToPdf(jasperPrint);
            response.getOutputStream().write(bytes);
        }
        catch (IOException | JRException e) {
            throw new BadRequestException(fileName + " Jasper file not found: " + e.getMessage());
        }
    }

}
