import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
	java
	id("org.springframework.boot") version "4.0.1"
	id("io.spring.dependency-management") version "1.1.7"
    // id "org.springdoc.openapi-gradle-plugin" version "1.3.4"
    id("org.openapi.generator") version "7.18.0"
}

group = "com.florianthom"
version = "0.0.1-SNAPSHOT"
description = "Spring Boot showcase project providing end-to-end system visibility using Grafana UI, Prometheus, and Grafana Alloy. It demonstrates real-time logs, monitoring, alerting and tracing including telemetry."


java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

sourceSets {
    main {
        java.srcDir("$rootDir/api-petstore/generatedJavaClient/src/main/java")
    }
}

repositories {
	mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")


    implementation("org.springframework.boot:spring-boot-starter-actuator:4.0.1")
    implementation("io.micrometer:micrometer-registry-prometheus:1.16.1")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    modules {
        module("org.springframework.boot:spring-boot-starter-logging") {
            replacedBy("org.springframework.boot:spring-boot-starter-log4j2", "Use Log4j2 instead of Logback")
        }
    }
    implementation("co.elastic.logging:log4j2-ecs-layout:1.7.0")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.openapitools:jackson-databind-nullable:0.2.7")
    implementation("org.springdoc:springdoc-openapi-ui:1.8.0")
    implementation("io.netty:netty-codec-http:4.2.9.Final")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.named("compileJava") {
    dependsOn("generatePetstoreClient")
}

tasks.register("generatePetstoreClient", GenerateTask::class){
    generatorName.set("java")
    library.set("webclient") // webclient
    generateModelTests.set(false)
    generateApiTests.set(false)
    generateModelDocumentation.set(false)
    //remoteInputSpec.set("")
    inputSpec.set("$rootDir/api-petstore/petstore-openapi.yaml")
    outputDir.set("$rootDir/api-petstore/generatedJavaClient")
    apiPackage.set("com.florianthom.petstore.javaClient.api")
    invokerPackage.set("com.florianthom.petstore.javaClient.invoker")
    modelPackage.set("com.florianthom.petstore.javaClient.model")
    modelNameSuffix.set("Dto")
    configOptions.put("dateLibrary", "java8")
    configOptions.put("interfaceOnly", "true")
    configOptions.put("useTags", "true")
    configOptions.put("useJakartaEe", "true")
    configOptions.put("openApiNullable", "false")
    configOptions.put("useEnumCaseInsensitive", "true")
    configOptions.put("useSpringBoot3", "true")
}
