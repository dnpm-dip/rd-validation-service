package de.dnpm.dip.rd.mvh.impl


import scala.concurrent.Future
import cats.Monad
import de.dnpm.dip.service.Distribution
import de.dnpm.dip.service.mvh.{
  BaseMVHService,
  Report,
  Repository,
  UseCase
}
import de.dnpm.dip.rd.mvh.api.{
  RDMVHService,
  RDMVHServiceProvider,
  RDReport
}
import de.dnpm.dip.rd.model.RDPatientRecord



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

  override def report(
    criteria: Report.Criteria
  )(
    implicit env: Monad[Future]
  ): Future[RDReport] =  
    env.map(
      baseReport(criteria)
    ){
      case (report,submissions) =>

        val familyControlLevels =
          submissions.flatMap(_.record.diagnoses.toList.flatMap(_.familyControlLevel.map(_.code.enumValue)))

        RDReport(
          report.site,
          report.createdAt,
          report.quarter,
          report.period,
          report.useCase,
          report.submissionTypes,
          Distribution.of(familyControlLevels),
          report.consentRevocations
        )

    }

}
