package com.wrkr.tickety.global.utils.converter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.core.convert.converter.Converter;

public class CellToBooleanConverter implements Converter<Cell, Boolean> {

    @Override
    public Boolean convert(Cell source) {
        CellType cellType = source.getCellType().equals(CellType.FORMULA) ?
            source.getCachedFormulaResultType() : source.getCellType();

        if (cellType.equals(CellType.BOOLEAN)) {
            return source.getBooleanCellValue();
        } else if (cellType.equals(CellType.STRING)) {
            return Boolean.valueOf(source.getStringCellValue());
        } else {
            throw new IllegalArgumentException(
                String.format("cell-to-boolean converter does not support cell type : %s", cellType)
            );
        }
    }
}
