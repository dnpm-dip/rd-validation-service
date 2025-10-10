package de.dnpm.dip.rd.mvh.api


import java.time.{
  LocalDate,
  LocalDateTime
}
import de.dnpm.dip.coding.Coding
import de.dnpm.dip.service.Distribution
import de.dnpm.dip.service.mvh.{
  Consent,
  Report,
  Submission,
  UseCase
}
import de.dnpm.dip.model.{
  ClosedPeriod,
  Site
}
import de.dnpm.dip.rd.model.RDDiagnosis.FamilyControlLevel
import play.api.libs.json.{ 
  Json,
  Format,
  OFormat
}


final case class RDReport
(
  site: Coding[Site],
  createdAt: LocalDateTime,
  quarter: Option[Report.Quarter.Value],
  period: ClosedPeriod[LocalDate],
  useCase: UseCase.Value,
  submissionTypes: Distribution[Submission.Type.Value],
  familyControlLevels: Distribution[FamilyControlLevel.Value],
  consentRevocations: Option[Map[Consent.Category.Value,Distribution[Consent.Subject.Value]]]
)
extends Report


object RDReport
{ 
  implicit val formatFamilyControlLevel: Format[FamilyControlLevel.Value] =
    Json.formatEnum(FamilyControlLevel)

  implicit val format: OFormat[RDReport] =
    Json.format[RDReport]
}
