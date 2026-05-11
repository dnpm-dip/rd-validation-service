package de.dnpm.dip.rd.validation.impl


import scala.concurrent.Future
import cats.Monad
import de.ekut.tbi.validation.Validator
import de.dnpm.dip.service.validation.{
  BaseValidationService,
  Issue,
  Repository
}
import de.dnpm.dip.model.NGSReport.Type.{ 
  GenomeShortRead,
  GenomeLongRead
}
import de.dnpm.dip.rd.model.RDPatientRecord
import de.dnpm.dip.rd.validation.api.{
  RDValidationService,
  RDValidationServiceProvider
}


class RDValidationServiceProviderImpl extends RDValidationServiceProvider
{
  override def getInstance =
    RDValidationServiceImpl.instance
}


object RDValidationServiceImpl
{

  lazy val instance =
    new RDValidationServiceImpl(
      RDValidators.patientRecordValidator,
      RDValidationRepository.getInstance.get
    )

}


class RDValidationServiceImpl
(
  private val validator: Validator[Issue,RDPatientRecord],
  private val repo: Repository[Future,Monad[Future],RDPatientRecord]
)
extends BaseValidationService(
  Set(GenomeShortRead,GenomeLongRead), // Only Whole-Genome Seq. admissible for RD from now on
  validator,
  repo
)
with RDValidationService
