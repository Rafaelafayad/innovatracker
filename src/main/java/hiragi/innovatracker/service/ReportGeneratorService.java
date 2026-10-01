package hiragi.innovatracker.service;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;
import java.io.IOException;


@Service
public class ReportGeneratorService {


    public byte[] generateSampleExcelReport() throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Status InnovaTracker");


            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Métrica");
            header.createCell(1).setCellValue("Valor");


            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("Virtual Threads Ativas");
            row1.createCell(1).setCellValue("Sim");


            Row row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue("Status do Banco de Dados");
            row2.createCell(1).setCellValue("Conectado (PostgreSQL)");


            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);


            workbook.write(out);
            return out.toByteArray();
        }
    }
}
