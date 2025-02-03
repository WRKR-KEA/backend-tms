package com.wrkr.tickety.global.utils.excel;

import static com.wrkr.tickety.global.response.code.CommonErrorCode.INTERNAL_SERVER_ERROR;

import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.DepartmentTicketResponse;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.converter.ConverterUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class ExcelUtil {

    private static final int headerStartRowToParse = 0; // 헤더가 시작하는 행 인덱스
    private static final int headerStartRowToRender = 0; // 헤더가 시작하는 행 인덱스 (렌더링용)
    private static final int bodyStartRowToParse = 1; // 본문 데이터가 시작하는 행 인덱스 (파싱용)
    private static final int bodyStartRowToRender = 1; // 본문 데이터가 시작하는 행 인덱스
    private static final int startColToRender = 0; // 데이터가 시작하는 열 인덱스

    private final ConverterUtil converterUtil;

    public <T> List<T> parseExcelToObject(MultipartFile file, Class<T> clazz) {
        /* read workbook & sheet */
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(file.getInputStream());
        } catch (IOException e) {
            throw new ApplicationException(INTERNAL_SERVER_ERROR);
        }

        Sheet sheet = workbook.getSheetAt(0);

        /* parse header & body */
        Map<String, Integer> headers = parseHeader(sheet, clazz);
        return parseBody(headers, sheet, clazz); // 파라미터 채워줘
    }

    private <T> Map<String, Integer> parseHeader(Sheet sheet, Class<T> clazz) {
        Map<String, Integer> excelHeaders = new HashMap<>();
        Set<String> classHeaders = new HashSet<>();

        /* 엑셀 헤더를 Map으로 저장 */
        sheet.getRow(headerStartRowToParse).cellIterator()
            .forEachRemaining(e -> excelHeaders.put(e.getStringCellValue(), e.getColumnIndex()));

        Arrays.stream(clazz.getDeclaredFields())
            .filter(e -> e.isAnnotationPresent(ExcelColumn.class))
            .forEach(e -> {
                if (e.getAnnotation(ExcelColumn.class).headerName().equals("")) {
                    classHeaders.add(e.getName());
                } else {
                    classHeaders.add(e.getAnnotation(ExcelColumn.class).headerName());
                }
            });

        if (!excelHeaders.keySet().containsAll(classHeaders)) {
            throw new ApplicationException(INTERNAL_SERVER_ERROR);
        }

        return excelHeaders;
    }


    private <T> List<T> parseBody(Map<String, Integer> headers, Sheet sheet, Class<T> clazz) {
        List<T> objects = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        try {
            clazz.getDeclaredConstructor().setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new ApplicationException(INTERNAL_SERVER_ERROR);
        }

        /* 엑셀의 줄마다 매핑시킬 객체를 만들고 */
        for (int i = bodyStartRowToParse; i <= sheet.getLastRowNum(); i++) {
            try {
                T object = clazz.getDeclaredConstructor().newInstance(); // 객체 생성

                /* 객체의 필드를 순회하며 값을 넣음 */
                for (Field field : fields) {
                    field.setAccessible(true);

                    /* @ExcelColumn에 기입된 헤더명을 Key로 사용하면 */
                    String key = field.getAnnotation(ExcelColumn.class).headerName().equals("") ?
                        field.getName() : field.getAnnotation(ExcelColumn.class).headerName();

                    /* Value에 해당하는 엑셀 인덱스의 값을 가져올 수 있음 */
                    Cell cell = sheet.getRow(i).getCell(headers.get(key));

                    /* Converter를 활용한 자동 매핑 로직 */
                    try {
                        field.set(object, converterUtil.convert(cell, field.getType())); // object에 매핑
                    } catch (IllegalAccessException e) {
                        throw new ApplicationException(INTERNAL_SERVER_ERROR);
                    }
                }

                /* 완성된 객체를 리스트에 쌓기 */
                objects.add(object); // object 추가
            } catch (Exception e) {
                throw new ApplicationException(INTERNAL_SERVER_ERROR);
            }
        }
        return objects;
    }

    public <T> void renderObjectToExcel(HttpServletResponse response, List<T> data, Class<T> clazz, String filename) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xlsx\"");

            /* create workbook & sheet */
            Workbook workbook = WorkbookFactory.create(true);
            Sheet sheet = workbook.createSheet();

            /* render header & body */
            renderHeader(sheet, clazz);
            renderBody(sheet, data, clazz);

            /* close stream */
            workbook.write(response.getOutputStream());
            workbook.close();
            workbook.close();

        } catch (IOException e) {
            throw new ApplicationException(INTERNAL_SERVER_ERROR);
        }
    }

    private <T> void renderHeader(Sheet sheet, Class<T> clazz) {
        Row row = sheet.createRow(headerStartRowToRender);
        int colIdx = startColToRender;

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ExcelColumn.class)) {
                String headerName = field.getAnnotation(ExcelColumn.class).headerName();

                row.createCell(colIdx, CellType.STRING).setCellValue(
                    headerName.equals("") ? field.getName() : headerName
                );

                colIdx++;
            }
        }

        if (colIdx == startColToRender) {
            throw new ApplicationException(INTERNAL_SERVER_ERROR);
        }
    }

    private <T> void renderBody(Sheet sheet, List<T> data, Class<T> clazz) {
        int rowIdx = bodyStartRowToRender;

        for (T datum : data) {
            Row row = sheet.createRow(rowIdx);
            int colIdx = startColToRender;

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                try {
                    row.createCell(colIdx, CellType.STRING).setCellValue(String.valueOf(field.get(datum)));
                } catch (IllegalAccessException e) {
                    throw new ApplicationException(INTERNAL_SERVER_ERROR);
                }

                colIdx++;
            }

            rowIdx++;
        }
    }

    public void parseTicketDataToExcelGroupByStatus(HttpServletResponse response, List<DepartmentTicketResponse> ticketDataList, String filename) {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xlsx\"");

        try (Workbook workbook = WorkbookFactory.create(true)) {
            for (TicketStatus status : TicketStatus.values()) {
                List<DepartmentTicketResponse> filteredData = ticketDataList.stream()
                    .filter(ticket -> ticket.status() == status)
                    .collect(Collectors.toList());

                if (!filteredData.isEmpty()) {
                    Sheet sheet = workbook.createSheet(status.getDescription());
                    renderHeader(sheet, DepartmentTicketResponse.class);
                    renderBody(sheet, filteredData, DepartmentTicketResponse.class);
                }
            }

            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new ApplicationException(INTERNAL_SERVER_ERROR);
        }
    }
}
