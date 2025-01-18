package com.wrkr.tickety.global.config.swagger;

import io.swagger.v3.oas.models.examples.Example;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SwaggerExampleHolder {

    private Example example;
    private String name;
    private int code;
}

