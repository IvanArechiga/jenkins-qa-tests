import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id ("io.qameta.allure") version "2.11.2"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    // Use Maven Central for resolving dependencies.
    mavenLocal()
    mavenCentral()
}



dependencies {
    testImplementation("com.googlecode.json-simple:json-simple:1.1.1")
    testImplementation("org.apache.commons:commons-lang3:3.12.0")
    testImplementation("org.testcontainers:testcontainers:1.17.2")
    implementation("commons-codec:commons-codec:1.15")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("com.github.fge:json-patch:1.9")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.vintage:junit-vintage-engine:5.10.2")

    testImplementation("io.qameta.allure:allure-junit5:2.33.0")

}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

allprojects {
    apply(plugin = "java-library")
    java {
        sourceCompatibility = JavaVersion.VERSION_21
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "21"
    }
}