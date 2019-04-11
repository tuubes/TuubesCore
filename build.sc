import mill._, scalalib._
import coursier.maven.MavenRepository
import $ivy.`ch.epfl.scala::mill-bloop:1.2.5`

trait TuubesModule extends ScalaModule {
  def scalaVersion = "2.12.8"
  def repositories = super.repositories ++ Seq(
    MavenRepository("https://jcenter.bintray.com")
  )
}

trait JUnitTesting extends TestModule {// JUnit 5 tests
  def ivyDeps = Agg(ivy"net.aichler:jupiter-interface:0.8.1")
  def testFrameworks = Seq("net.aichler.jupiter.api.JupiterFramework")
}

object utils extends TuubesModule {
  object test extends Tests with JUnitTesting
}

object core extends TuubesModule {
  def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"com.electronwill::niol:1.5.4",
    ivy"com.electronwill.night-config:core:3.5.2",
    ivy"com.electronwill.night-config:json:3.5.2",
    ivy"com.electronwill.night-config:toml:3.5.2",
    ivy"com.github.pathikrit::better-files:3.7.1",
    ivy"org.fusesource.jansi:jansi:1.17.1",
    ivy"org.apache.logging.log4j:log4j-api:2.11.1",
    ivy"org.apache.logging.log4j:log4j-core:2.11.1",
    ivy"org.apache.logging.log4j::log4j-api-scala:11.0"
  )
  def moduleDeps = Seq(utils)

  object test extends Tests with JUnitTesting
}

object coreExamples extends TuubesModule {
  def moduleDeps = Seq(core)
}
