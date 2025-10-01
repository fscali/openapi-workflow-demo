package com.company.validator;

import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.util.Map;

public class PolicyCatalog {
    private final Map<String, Map<String, Object>> policies;

    private PolicyCatalog(Map<String, Map<String, Object>> policies) {
        this.policies = policies;
    }

    public static PolicyCatalog load(String path) throws Exception {
        Yaml yaml = new Yaml();
        try (FileInputStream fis = new FileInputStream(path)) {
            Map<String, Object> raw = yaml.load(fis);
            return new PolicyCatalog((Map<String, Map<String, Object>>) raw.get("policies"));
        }
    }

    public String getSchema(String policyName) {
        if (!policies.containsKey(policyName)) {
            throw new RuntimeException("❌ Unknown policy: " + policyName);
        }
        return (String) policies.get(policyName).get("configSchema");
    }
}
