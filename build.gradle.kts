plugins {
    val kotlinVersion = "1.7.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.12.3"
}

group = "cn.sakuraex"
version = "0.3.1"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}
