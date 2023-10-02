plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("io.gitlab.arturbosch.detekt")
    kotlin("plugin.noarg") version "1.9.0"
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.allopen")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.nats:jnats:2.16.14")
    implementation("com.google.protobuf:protobuf-java:3.24.3")
    implementation(project(":nats"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

noArg {
    annotation("com.nikitahohulia.listeningplatform.bpp.annotation.LogOnException")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
