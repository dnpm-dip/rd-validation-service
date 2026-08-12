package de.dnpm.dip.rd.mvh.impl


import scala.concurrent.Future
import cats.Monad
import de.dnpm.dip.service.mvh.{
  BaseMVHService,
  Repository,
  Submission,
  UseCase
}
import de.dnpm.dip.rd.mvh.api.{
  RDMVHService,
  RDMVHServiceProvider
}
import de.dnpm.dip.rd.model.RDPatientRecord
import de.dnpm.dip.rd.model.RDDiagnosis.FamilyControlLevel


class RDMVHServiceProviderImpl extends RDMVHServiceProvider
{
  override def getInstance: RDMVHService =
    RDMVHServiceImpl.instance
}


object RDMVHServiceImpl
{
  val instance =
    new RDMVHServiceImpl(
      RepositoryImpl.getInstance.get
    )
}


class RDMVHServiceImpl(
  repo: Repository[Future,Monad[Future],RDPatientRecord]
)
extends BaseMVHService(
  UseCase.RD,
  repo
)
with RDMVHService
{

  import de.dnpm.dip.service.mvh.extensions._

  override def sequenceTypes(
    record: RDPatientRecord
  ): Option[Set[Submission.SequenceType.Value]] =
    Option.when(record.mvhSequencingReports.nonEmpty)(Set(Submission.SequenceType.DNA))


  /**
   * Return the maximum FamilyControlLevel documented on the RDPatientRecord, if defined,
   * mapped to Submission.DiagnosticExtent.Value
   */
  override def diagnosticExtent(record: RDPatientRecord): Option[Submission.DiagnosticExtent.Value] =
    record.diagnoses.toList
      .flatMap(_.familyControlLevel.map(_.code.enumValue))
      .maxOption
      .collect {
        case FamilyControlLevel.Single => Submission.DiagnosticExtent.SingleGenome
        case FamilyControlLevel.Duo    => Submission.DiagnosticExtent.DuoGenome
        case FamilyControlLevel.Trio   => Submission.DiagnosticExtent.TrioGenome
      }
    
}
