package com.fernandocanabarro.booking_app_backend.utils;

import java.io.IOException;
import java.util.Base64;

import org.springframework.web.multipart.MultipartFile;

import com.fernandocanabarro.booking_app_backend.services.exceptions.ImageGeneratingException;

public class FileUtils {

    public static String generateBase64Image(MultipartFile file) {
        String base64Prefix = "data:image/png;base64,";
        try {
            byte[] imageBytes = file.getBytes();
            String base64Image = base64Prefix + Base64.getEncoder().encodeToString(imageBytes);
            return base64Image;
        } catch (IOException e) {
            throw new ImageGeneratingException();
        }
        
    }

}
