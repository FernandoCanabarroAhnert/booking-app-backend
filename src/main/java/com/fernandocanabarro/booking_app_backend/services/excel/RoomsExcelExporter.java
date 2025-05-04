package com.fernandocanabarro.booking_app_backend.services.excel;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomResponseDTO;
import com.fernandocanabarro.booking_app_backend.services.exceptions.BadRequestException;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public class RoomsExcelExporter {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<RoomResponseDTO> rooms;

    public RoomsExcelExporter(List<RoomResponseDTO> rooms) {
        this.rooms = rooms;
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Rooms");
    }

    private void writeHeaderRow() {
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);
        List<String> headers = List.of("Id", "Número", "Andar", "Tipo", "Diária", "Descrição", "Capacidade", "Hotel Id");
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(style);
        }
        
    }

    private void writeDataRows() {
        int rowCount = 1;
        for (RoomResponseDTO room : rooms) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(room.getId());
            sheet.autoSizeColumn(0);
            row.createCell(1).setCellValue(room.getNumber());
            sheet.autoSizeColumn(1);
            row.createCell(2).setCellValue(room.getFloor());
            sheet.autoSizeColumn(2);
            row.createCell(3).setCellValue(room.getType().toString());
            sheet.autoSizeColumn(3);
            row.createCell(4).setCellValue("R$ " + String.valueOf(room.getPricePerNight()).replace(".", ","));
            sheet.autoSizeColumn(4);
            row.createCell(5).setCellValue(room.getDescription());
            sheet.autoSizeColumn(5);
            row.createCell(6).setCellValue(room.getCapacity());
            sheet.autoSizeColumn(6);
            row.createCell(7).setCellValue(room.getHotelId());
            sheet.autoSizeColumn(7);
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
            throw new BadRequestException("Error exporting rooms to Excel");
        }
    }

}
