package com.wrkr.tickety.global.utils.converter;

import jakarta.annotation.PostConstruct;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Component;

@Component
public class ConverterUtil {

    private DefaultConversionService conversionService;

    @PostConstruct
    public void registerConverters() {
        this.conversionService = new DefaultConversionService();

        /* 커스텀 컨버터 등록 */
        conversionService.addConverterFactory(new CellToEnumConverterFactory());
        conversionService.addConverter(new CellToBooleanConverter());
        conversionService.addConverter(new CellToIntegerConverter());
        conversionService.addConverter(new CellToStringConverter());
    }

    public <T> T convert(Object source, Class<T> clazz) {
        return this.conversionService.convert(source, clazz);
    }
}
