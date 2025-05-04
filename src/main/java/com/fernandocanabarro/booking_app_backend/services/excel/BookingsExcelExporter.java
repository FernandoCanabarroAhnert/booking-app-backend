package com.fernandocanabarro.booking_app_backend.services.excel;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingDetailResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.enums.PaymentTypeEnum;
import com.fernandocanabarro.booking_app_backend.services.exceptions.BadRequestException;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public class BookingsExcelExporter {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<BookingDetailResponseDTO> bookings;

    public BookingsExcelExporter(List<BookingDetailResponseDTO> bookings) {
        this.bookings = bookings;
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Bookings");
    }

    private void writeHeaderRow() {
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);
        List<String> headers = List.of("Id", "Check-In", "Check-Out", "Pagamento", "Quarto Id", "Diária", 
            "Nome do Hotel", "Valor Total", "Nome do Hóspede", "CPF do Hóspede");
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(style);
        }
    }

    private void writeDataRows() {
        int rowCount = 1;
        for (BookingDetailResponseDTO booking : bookings) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(booking.getId());
            sheet.autoSizeColumn(0);
            row.createCell(1).setCellValue(booking.getCheckIn().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            sheet.autoSizeColumn(1);
            row.createCell(2).setCellValue(booking.getCheckOut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            sheet.autoSizeColumn(2);
            row.createCell(3).setCellValue(PaymentTypeEnum.fromValue(booking.getPayment().getPaymentType()).toString());
            sheet.autoSizeColumn(3);
            row.createCell(4).setCellValue(booking.getRoom().getId());
            sheet.autoSizeColumn(4);
            row.createCell(5).setCellValue(String.valueOf(booking.getRoom().getPricePerNight()).replace(".", ","));
            sheet.autoSizeColumn(5);
            row.createCell(6).setCellValue(booking.getRoom().getHotelName());
            sheet.autoSizeColumn(6);
            row.createCell(7).setCellValue(String.valueOf(booking.getTotalPrice()).replace(".", ","));
            sheet.autoSizeColumn(7);
            row.createCell(8).setCellValue(booking.getUser().getFullName());
            sheet.autoSizeColumn(8);
            row.createCell(9).setCellValue(booking.getUser().getCpf());
            sheet.autoSizeColumn(9);
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
            throw new BadRequestException("Error exporting bookings to Excel");
        }
    }

}
