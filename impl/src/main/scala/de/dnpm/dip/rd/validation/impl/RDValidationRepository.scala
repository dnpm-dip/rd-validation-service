package de.dnpm.dip.rd.validation.impl


import java.io.File
import scala.concurrent.Future
import scala.util.chaining._
import cats.Monad
import de.dnpm.dip.util.{
  SPI,
  SPILoader
}
import de.dnpm.dip.service.validation.{
  Repository,
  FSBackedRepository
}
import de.dnpm.dip.rd.model.RDPatientRecord


trait RDValidationRepository extends Repository[Future,Monad[Future],RDPatientRecord]

trait RDValidationRepositoryProvider extends SPI[RDValidationRepository]

object RDValidationRepository extends SPILoader[RDValidationRepositoryProvider]
{

  private[impl] val dataDirProp =
    "dnpm.dip.data.dir"


  override def getInstance =
    super.getInstance // Load implementation from runtime context (e.g. test implementation)...
      .recover {      // ... else default to file system-backed repo
        case _ =>
          Option(System.getProperty(dataDirProp)) match {
            case Some(dir) =>
              val validationDir = new File(s"$dir/rd_data/validation")
              validationDir.mkdirs
              new FSBackedRepository[Future,RDPatientRecord](validationDir)
                with RDValidationRepository
          
            case None =>
              val msg =
                s"System property $dataDirProp for the data storage directory is undefined, can't instantiate validation data repository!"
                 .tap(log.error)
              throw new IllegalStateException(msg)
          }
      }

}
