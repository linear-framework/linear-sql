plugins {
  `java-library`
  scala
}

repositories {
  jcenter()
  mavenCentral()
}

dependencies {
  implementation("org.scala-lang:scala-library:2.13.1")

  testImplementation("junit:junit:4.13")
  testImplementation("org.scalatest:scalatest_2.13:3.1.1")
  testImplementation("org.scalatestplus:junit-4-12_2.13:3.1.1.0")
  testImplementation("org.scalatestplus:mockito-3-2_2.13:3.1.1.0")
  testImplementation("org.mockito:mockito-core:3.3.0")
  testImplementation("org.apache.commons:commons-dbcp2:2.7.0")

  testRuntimeOnly("org.mariadb.jdbc:mariadb-java-client:2.5.4")
  testRuntimeOnly("org.postgresql:postgresql:42.2.10")
  testRuntimeOnly("com.h2database:h2:1.4.200")
}

tasks.named<Jar>("jar") {
  from(sourceSets["main"].output)
  from(sourceSets["main"].allSource)
}