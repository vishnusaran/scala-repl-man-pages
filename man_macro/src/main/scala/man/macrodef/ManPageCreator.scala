package man.macrodef

/** This class has the following responsibilities.
 */
object ManPageCreator {
    def create(typeName: String, topHelp: String, definition: String, internalHelps: List[String]): String = {
        val head = s"Help Page for $typeName"

        s"""
          |$head
          |${(0 until head.length).map(a => "-").mkString}
          |
          |class/trait/object Definition
          |-----------------------------
          |$definition
          |
          |$topHelp
          |
          |Documented Members
          |------------------
          |${internalHelps.mkString("\n")}
          |
        """.stripMargin
    }
}
