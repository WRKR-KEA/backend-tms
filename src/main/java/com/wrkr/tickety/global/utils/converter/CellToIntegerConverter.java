package com.wrkr.tickety.global.utils.converter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.core.convert.converter.Converter;

public class CellToIntegerConverter implements Converter<Cell, Integer> {

    @Override
    public Integer convert(Cell source) {
        CellType cellType = source.getCellType().equals(CellType.FORMULA) ?
            source.getCachedFormulaResultType() : source.getCellType();

        if (cellType.equals(CellType.NUMERIC)) {
            return (int) source.getNumericCellValue(); // Numeric 값이 정수로 변환
        } else if (cellType.equals(CellType.STRING)) {
            return Integer.valueOf(source.getStringCellValue());
        } else {
            throw new IllegalArgumentException(
                String.format("cell-to-integer converter does not support cell type : %s", cellType)
            );
        }
    }
}
