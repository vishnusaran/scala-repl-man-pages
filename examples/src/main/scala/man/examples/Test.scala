package man.examples

import java.io.File

import man.macrodef.{ help, man }

/** This class has the following responsibilities.
 */
object Test extends App {

    //    @man("Utils trait help")
    trait Utils

    //    @man("This class is used ")
    //    abstract class PathUtils(path: String, var ext: String) extends Utils {
    //        @help("this is variable to store madhu's name")
    //        val dir: File = new File(path)
    //        @help("this is a var storing 10")
    //        var somevar: Int = 10
    //        @help("this is a getName method")
    //        def getName: String = {
    //            dir.getName
    //        }
    //    }

    //    @man("File utitlity which eases file usage")
    //    object FileUtils {
    //
    //        @help("max file accessed through this object")
    //        val fileCount: Int = 10
    //        @help("method to get file path")
    //        def getPath: String = "blah"
    //
    //    }

    //    @man("FileUtils class to get the dir")
    @man(
        """
      |FileUtls class
    """.stripMargin)
    class FileUtils() {
        @help(
            """
              |this method gets the dir name
            """.stripMargin)
        def getMyDir: String = "blah"
    }

    println(FileUtils.help)

}
