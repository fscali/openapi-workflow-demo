package com.company.validator;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

public class PolicyValidator {

    public static void validatePolicies(String openApiPath, PolicyCatalog catalog) throws Exception {
        Yaml yaml = new Yaml();
        try (FileInputStream fis = new FileInputStream(openApiPath)) {
            Map<String, Object> spec = yaml.load(fis);

            Map<String, Object> paths = (Map<String, Object>) spec.get("paths");
            for (Map.Entry<String, Object> pathEntry : paths.entrySet()) {
                Map<String, Object> methods = (Map<String, Object>) pathEntry.getValue();
                for (Map.Entry<String, Object> methodEntry : methods.entrySet()) {
                    Map<String, Object> operation = (Map<String, Object>) methodEntry.getValue();
                    if (operation.containsKey("x-policies")) {
                        List<Object> policies = (List<Object>) operation.get("x-policies");
                        for (Object policyObj : policies) {
                            Map<String, Object> policy;

                            if (policyObj instanceof Map && ((Map<?, ?>) policyObj).containsKey("$ref")) {
                                String refPath = (String) ((Map<?, ?>) policyObj).get("$ref");
                                policy = loadExternalPolicy(openApiPath, refPath);
                            } else {
                                policy = (Map<String, Object>) policyObj;
                            }

                            validatePolicy(policy, catalog, pathEntry.getKey());
                        }
                    }
                }
            }
        }
    }

    private static Map<String, Object> loadExternalPolicy(String baseFile, String refPath) throws Exception {
        File base = new File(baseFile).getParentFile();
        File refFile = new File(base, refPath).getCanonicalFile();
        Yaml yaml = new Yaml();
        try (FileInputStream fis = new FileInputStream(refFile)) {
            return yaml.load(fis);
        }
    }

    private static void validatePolicy(Map<String, Object> policy, PolicyCatalog catalog, String apiPath) {
        String name = (String) policy.get("name");
        if (name == null) {
            throw new RuntimeException("❌ Policy missing 'name' field in " + apiPath);
        }

        String schemaString = catalog.getSchema(name);
        JSONObject schemaJson = new JSONObject(schemaString);
        Schema schema = SchemaLoader.load(schemaJson);

        Map<String, Object> configMap = (Map<String, Object>) policy.get("config");
        if (configMap == null) {
            throw new RuntimeException("❌ Policy " + name + " has no config in " + apiPath);
        }

        JSONObject config = new JSONObject(configMap);
        schema.validate(config);

        System.out.println("✅ Policy " + name + " is valid on " + apiPath);
    }
}
