plugins {
    idea
    scala
    `java-library`
    `maven-publish`
    id("org.springframework.boot") version "2.6.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

group = "com.github.yjgbg"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}
val springVersion = "2.6.4"
val vertxVersion = "4.2.6"
val micrometerVersion = "1.8.4"
dependencies {
    api("org.springframework.boot:spring-boot-starter:${springVersion}")
    api("io.vertx:vertx-redis-client:${vertxVersion}")
    api("io.vertx:vertx-web-client:${vertxVersion}")
    api("io.vertx:vertx-web:${vertxVersion}")
    api("io.vertx:vertx-core:${vertxVersion}")
    api("io.vertx:vertx-mysql-client:$vertxVersion")
    api("io.vertx:vertx-micrometer-metrics:$vertxVersion")
    api("io.micrometer:micrometer-registry-influx:$micrometerVersion")
    api("io.micrometer:micrometer-registry-prometheus:$micrometerVersion")
    api("io.micrometer:micrometer-registry-jmx:$micrometerVersion")
    api("org.scala-lang:scala3-library_3:3.1.1")
    api("com.typesafe.scala-logging:scala-logging_3:3.9.4")
    api("com.github.rssh:dotty-cps-async_3:0.9.8") // async和 await支持
    api("io.circe:circe-parser_3:0.15.0-M1")// json反序列化
    api("io.circe:circe-generic_3:0.15.0-M1") // json序列化
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:${springVersion}")
}

configurations{
    all {
        exclude("com.fasterxml.jackson.core")
    }
}

java {
    withSourcesJar()
}

publishing {
    publications.create<MavenPublication>("this") {
        from(components["java"])
        pom {
            version = "${project.ext["publicationVersion"].toString()}-SNAPSHOT"
        }
    }
    repositories.maven("https://oss.sonatype.org/content/repositories/snapshots") {
        credentials {
            username = project.ext["mavenUsername"].toString()
            password = project.ext["mavenPassword"].toString()
        }
    }
}