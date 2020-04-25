plugins {
	java
	groovy
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
	
	compile("org.codehaus.groovy", "groovy-all", "2.4.8")
	testCompile("org.spockframework", "spock-core", "1.1-groovy-2.4-rc-2")
}

configure<JavaPluginConvention> {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}