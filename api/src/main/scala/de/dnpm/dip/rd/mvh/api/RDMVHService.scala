package de.dnpm.dip.rd.mvh.api


import scala.concurrent.Future
import cats.Monad
import de.dnpm.dip.util.{
  SPI,
  SPILoader
}
import de.dnpm.dip.service.mvh.MVHService
import de.dnpm.dip.rd.model.RDPatientRecord



trait RDMVHService extends MVHService[Future,Monad[Future],RDPatientRecord]
{
  type ReportType = RDReport
}

trait RDMVHServiceProvider extends SPI[RDMVHService]

object RDMVHService extends SPILoader[RDMVHServiceProvider]


