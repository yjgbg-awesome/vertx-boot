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
    api("io.circe:circe-parser_3:0.15.0-M1")// json反序列化
    api("io.circe:circe-generic_3:0.15.0-M1") // json序列化
    api("org.springframework.boot:spring-boot-starter:${springVersion}")
    api("com.github.rssh:dotty-cps-async_3:0.9.8") // async和 await支持
    api("io.vertx:vertx-redis-client:${vertxVersion}")
    api("io.vertx:vertx-web-client:${vertxVersion}")
    api("io.vertx:vertx-web:${vertxVersion}")
    api("com.typesafe.scala-logging:scala-logging_3:3.9.4")
    api("org.scala-lang:scala3-library_3:3.1.1")
}

publishing {
    publications.create<MavenPublication>("this") {
        from(components["java"])
    }
    repositories.maven("https://oss.sonatype.org/content/repositories/snapshots") {
        name = "centralSnapshot"
        credentials {
            username = project.ext["mavenUsername"].toString()
            password = project.ext["mavenPassword"].toString()
        }
    }
    repositories.mavenCentral() {
        name = "central"
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