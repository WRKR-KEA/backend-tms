package com.wrkr.tickety.global.utils.excel;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ExcelMetaData<T> {

    private final Class<T> type;
    private final Map<String, String> fieldToHeaderMap = new HashMap<>();

    public ExcelMetaData(Class<T> type) {
        this.type = type;
        initializeFieldToHeaderMap();
    }

    private void initializeFieldToHeaderMap() {
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            ExcelColumn headerAnnotation = field.getAnnotation(ExcelColumn.class);
            if (headerAnnotation != null) {
                fieldToHeaderMap.put(field.getName(), headerAnnotation.headerName());
            } else {
                fieldToHeaderMap.put(field.getName(), field.getName()); // 주석이 없으면 필드 이름 사용
            }
        }
    }

    public String getExcelHeaderName(String fieldName) {
        return fieldToHeaderMap.getOrDefault(fieldName, fieldName);
    }

    public Iterable<String> getDataFieldNames() {
        return fieldToHeaderMap.keySet();
    }

    public Class<T> getType() {
        return type;
    }
}
