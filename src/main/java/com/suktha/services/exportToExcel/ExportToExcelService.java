package com.suktha.services.exportToExcel;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public interface ExportToExcelService {

    // Method to generate Excel file as byte array
    byte[] generateExcel(List<Map<String, Object>> tasks) throws IOException;
}
