package com.fernandocanabarro.booking_app_backend.services.excel;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fernandocanabarro.booking_app_backend.models.dtos.hotel.HotelResponseDTO;
import com.fernandocanabarro.booking_app_backend.services.exceptions.BadRequestException;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public class HotelExcelExporter {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<HotelResponseDTO> hotels;

    public HotelExcelExporter(List<HotelResponseDTO> hotels) {
        this.hotels = hotels;
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Hotels");
    }

    private void writeHeaderRow() {
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);
        List<String> headers = List.of("Id", "Nome", "Qtde. Quartos", "Rua", "Número", "Cidade", "CEP", "Estado","Telefone");
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(style);
        }
    }

    private void writeDataRows() {
        int rowCount = 1;
        for (HotelResponseDTO hotel : hotels) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(hotel.getId());
            sheet.autoSizeColumn(0);
            row.createCell(1).setCellValue(hotel.getName());
            sheet.autoSizeColumn(1);
            row.createCell(2).setCellValue(hotel.getRoomQuantity());
            sheet.autoSizeColumn(2);
            row.createCell(3).setCellValue(hotel.getStreet());
            sheet.autoSizeColumn(3);                
            row.createCell(4).setCellValue(hotel.getNumber());
            sheet.autoSizeColumn(4);
            row.createCell(5).setCellValue(hotel.getCity());
            sheet.autoSizeColumn(5);
            row.createCell(6).setCellValue(hotel.getZipCode());
            sheet.autoSizeColumn(6);
            row.createCell(7).setCellValue(hotel.getState());
            sheet.autoSizeColumn(7);
            row.createCell(8).setCellValue(hotel.getPhone());
            sheet.autoSizeColumn(8);
        }
    }

    public void export(HttpServletResponse response) {
        try {
            writeHeaderRow();
            writeDataRows();
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        }
        catch (IOException e) {
            throw new BadRequestException("Error exporting hotels to Excel");
        }
    }

}
