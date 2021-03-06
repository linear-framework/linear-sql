plugins {
  `java-library`
  scala
  `maven-publish`
}

group = "com.linearframework"

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
  jcenter()
  mavenCentral()
}

dependencies {
  implementation("org.scala-lang:scala-library:2.13.2")

  testImplementation("junit:junit:4.13")
  testImplementation("org.scalatest:scalatest_2.13:3.1.2")
  testImplementation("org.scalatestplus:junit-4-12_2.13:3.1.2.0")
  testImplementation("org.scalatestplus:mockito-3-2_2.13:3.1.2.0")
  testImplementation("org.mockito:mockito-core:3.3.3")
  testImplementation("org.apache.commons:commons-dbcp2:2.7.0")

  testRuntimeOnly("org.mariadb.jdbc:mariadb-java-client:2.6.0")
  testRuntimeOnly("org.postgresql:postgresql:42.2.12")
  testRuntimeOnly("com.h2database:h2:1.4.200")
}

tasks.named<Jar>("jar") {
  from(sourceSets["main"].output)
  from(sourceSets["main"].allSource)
}

publishing {
  repositories {
    maven {
      name = "LinearSql"
      url = uri("https://maven.pkg.github.com/linear-framework/linear-sql")
      credentials {
        username = System.getenv("GITHUB_USER")
        password = System.getenv("GITHUB_TOKEN")
      }
    }
  }
  publications {
    create<MavenPublication>("PublishToGithub") {
      artifactId = "sql"
      from(components["java"])
    }
  }
}