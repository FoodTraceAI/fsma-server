import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency)
    kotlin("kapt") version "1.9.23"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
//    gradlePluginPortal()  // What is this used for?
    mavenCentral()
}

dependencies {
    implementation(libs.apache.poi)         // to create xlsx file
    implementation(libs.apache.poi.ooxml)   // to create xlsx file
    implementation(libs.commons.csv)
    implementation(libs.jackson)
    implementation(libs.jsonwebtoken)
    implementation(libs.jsonwebtoken.impl)
    implementation(libs.jsonwebtoken.jackson)
    implementation(libs.kotlin.reflect)
    implementation(libs.postgresql.driver)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.mustache)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.security)
    developmentOnly(libs.spring.boot.devtools)
    implementation(libs.springdoc.common)
    implementation(libs.springdoc.webmvc)

    runtimeOnly(libs.h2database)
    testImplementation(libs.spring.boot.starter.test) {
        exclude(module = "mockito-core")
    }
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
