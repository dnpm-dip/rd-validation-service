package de.dnpm.dip.rd.mvh.impl


import java.io.File
import scala.concurrent.Future
import scala.util.chaining._
import cats.Monad
import de.dnpm.dip.util.{
  SPI,
  SPILoader
}
import de.dnpm.dip.service.mvh.{
  Repository,
  FSBackedRepository,
}
import de.dnpm.dip.rd.model.RDPatientRecord


trait RepositoryImpl extends Repository[Future,Monad[Future],RDPatientRecord]

trait RepositoryImplProvider extends SPI[RepositoryImpl]

object RepositoryImpl extends SPILoader[RepositoryImplProvider]
{

  private[impl] val dataDirProp =
    "dnpm.dip.data.dir"


  override def getInstance =
    super.getInstance // Load implementation from runtime context (e.g. test implementation)...
      .recover {      // ... else default to file system-backed repo
        case t =>
          Option(System.getProperty(dataDirProp)) match {
            case Some(dir) =>
              val dataDir = new File(s"$dir/rd_data/mvh")
              dataDir.mkdirs
              new FSBackedRepository[Future,RDPatientRecord](dataDir)
                with RepositoryImpl

            case None =>
              val msg =
                s"System property $dataDirProp for the data storage directory is undefined, can't instantiate RD data MVH repository!"
                 .tap(log.error)
              throw new IllegalStateException(msg)
          }
      }

}

