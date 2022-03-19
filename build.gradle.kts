plugins {
    `java-library`
    scala
    `maven-publish`
}

group = "com.github.yjgbg"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}
val springVersion = "2.6.4"
val vertxVersion = "4.2.6"
dependencies {
    api("io.circe:circe-parser_3:0.15.0-M1")
    api("io.circe:circe-generic_3:0.15.0-M1")
    api("org.springframework.boot:spring-boot-starter:${springVersion}")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:${springVersion}")
    api("com.github.rssh:dotty-cps-async_3:0.9.8") // async和 await支持
    api("io.vertx:vertx-redis-client:${vertxVersion}")
    api("io.vertx:vertx-web-client:${vertxVersion}")
    api("io.vertx:vertx-web:${vertxVersion}")
    api("ch.qos.logback:logback-classic:1.2.11")
    api("com.typesafe.scala-logging:scala-logging_3:3.9.4")
    api("org.scala-lang:scala3-library_3:3.1.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

java {
    withSourcesJar()
}

publishing {
    publications.create<MavenPublication>("this") {
        from(components["java"])
    }
    repositories.maven("https://oss.sonatype.org/content/repositories/snapshots") {
        credentials {
            username = project.ext["mavenUsername"].toString()
            password = project.ext["mavenPassword"].toString()
        }
    }
}
configurations{
    all {
        exclude("com.fasterxml.jackson.core")
    }
}