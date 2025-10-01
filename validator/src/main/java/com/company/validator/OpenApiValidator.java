package com.company.validator;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

public class OpenApiValidator {
    public static OpenAPI validate(String filePath) {
        SwaggerParseResult result = new OpenAPIV3Parser().readLocation(filePath, null, null);
        if (!result.getMessages().isEmpty()) {
            throw new RuntimeException("❌ OpenAPI validation failed: " + result.getMessages());
        }
        return result.getOpenAPI();
    }
}
