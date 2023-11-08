plugins {
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
    kotlin("jvm")
    kotlin("plugin.noarg") version "1.9.0"
    kotlin("plugin.spring") version "1.9.0"
    kotlin("plugin.allopen") version "1.9.0"
}

dependencies {
    implementation(project(":internal-api"))
    implementation(project(":listening-platform:core"))
    implementation(project(":listening-platform:user"))
    implementation(project(":listening-platform:publisher"))
    implementation(project(":listening-platform:post"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive:3.1.3")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive:3.1.2")

    implementation("org.springframework.kafka:spring-kafka:3.0.12")
    implementation("io.nats:jnats:2.16.14")
    implementation("io.projectreactor.kafka:reactor-kafka:1.3.21")
    implementation("org.apache.kafka:kafka-clients:3.6.0")
    implementation("io.confluent:kafka-protobuf-serializer:7.5.1")

    implementation("net.devh:grpc-spring-boot-starter:2.15.0.RELEASE")
    implementation("net.devh:grpc-server-spring-boot-starter:2.15.0.RELEASE")

    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
    implementation("io.projectreactor:reactor-core:3.5.10")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.google.protobuf:protobuf-java:3.24.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.27.0")
    implementation("com.willowtreeapps.assertk:assertk:0.27.0")
    implementation("net.datafaker:datafaker:2.0.1")
}

subprojects {

    apply(plugin = "kotlin")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.plugin.noarg")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.allopen")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    dependencies {

        implementation(project(":"))
        implementation(project(":internal-api"))

        implementation("org.springframework.boot:spring-boot-starter-webflux")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive:3.1.3")
        implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive:3.1.2")

        implementation("org.springframework.kafka:spring-kafka:3.0.12")
        implementation("io.nats:jnats:2.16.14")
        implementation("io.projectreactor.kafka:reactor-kafka:1.3.21")
        implementation("org.apache.kafka:kafka-clients:3.6.0")
        implementation("io.confluent:kafka-protobuf-serializer:7.5.1")

        implementation("net.devh:grpc-spring-boot-starter:2.15.0.RELEASE")
        implementation("net.devh:grpc-server-spring-boot-starter:2.15.0.RELEASE")

        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
        implementation("io.projectreactor:reactor-core:3.5.10")
        implementation("org.jetbrains.kotlin:kotlin-reflect")

        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("com.google.protobuf:protobuf-java:3.24.3")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.27.0")
        implementation("com.willowtreeapps.assertk:assertk:0.27.0")
        implementation("net.datafaker:datafaker:2.0.1")
    }

    tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
        enabled = false
    }

    noArg {
        annotation("com.nikitahohulia.listeningplatform.core.infrastructure.annotation.LogOnException")
    }

    tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun> {
        enabled = false
    }
}

noArg {
    annotation("com.nikitahohulia.listeningplatform.core.infrastructure.annotation.LogOnException")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging.showStandardStreams = true
}
