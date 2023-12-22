import java.text.SimpleDateFormat

plugins {
    id("java-library")
    id("maven-publish")
    id("com.github.johnrengelman.shadow").version("7.1.2")
}

repositories {
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")
    maven("http://repo.crypticlib.com:8081/repository/maven-public/") {
        isAllowInsecureProtocol = true
    }
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://repo.md-5.net/content/groups/public/")
    maven( "https://repo.dmulloy2.net/repository/public/" )
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("org.spigotmc:spigot-api:1.15.2-R0.1-SNAPSHOT")
    compileOnly("io.github.dreamvoid:MiraiMC-Integration:1.8")
    implementation("com.crypticlib:common:0.10.0")
}

group = "com.github.yufiriamazenta"
version = "1.7.2"
var mainClass = "${rootProject.group}.${rootProject.name.lowercase()}.Whitelist4QQ"
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
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    shadowJar {
        relocate("crypticlib", "${rootProject.group}.${rootProject.name.lowercase()}.crypticlib")
        archiveFileName.set("${rootProject.name}-${version}.jar")
    }
    assemble {
        dependsOn(shadowJar)
    }
}
