package com.company.validator;

import java.io.File;
import java.nio.file.Path;

public class ValidatorMain {
  public static void main(String[] args) throws Exception {
    if (args.length == 0 || args[0].isBlank()) {
      System.err.println("❌ No YAML file provided. Use -Pfile=yamls/xxx.yaml");
      System.exit(1);
    }

    String yamlPath = args[0];
    Path path = Path.of(yamlPath).normalize();

    // Ensure file is inside yamls/
    // if (!path.startsWith(Path.of("yamls"))) {
    //   System.err.println("❌ File must be inside yamls/: " + yamlPath);
    //   System.exit(1);
    // }

    File yamlFile = path.toFile();
    if (!yamlFile.exists()) {
      System.err.println("❌ File not found: " + yamlFile);
      System.exit(1);
    }

    // String openApiPath = "../yamls/main.yaml";
    String openApiPath = path.toString();
    String catalogPath = "../yamls/policy-catalog.yaml";

    OpenApiValidator.validate(openApiPath);
    PolicyCatalog catalog = PolicyCatalog.load(catalogPath);
    PolicyValidator.validatePolicies(openApiPath, catalog);

    System.out.println("✅ Validation passed: OpenAPI and policies are valid");
  }
}
