import java.text.SimpleDateFormat

plugins {
    id("java-library")
    id("maven-publish")
    id("com.github.johnrengelman.shadow").version("7.1.2")
    id("net.kyori.blossom") version "1.3.1"
}

repositories {
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")
    maven("https://repo.crypticlib.com:8081/repository/maven-public/")
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://repo.md-5.net/content/groups/public/")
    maven( "https://repo.dmulloy2.net/repository/public/" )
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    compileOnly("io.github.dreamvoid:MiraiMC-Integration:1.8")
    compileOnly("com.electronwill.night-config:core:3.6.7")
    implementation("com.crypticlib:velocity:1.11.2")
}

group = "pers.yufiria"
version = "1.0.2"
var mainClass = "pers.yufiria.whitelist4qq.velocity.Whitelist4QQ"
var pluginVersion: String = version.toString() + "-" + SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis())
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks {
    val props = HashMap<String, String>()
    props["version"] = pluginVersion
    processResources {
        filesMatching("velocity-plugin.json") {
            expand(props)
        }
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    shadowJar {
        relocate("crypticlib", "pers.yufiria.whitelist4qq.velocity.crypticlib")
        archiveFileName.set("${rootProject.name}-${version}.jar")
    }
    assemble {
        dependsOn(shadowJar)
    }
}

blossom {
    replaceToken("{{id}}", rootProject.name.lowercase())
    replaceToken("{{name}}", rootProject.name)
    replaceToken("{{version}}", rootProject.version.toString())
    replaceTokenIn("src/main/java/pers/yufiria/whitelist4qq/velocity/Whitelist4QQ.java")
}