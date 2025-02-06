package com.wrkr.tickety.global.utils.converter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

public class CellToEnumConverterFactory implements ConverterFactory<Cell, Enum> {

    @Override
    public <T extends Enum> Converter<Cell, T> getConverter(Class<T> targetType) {
        return new CellToEnum<>(targetType);
    }

    private static class CellToEnum<T extends Enum> implements Converter<Cell, T> {

        private final Class<T> enumType;

        CellToEnum(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(Cell source) {
            CellType cellType = source.getCellType();

            if (cellType.equals(CellType.STRING)) {
                return (T) Enum.valueOf(enumType, source.getStringCellValue());
            } else {
                throw new IllegalArgumentException(
                    String.format("Not supported cell type : %s", cellType)
                );
            }
        }
    }
}
