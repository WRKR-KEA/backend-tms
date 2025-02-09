package com.wrkr.tickety.global.utils.converter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.core.convert.converter.Converter;

public class CellToStringConverter implements Converter<Cell, String> {

    @Override
    public String convert(Cell source) {
        CellType cellType = source.getCellType().equals(CellType.FORMULA) ?
            source.getCachedFormulaResultType() : source.getCellType();

        if (cellType.equals(CellType.STRING)) {
            return source.getStringCellValue(); // String 값 반환
        } else if (cellType.equals(CellType.NUMERIC)) {
            return String.valueOf(source.getNumericCellValue());
        } else if (cellType.equals(CellType.BOOLEAN)) {
            return String.valueOf(source.getBooleanCellValue());
        } else {
            throw new IllegalArgumentException(
                String.format("cell-to-string converter does not support cell type : %s", cellType)
            );
        }
    }
}
