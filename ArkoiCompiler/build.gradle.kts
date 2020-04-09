import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	java
	kotlin("jvm") version "1.3.71"
}

group = "com.arkoisystems"
version = "2.0.0-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	compile("org.junit.jupiter", "junit-jupiter", "5.4.2")
	
	compile("org.jetbrains", "annotations", "19.0.0")
	annotationProcessor("org.projectlombok", "lombok", "1.18.8")
	compileOnly("org.projectlombok", "lombok", "1.18.8")
	
	compile("commons-cli", "commons-cli", "1.4")
	
	implementation(kotlin("stdlib-jdk8"))
}

configure<JavaPluginConvention> {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
	jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
	jvmTarget = "1.8"
}