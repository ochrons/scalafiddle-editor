import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

/**
  * Application settings. Configure the build for your application here.
  * You normally don't have to touch the actual build definition after this.
  */
object Settings {
  /** The version of your application */
  val version = "1.0.6"

  /** Options for the scala compiler */
  val scalacOptions = Seq(
    "-Xlint",
    "-unchecked",
    "-deprecation",
    "-feature"
  )

  /** Declare global dependency versions here to avoid mismatches in multi part dependencies */
  object versions {
    val scala = "2.11.8"
    val scalatest = "3.0.1"
    val scalaDom = "0.9.1"
    val scalajsReact = "0.11.3"
    val scalaCSS = "0.5.1"
    val autowire = "0.2.6"
    val booPickle = "1.2.4"
    val diode = "1.1.1"
    val uTest = "0.4.3"
    val upickle = "0.4.4"
    val slick = "3.1.1"
    val silhouette = "4.0.0"

    val jQuery = "3.1.1"
    val semantic = "2.2.7"
    val ace = "1.2.3"

    val react = "15.4.2"

    val playScripts = "0.5.0"
  }

  /**
    * These dependencies are shared between JS and JVM projects
    * the special %%% function selects the correct version for each project
    */
  val sharedDependencies = Def.setting(Seq(
    "com.lihaoyi" %%% "autowire" % versions.autowire,
    "com.lihaoyi" %%% "upickle" % versions.upickle,
    "org.scalatest" %%% "scalatest" % versions.scalatest % "test"
  ))

  /** Dependencies only used by the JVM project */
  val jvmDependencies = Def.setting(Seq(
    "com.typesafe.slick" %% "slick" % versions.slick,
    "com.typesafe.slick" %% "slick-hikaricp" % versions.slick,
    "com.h2database" % "h2" % "1.4.193",
    "org.postgresql" % "postgresql" % "9.4-1206-jdbc41",
    "com.mohiva" %% "play-silhouette" % versions.silhouette,
    "com.mohiva" %% "play-silhouette-password-bcrypt" % versions.silhouette,
    "com.mohiva" %% "play-silhouette-persistence" % versions.silhouette,
    "com.mohiva" %% "play-silhouette-crypto-jca" % versions.silhouette,
    "net.codingwell" %% "scala-guice" % "4.1.0",
    "com.iheart" %% "ficus" % "1.4.0",
    "com.vmunier" %% "play-scalajs-scripts" % versions.playScripts,
    "org.webjars" % "font-awesome" % "4.7.0" % Provided,
    "org.webjars" % "Semantic-UI" % versions.semantic % Provided,
    "org.webjars" % "ace" % versions.ace % Provided
  ))

  /** Dependencies only used by the JS project (note the use of %%% instead of %%) */
  val scalajsDependencies = Def.setting(Seq(
    "com.github.japgolly.scalajs-react" %%% "core" % versions.scalajsReact,
    "com.github.japgolly.scalajs-react" %%% "extra" % versions.scalajsReact,
    "com.github.japgolly.scalacss" %%% "ext-react" % versions.scalaCSS,
    "io.suzaku" %%% "diode" % versions.diode,
    "io.suzaku" %%% "diode-react" % versions.diode,
    "com.github.marklister" %%% "base64" % "0.2.3",
    "org.scala-js" %%% "scalajs-dom" % versions.scalaDom
  ))

  /** Dependencies for external JS libs that are bundled into a single .js file according to dependency order */
  val jsDependencies = Def.setting(Seq(
    "org.webjars.bower" % "react" % versions.react / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React",
    "org.webjars.bower" % "react" % versions.react / "react-dom.js" minified "react-dom.min.js" dependsOn "react-with-addons.js" commonJSName "ReactDOM",
    "org.webjars" % "jquery" % versions.jQuery / "jquery.js" minified "jquery.min.js",
    "org.webjars" % "Semantic-UI" % versions.semantic / "semantic.js" minified "semantic.min.js" dependsOn "jquery.js"
  ))
}
