package com.suktha.services.exportToExcel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ExportToExcelServiceImple implements ExportToExcelService {

    @Override
    public byte[] generateExcel(List<Map<String, Object>> tasks) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Tasks");

            // Create Header Row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Employee ID", "Title", "Priority", "Due Date", "Task Status", "Employee Name", "description"};

            CellStyle headerStyle = createHeaderStyle(workbook);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Populate Data Rows
            int rowNum = 1;
            for (Map<String, Object> task : tasks) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(getStringValue(task.get("id")));
                row.createCell(1).setCellValue(getStringValue(task.get("employeeId")));
                row.createCell(2).setCellValue(getStringValue(task.get("title")));
                row.createCell(3).setCellValue(getStringValue(task.get("priority")));
                row.createCell(4).setCellValue(getStringValue(task.get("dueDate")));
                row.createCell(5).setCellValue(getStringValue(task.get("taskStatus")));
                row.createCell(6).setCellValue(getStringValue(task.get("employeeName")));
                row.createCell(7).setCellValue(getStringValue(task.get("description")));
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    // Helper Method: Convert Object to String safely (handles null values)
    private String getStringValue(Object value) {
        return Optional.ofNullable(value).map(Object::toString).orElse("");
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        return headerStyle;
    }
}


