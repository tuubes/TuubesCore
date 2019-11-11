import mill._, scalalib._
import coursier.maven.MavenRepository

trait TuubesModule extends ScalaModule {
  def scalaVersion = "2.13.1"
  def repositories = super.repositories ++ Seq(
    MavenRepository("https://jcenter.bintray.com")
  )
}

trait JUnitTests extends TestModule {// JUnit 5 tests
  def ivyDeps = Agg(ivy"net.aichler:jupiter-interface:0.8.3")
  def testFrameworks = Seq("net.aichler.jupiter.api.JupiterFramework")
}

object maths extends TuubesModule {
  object test extends Tests with JUnitTests
}

object engine extends TuubesModule {
  def moduleDeps = Seq(maths)
}

object plugin extends TuubesModule {
  def moduleDeps = Seq(engine)
  def ivyDeps = Agg(
    ivy"com.electronwill.night-config:toml:3.6.2"
  )
}

object worldgen extends TuubesModule {
  def moduleDeps = Seq(engine)
}

object network extends TuubesModule {
  def moduleDeps = Seq(engine)
  def ivyDeps = Agg(
    ivy"com.electronwill::niol:2.0.0"
  )
}

object runnable extends TuubesModule {
  def mainClass = Some("org.tuubes.Main")
  def moduleDeps = Seq(plugin, worldgen, network)
  def ivyDeps = Agg(
    ivy"org.fusesource.jansi:jansi:1.18"
  )
}

