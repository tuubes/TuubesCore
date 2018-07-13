// build.sc
import mill._, scalalib._
import coursier.maven.MavenRepository
import $ivy.`ch.epfl.scala::mill-bloop:1.0.0-M11`

trait TuubesModule extends ScalaModule {
  def scalaVersion = "2.12.6"
  def repositories = super.repositories ++ Seq(
    MavenRepository("https://jcenter.bintray.com")
  )
}

trait JUnitTesting extends TestModule {// JUnit 5 tests
  def ivyDeps = Agg(ivy"net.aichler:jupiter-interface:0.7.0")
  def testFrameworks = Seq("net.aichler.jupiter.api.JupiterFramework")
}

object core extends TuubesModule {
  def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"com.electronwill.night-config:core:3.3.0",
    ivy"com.electronwill.night-config:json:3.3.0",
    ivy"com.electronwill.night-config:toml:3.3.0",
    ivy"com.electronwill::niol:1.5.2",
    ivy"org.fusesource.jansi:jansi:1.17.1",
    ivy"com.typesafe.scala-logging::scala-logging:3.9.0",
    ivy"com.github.pathikrit::better-files:3.5.0"
  )
  object test extends Tests with JUnitTesting
}

object coreMacros extends TuubesModule {
  def compileIvyDeps = Agg(ivy"org.scala-lang:scala-reflect:2.12.6")
  def scalacPluginIvyDeps = Agg(ivy"org.scalamacros:paradise_2.12.6:2.1.1")
  def moduleDeps = Seq(core)
} 

object coreExamples extends TuubesModule {
  def moduleDeps = Seq(core)
}

