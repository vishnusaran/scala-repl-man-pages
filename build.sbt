lazy val repl_man = (project in file(".")).aggregate(
man_macro,
examples
).settings(CommonSettings.commonBuildSettings)

lazy val man_macro = project.settings(CommonSettings.commonBuildSettings)
lazy val examples = project.settings(CommonSettings.commonBuildSettings).dependsOn(man_macro)

