plugins {
    id("java")
    application
}
group = "be.ugent"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.javatuples:javatuples:1.2")
    implementation("org.apache.logging.log4j:log4j-api:2.22.1")
    implementation("org.apache.logging.log4j:log4j-core:2.22.1")
    implementation("commons-cli:commons-cli:1.6.0")
}

tasks.test {
    useJUnitPlatform()
}


application {
    mainClass.set("be.ugent.Benchmark") // The main class of the application
}