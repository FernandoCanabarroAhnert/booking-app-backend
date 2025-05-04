package com.fernandocanabarro.booking_app_backend.services.excel;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.UserResponseDTO;
import com.fernandocanabarro.booking_app_backend.services.exceptions.BadRequestException;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public class UsersExcelExporter {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<UserResponseDTO> users;

    public UsersExcelExporter(List<UserResponseDTO> users) {
        this.users = users;
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Users");
    }

    private void writeHeaderRow() {
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);
        List<String> headers = List.of("Id", "Nome", "Email", "Telefone", "CPF", "Data de Nascimento", "Criado em", "Funções", "Hotel Id");
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(style);
        }
    }

    private void writeDataRows() {
        int rowCount = 1;
        for (UserResponseDTO user : users) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(user.getId());
            sheet.autoSizeColumn(0);
            row.createCell(1).setCellValue(user.getFullName());
            sheet.autoSizeColumn(1);
            row.createCell(2).setCellValue(user.getEmail());
            sheet.autoSizeColumn(2);
            row.createCell(3).setCellValue(user.getPhone());
            sheet.autoSizeColumn(3);
            row.createCell(4).setCellValue(user.getCpf());
            sheet.autoSizeColumn(4);
            row.createCell(5).setCellValue(user.getBirthDate().toString());
            sheet.autoSizeColumn(5);
            row.createCell(6).setCellValue(user.getCreatedAt().toString());
            sheet.autoSizeColumn(6);
            row.createCell(7).setCellValue(user.getRoles().stream().map(role -> {
                return role.getAuthority().equals("ROLE_GUEST") ? "Hospede" : role.getAuthority().equals("ROLE_OPERATOR") ? "Funcionário" : "Administrador";
            }).collect(Collectors.joining(", ")));
            sheet.autoSizeColumn(7);
            row.createCell(8).setCellValue(user.getWorkingHotelId() != null ? user.getWorkingHotelId().toString() : "Sem hotel");
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
            throw new BadRequestException("Error exporting users to Excel");
        }
    }

}
