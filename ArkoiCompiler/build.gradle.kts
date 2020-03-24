plugins {
	java
}

group = "com.arkoisystems"
version = "2.0.0-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	compile("org.junit.jupiter", "junit-jupiter", "5.4.2")
	
	annotationProcessor("org.projectlombok", "lombok", "1.18.8")
	compileOnly("org.projectlombok", "lombok", "1.18.8")
	
	compile("commons-cli", "commons-cli", "1.4")
	compile("org.jetbrains", "annotations", "19.0.0")
}

configure<JavaPluginConvention> {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}