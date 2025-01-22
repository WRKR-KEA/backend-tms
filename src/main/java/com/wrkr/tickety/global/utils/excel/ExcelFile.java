package com.wrkr.tickety.global.utils.excel;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;

public class ExcelFile<T> {

  private static final SpreadsheetVersion supplyExcelVersion = SpreadsheetVersion.EXCEL2007;
  private static final int ROW_START_INDEX = 0;
  private static final int COLUMN_START_INDEX = 0;

  private SXSSFWorkbook wb;
  private Sheet sheet;
  private ExcelMetaData<T> excelMetaData;

  public ExcelFile(List<T> data, Class<T> type) {
    validateMaxRow(data);
    this.wb = new SXSSFWorkbook();
    this.excelMetaData = new ExcelMetaData<>(type);
    renderExcel(data);
  }

  private void validateMaxRow(List<T> data) {
    int maxRows = supplyExcelVersion.getMaxRows();
    if (data.size() > maxRows) {
      throw new IllegalArgumentException(
              String.format("This concrete ExcelFile does not support over %s rows", maxRows));
    }
  }

  private void renderExcel(List<T> data) {
    sheet = wb.createSheet();
    renderHeadersWithNewSheet(sheet, ROW_START_INDEX, COLUMN_START_INDEX);

    if (data.isEmpty()) {
      return;
    }

    // Render Body
    int rowIndex = ROW_START_INDEX + 1;
    for (T renderedData : data) { // 제네릭 타입 T 사용
      renderBody(renderedData, rowIndex++, COLUMN_START_INDEX);
    }
  }

  private void renderHeaders(Sheet sheet, int rowIndex, int columnStartIndex) {
    Row row = sheet.createRow(rowIndex);
    int columnIndex = columnStartIndex;
    for (String dataFieldName : excelMetaData.getDataFieldNames()) {
      Cell cell = row.createCell(columnIndex++);
      cell.setCellValue(excelMetaData.getExcelHeaderName(dataFieldName));
    }
  }

  private void renderBody(T data, int rowIndex, int columnStartIndex) {
    Row row = sheet.createRow(rowIndex);
    int columnIndex = columnStartIndex;
    for (String dataFieldName : excelMetaData.getDataFieldNames()) {
      Cell cell = row.createCell(columnIndex++);
      try {
        Field field = getField(data.getClass(), dataFieldName);
        field.setAccessible(true);
        renderCellValue(cell, field.get(data));
      } catch (Exception e) {
        throw new ExcelInternalException(e.getMessage(), e);
      }
    }
  }

  private void renderCellValue(Cell cell, Object cellValue) {
    if (cellValue instanceof Number) {
      Number numberValue = (Number) cellValue;
      cell.setCellValue(numberValue.doubleValue());
      return;
    }
    cell.setCellValue(cellValue == null ? "" : cellValue.toString());
  }

  public void write(OutputStream stream) throws IOException {
    wb.write(stream);
    wb.close();
    wb.dispose();
    stream.close();
  }

  private void renderHeadersWithNewSheet(Sheet sheet, int rowIndex, int columnStartIndex) {
    renderHeaders(sheet, rowIndex, columnStartIndex);
  }

  private Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
    while (clazz != null) {
      try {
        return clazz.getDeclaredField(fieldName);
      } catch (NoSuchFieldException e) {
        clazz = clazz.getSuperclass();
      }
    }
    throw new NoSuchFieldException("Field " + fieldName + " not found in class " + clazz);
  }
}
