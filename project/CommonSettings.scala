import org.scalastyle.sbt.ScalastylePlugin
import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtScalariform.{ ScalariformKeys, _ }

object CommonSettings {

    private lazy val defaultSettings = Seq(
        scalaVersion := BuildCommon.scalaVersion,
        scalacOptions := BuildCommon.scalacOption,
        javacOptions := BuildCommon.javacOptions,
        parallelExecution in Test := true,
        resolvers ++= BuildCommon.resolvers)

    private lazy val localScalariformSettings = scalariformSettings ++ Seq(
        ScalariformKeys.preferences := {
            import scalariform.formatter.preferences._
            FormattingPreferences().
                setPreference(AlignParameters, true).
                setPreference(AlignSingleLineCaseStatements, true).
                setPreference(CompactControlReadability, true).
                setPreference(CompactStringConcatenation, false).
                setPreference(DoubleIndentClassDeclaration, true).
                setPreference(FormatXml, true).
                setPreference(IndentLocalDefs, true).
                setPreference(IndentPackageBlocks, true).
                setPreference(IndentSpaces, 4).
                setPreference(MultilineScaladocCommentsStartOnFirstLine, true).
                setPreference(PreserveSpaceBeforeArguments, false).
                setPreference(PreserveDanglingCloseParenthesis, false).
                setPreference(RewriteArrowSymbols, false).
                setPreference(SpaceBeforeColon, false).
                setPreference(SpaceInsideBrackets, false).
                setPreference(SpacesWithinPatternBinders, true)
        })

    private lazy val localScalaStylePluginSettings = ScalastylePlugin.projectSettings ++ Seq(
        ScalastylePlugin.scalastyleTarget := sbt.file("target/scalastyle-result.xml"),
        ScalastylePlugin.scalastyleFailOnError := true)

    private lazy val commandAlias = addCommandAlias("runAll", ";compile;test;scalastyle") ++
        addCommandAlias("cleanRunAll", ";clean;compile;test;scalastyle")

    val commonBuildSettings = Defaults.coreDefaultSettings ++
        defaultSettings ++
        localScalariformSettings ++
        localScalaStylePluginSettings ++
        commandAlias ++
        addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0-M5" cross CrossVersion.full)

}