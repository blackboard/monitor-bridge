import sbt._
import Keys._

object MyBuild extends Build {
  lazy val distZip = TaskKey[Unit]("dist-zip")
  
  def distZipTask = distZip <<= (update, baseDirectory, packageBin in Compile) map {
    (updateReport, baseDir, bin) => {
      val distDir = baseDir / "target" / "dist"
      var dstZip = baseDir/ "target" / "dist.zip"
         
      updateReport.allFiles foreach { srcPath =>
        val destPath = distDir / "lib" / srcPath.getName
        IO.copyFile(srcPath, destPath, preserveLastModified = true)
      }
      IO.copyFile(bin, distDir / "lib" /bin.getName, true)
      
      val scripts = baseDir / "bin" * "*"
      scripts.get foreach { srcPath =>
        val destPath = distDir / "bin" / srcPath.getName
        IO.copyFile(srcPath, destPath, preserveLastModified = true)
      }

      val conf = baseDir / "conf" * "*"
      conf.get foreach { srcPath =>
        val destPath = distDir / "conf" / srcPath.getName
        IO.copyFile(srcPath, destPath, preserveLastModified = true)
      }
      
      val files = (distDir ** "*") filter {!_.isDirectory}
      val base: Seq[File] = distDir :: Nil
      val mappings: Seq[(File, String)] = files x Path.relativeTo(base)
      
      IO.delete(dstZip)
      IO.zip(mappings, dstZip)
    }
  }

  lazy val root = Project(
    "root",
    file("."),
    settings = Defaults.defaultSettings ++ Seq(distZipTask))
}
