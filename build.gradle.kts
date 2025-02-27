val kotlin_version = "2.1.0"
var ktor_version = "3.0.3"
var logback_version = "1.4.14"

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("io.ktor.plugin") version "3.0.3"
}

group = "com.polls_example"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-auth-jwt")
    implementation("io.ktor:ktor-server-netty")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-config-yaml")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")
    implementation("io.ktor:ktor-server-websockets:$ktor_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
//    testImplementation("io.ktor:ktor-server-tests:$ktor_version")

    implementation("com.yandex.scout:scout-core:0.9.3")
    implementation("org.postgresql:postgresql:42.3.1")
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.41.1")


    implementation("org.apache.commons:commons-email:1.5")
    implementation("redis.clients:jedis:5.2.0")
    implementation("org.springframework.security:spring-security-core:5.5.0")


    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("io.ktor:ktor-websockets:$ktor_version")
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testImplementation("com.h2database:h2:2.1.214")
    testImplementation("io.mockk:mockk:1.12.0")
    testImplementation("io.ktor:ktor-client-mock:$ktor_version")
    testImplementation("org.mockito:mockito-core:3.12.4") // Основной Mockito модуль
    testImplementation("org.mockito:mockito-inline:3.12.4") // Для работы с корутинными тестами
    testImplementation("io.ktor:ktor-server-tests:1.6.5") // для тестирования Ktor

    implementation("javax.mail:javax.mail-api:1.6.2")
    implementation("com.sun.mail:javax.mail:1.6.2")

}
