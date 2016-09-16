import java.io.{File, FileInputStream}
import java.util.Properties

import android.Keys._
import sbt.Keys._
import sbt._

import scala.annotation.tailrec
import scala.collection.JavaConverters._

object ReplacePropertiesGenerator {

  lazy val propertyNames: List[String] = List(
    "backend.v1.url",
    "backend.v1.appid",
    "backend.v1.appkey",
    "backend.v2.url")

  lazy val propertiesFileName = sys.env.getOrElse("9CARDS_PROPERTIES", "debug.properties")

  def replaceValuesTask = Def.task[Seq[File]] {

    val log = streams.value.log

    log.debug("Replacing values")
    try {
      val dir: (File, File) = (collectResources in Android).value
      val valuesFile: File =  new File(dir._2, "/values/values.xml")
      replaceContent(valuesFile, valuesFile)(log)
      Seq(valuesFile)
    } catch {
      case e: Throwable =>
        log.error(s"An error occurred replacing values")
        throw e
    }
  }

  def replaceContent(origin: File, target: File)(log: Logger) = {

    def loadPropertiesFile: Option[File] = {
      val file = new File(propertiesFileName)
      if (file.exists()) Some(file) else {
        log.warn(s"File not found at ${file.getAbsolutePath}")
        None
      }
    }

    def populateDefaultProperties(propertiesMap: Map[String, String]): Map[String, String] =
      propertiesMap ++ (propertyNames filterNot propertiesMap.contains map (_ -> "") toMap)

    def loadPropertiesMap: Map[String, String] = populateDefaultProperties {
      (loadPropertiesFile map { file =>
        val properties = new Properties()
        properties.load(new FileInputStream(file))
        properties.asScala.toMap
      }) getOrElse Map.empty
    }

    def replaceLine(properties: Map[String, String], line: String) = {
      @tailrec
      def replace(properties: Map[String, String], line: String): String = {
        properties.headOption match {
          case Some(property) =>
            val (key, value) = property
            val name = s"$${$key}"
            replace(properties.tail, if (line.contains(name)) line.replace(name, value) else line)
          case None => line
        }
      }
      replace(properties, line)
    }

    log.debug(s"Loading properties file $propertiesFileName")
    val propertiesMap = loadPropertiesMap
    val content = IO.readLines(origin) map (replaceLine(propertiesMap, _))
    IO.write(target, content.mkString("\n"))
  }

}