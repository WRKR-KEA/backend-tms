package com.wrkr.tickety.global.config.swagger;

import com.wrkr.tickety.domains.auth.exception.AuthErrorCode;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.domains.notification.exception.NotificationErrorCode;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
import com.wrkr.tickety.domains.ticket.exception.CommentErrorCode;
import com.wrkr.tickety.domains.ticket.exception.GuideErrorCode;
import com.wrkr.tickety.domains.ticket.exception.StatisticsErrorCode;
import com.wrkr.tickety.domains.ticket.exception.TemplateErrorCode;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.response.ApplicationResponse;
import com.wrkr.tickety.global.response.code.BaseErrorCode;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.method.HandlerMethod;

@Configuration
public class SwaggerConfig {

    @Value("${swagger.server.url}")
    private String serverUrl;

    @Bean
    @Profile("local")
    public OpenAPI localOpenAPI() {
        return createOpenAPI(getLocalServer());
    }

    @Bean
    @Profile("dev")
    public OpenAPI devOpenAPI() {
        return createOpenAPI(getDevServer());
    }

    private OpenAPI createOpenAPI(Server server) {
        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .in(SecurityScheme.In.HEADER)
                    .name("Authorization"))
            )
            .servers(List.of(server))
            .info(new Info().title("Tickety TMS Swagger").version("1.0"))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    private Server getLocalServer() {
        return new Server()
            .url(serverUrl)
            .description("Local Server");
    }

    private Server getDevServer() {
        return new Server()
            .url(serverUrl)
            .description("Dev Server");
    }

    @Bean
    public OperationCustomizer customize() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            CustomErrorCodes customErrorCodes = handlerMethod.getMethodAnnotation(CustomErrorCodes.class);

            if (customErrorCodes != null) {
                generateErrorCodeResponse(
                    operation,
                    customErrorCodes.commonErrorCodes(),
                    customErrorCodes.memberErrorCodes(),
                    customErrorCodes.ticketErrorCodes(),
                    customErrorCodes.commentErrorCodes(),
                    customErrorCodes.categoryErrorCodes(),
                    customErrorCodes.templateErrorCodes(),
                    customErrorCodes.guideErrorCodes(),
                    customErrorCodes.statisticsErrorCodes(),
                    customErrorCodes.authErrorCodes(),
                    customErrorCodes.notificationErrorCodes()
                );
            }

            return operation;
        };
    }

    private void generateErrorCodeResponse(
        Operation operation,
        CommonErrorCode[] commonErrorCodes,
        MemberErrorCode[] memberErrorCodes,
        TicketErrorCode[] ticketErrorCodes,
        CommentErrorCode[] commentErrorCodes,
        CategoryErrorCode[] categoryErrorCodes,
        TemplateErrorCode[] templateErrorCodes,
        GuideErrorCode[] guideErrorCodes,
        StatisticsErrorCode[] statisticsErrorCodes,
        AuthErrorCode[] authErrorCodes,
        NotificationErrorCode[] notificationErrorCodes
    ) {
        ApiResponses responses = operation.getResponses();

        if (commonErrorCodes != null) {
            for (CommonErrorCode errorCode : commonErrorCodes) {
                SwaggerExampleHolder SwaggerExampleHolder = getSwaggerExampleHolder(errorCode);
                addExamplesToResponses(responses, SwaggerExampleHolder);
            }

            for (MemberErrorCode errorCode : memberErrorCodes) {
                SwaggerExampleHolder SwaggerExampleHolder = getSwaggerExampleHolder(errorCode);
                addExamplesToResponses(responses, SwaggerExampleHolder);
            }

            for (TicketErrorCode errorCode : ticketErrorCodes) {
                SwaggerExampleHolder SwaggerExampleHolder = getSwaggerExampleHolder(errorCode);
                addExamplesToResponses(responses, SwaggerExampleHolder);
            }

            for (CommentErrorCode errorCode : commentErrorCodes) {
                SwaggerExampleHolder SwaggerExampleHolder = getSwaggerExampleHolder(errorCode);
                addExamplesToResponses(responses, SwaggerExampleHolder);
            }

            for (CategoryErrorCode errorCode : categoryErrorCodes) {
                SwaggerExampleHolder SwaggerExampleHolder = getSwaggerExampleHolder(errorCode);
                addExamplesToResponses(responses, SwaggerExampleHolder);
            }

            for (TemplateErrorCode errorCode : templateErrorCodes) {
                SwaggerExampleHolder SwaggerExampleHolder = getSwaggerExampleHolder(errorCode);
                addExamplesToResponses(responses, SwaggerExampleHolder);
            }

            for (GuideErrorCode errorCode : guideErrorCodes) {
                SwaggerExampleHolder SwaggerExampleHolder = getSwaggerExampleHolder(errorCode);
                addExamplesToResponses(responses, SwaggerExampleHolder);
            }

            for (StatisticsErrorCode errorCode : statisticsErrorCodes) {
                SwaggerExampleHolder SwaggerExampleHolder = getSwaggerExampleHolder(errorCode);
                addExamplesToResponses(responses, SwaggerExampleHolder);
            }

            for (AuthErrorCode errorCode : authErrorCodes) {
                SwaggerExampleHolder SwaggerExampleHolder = getSwaggerExampleHolder(errorCode);
                addExamplesToResponses(responses, SwaggerExampleHolder);
            }

            for (NotificationErrorCode errorCode : notificationErrorCodes) {
                SwaggerExampleHolder SwaggerExampleHolder = getSwaggerExampleHolder(errorCode);
                addExamplesToResponses(responses, SwaggerExampleHolder);
            }
        }
    }

    private SwaggerExampleHolder getSwaggerExampleHolder(BaseErrorCode errorCode) {
        return SwaggerExampleHolder.builder()
            .example(getSwaggerExample(errorCode))
            .name(errorCode.getCustomCode())
            .code(errorCode.getHttpStatus().value())
            .build();
    }

    /**
     * {@code @ApiErrorCodes} annotation 이 존재할 경우 {@code ApiResponses}에 {@code Example}을 추가
     */
    private void addExamplesToResponses(ApiResponses responses, SwaggerExampleHolder SwaggerExampleHolder) {
        String responseKey = String.valueOf(SwaggerExampleHolder.getCode());
        ApiResponse apiResponse = responses.computeIfAbsent(responseKey, k -> new ApiResponse());

        Content content = apiResponse.getContent();
        if (content == null) {
            content = new Content();
            apiResponse.setContent(content);
        }

        MediaType mediaType = content.computeIfAbsent("application/json", k -> new MediaType());
        mediaType.addExamples(SwaggerExampleHolder.getName(), SwaggerExampleHolder.getExample());
        responses.addApiResponse(responseKey, apiResponse);
    }

    /**
     * {@code BaseErrorCode}를 통해 {@code Example}을 생성
     */
    private Example getSwaggerExample(BaseErrorCode errorCode) {
        ApplicationResponse<Void> response = ApplicationResponse.onFailure(errorCode.getCustomCode(), errorCode.getMessage(), null);
        Example example = new Example();
        example.setValue(response);

        return example;
    }
}
