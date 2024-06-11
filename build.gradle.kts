val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.0.0"
    application
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
}

group = "xyz.potatoez"
version = "0.0.1"

application {
    mainClass.set("xyz.potatoez.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-apache-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-cors-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-caching-headers-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("org.postgresql:postgresql:42.5.1")
    implementation("com.h2database:h2:2.1.214")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-client-cio-jvm:3.0.0-beta-1")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    runtimeOnly("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.github.smiley4:ktor-swagger-ui:2.10.0")

    // mongodb server side
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:4.10.1")

    //Koin Dependency Injection
    implementation("io.insert-koin:koin-ktor:3.6.0-Beta4")
    implementation("io.insert-koin:koin-logger-slf4j:3.6.0-Beta4")

    implementation("org.mindrot:jbcrypt:0.4")
}
