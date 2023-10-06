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
    implementation(project(":internal-api"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.nats:jnats:2.16.14")
    implementation("com.google.protobuf:protobuf-java:3.24.3")
    implementation("com.willowtreeapps.assertk:assertk:0.27.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("net.datafaker:datafaker:2.0.1")


}

noArg {
    annotation("com.nikitahohulia.listeningplatform.bpp.annotation.LogOnException")
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging.showStandardStreams = true
}
