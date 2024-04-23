package de.dnpm.dip.rd.validation.api


import scala.concurrent.Future
import cats.Monad
import de.dnpm.dip.util.{
  SPI,
  SPILoader
}
import de.dnpm.dip.service.validation.ValidationService
import de.dnpm.dip.rd.model.RDPatientRecord


trait RDValidationService extends ValidationService[
  Future,
  Monad[Future],
  RDPatientRecord
]


trait RDValidationServiceProvider extends SPI[RDValidationService]

object RDValidationService extends SPILoader[RDValidationServiceProvider]

