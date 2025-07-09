package de.dnpm.dip.rd.validation.impl


import cats.{
  Applicative,
  Id
}
import de.ekut.tbi.validation.Validator
import de.ekut.tbi.validation.dsl._
import de.dnpm.dip.coding.{
  Coding,
  CodeSystemProvider
}
import de.dnpm.dip.coding.atc.ATC
import de.dnpm.dip.coding.icd.ICD10GM
import de.dnpm.dip.coding.hgnc.HGNC
import de.dnpm.dip.model.Patient
import de.dnpm.dip.service.validation.{
  Issue,
  Validators
}
import de.dnpm.dip.rd.model._
import Issue.{
  Error,
  Path
}


trait RDValidators extends Validators
{

  implicit val hpoTermNode: Path.Node[HPOTerm] =
    Path.Node("HPO-Term")

  implicit val variantNode: Path.Node[Variant] =
    Path.Node("Variante")

  implicit val smallVariantNode: Path.Node[SmallVariant] =
    Path.Node("Einfache-Variante")

  implicit val structuralVariantNode: Path.Node[StructuralVariant] =
    Path.Node("Struktur-Variante")

  implicit val cnvNode: Path.Node[CopyNumberVariant] =
    Path.Node("CNV")

  implicit val ngsReportNode: Path.Node[RDNGSReport] =
    Path.Node("NGS-Bericht")

  implicit val carePlanNode: Path.Node[RDCarePlan] =
    Path.Node("Board-Beschluss")


  implicit val atc: CodeSystemProvider[ATC,Id,Applicative[Id]]

  implicit val hgnc: CodeSystemProvider[HGNC,Id,Applicative[Id]]

  implicit val hpo: CodeSystemProvider[HPO,Id,Applicative[Id]]

  implicit val icd10gm: CodeSystemProvider[ICD10GM,Id,Applicative[Id]]

  implicit val alphaIdSE: CodeSystemProvider[AlphaIDSE,Id,Applicative[Id]]

  implicit val orphanet: CodeSystemProvider[Orphanet,Id,Applicative[Id]]


  implicit def diagnosisValidator(
    implicit patient: Patient
  ): Validator[Issue,RDDiagnosis] =
    diagnosis =>
      (
        validate(diagnosis.patient) at "Patient",
        (diagnosis.codes.map(_.system) must contain (allOf (Coding.System.UriSet[RDDiagnosis.Systems].values))) orElse (
          diagnosis.missingCodeReason must be (defined)
        ) otherwise (
          Error("Es muss ein ICD-10-GM, Orphanet und Alpha-ID-SE-Code definiert sein, oder als Grund explizit angegeben sein, dass kein passender code existiert")
        ) andThen (
          _ => validateEach(diagnosis.codes)
        ) at "Diagnose-Codes"
      )
      .errorsOr(diagnosis) on diagnosis


  implicit def hpoTermValidator(
    implicit patient: Patient
  ): Validator[Issue,HPOTerm] =
    ObservationValidator[HPOTerm]



  implicit def VariantValidator[V <: Variant: Path.Node](
    implicit patient: Patient
  ): Validator[Issue,V] =
    variant =>
      (
        validate(variant.patient) at "Patient",
        ifDefined(variant.genes.map(_.toList))(validateEach(_)) at "Gen(e)",
        validateOpt(variant.proteinChange)
      )
      .errorsOr(variant) on variant

      
  implicit def ngsReportValidator(
    implicit patient: Patient
  ): Validator[Issue,RDNGSReport] =
    ngs =>
      (
        validate(ngs.patient) at "Patient",
        ngs.variants must be (nonEmpty) otherwise (MissingResult("Varianten")),
        ifDefined(ngs.results.flatMap(_.smallVariants))(validateEach(_)),
        ifDefined(ngs.results.flatMap(_.structuralVariants))(validateEach(_)),
        ifDefined(ngs.results.flatMap(_.copyNumberVariants))(validateEach(_))
      )
      .errorsOr(ngs) on ngs


  implicit def carePlanValidator(
    implicit
    patient: Patient,
    ngsReports: Seq[RDNGSReport]
  ): Validator[Issue,RDCarePlan] = {

    implicit val variants =
      ngsReports.flatMap(_.variants)

    implicit val therapyRecommendationValidator =
      RecommendationValidator[RDTherapyRecommendation] combineWith {
        rec =>
          ifDefined(rec.medication.map(_.toList))(validateEach(_) at "Medikation")
            .map(_ => rec)
      }

    carePlan =>
      (
        validate(carePlan.patient) at "Patient",
        ifDefined(carePlan.therapyRecommendations)(validateEach(_)),
        ifDefined(carePlan.studyEnrollmentRecommendations)(validateEach(_))
      )
      .errorsOr(carePlan) on carePlan

  }

  implicit def TherapyValidator(
    implicit
    patient: Patient,
    recommendations: Iterable[RDTherapyRecommendation]
  ): Validator[Issue,RDTherapy] =
    therapy =>
      (
        validate(therapy.patient) at "Patient",
        validateOpt(therapy.basedOn) at "Therapie-Empfehlung",
        therapy.period must be (defined) otherwise (MissingValue("Zeitraum")),
        therapy.category must be (defined) otherwise (MissingValue("Therapie-Kategorie")),
        therapy.`type` must be (defined) otherwise (MissingValue("Therapie-Art")),
        ifDefined(therapy.medication.map(_.toList))(validateEach(_) at "Medikation")

      )
      .errorsOr(therapy) on therapy



  val patientRecordValidator: Validator[Issue,RDPatientRecord] =
    PatientRecordValidator[RDPatientRecord] combineWith {
      record =>
    
        implicit val patient =
          record.patient
   
        implicit val ngsReports =
          record.ngsReports.getOrElse(List.empty)

        implicit val recommendations =
          record.getCarePlans
            .flatMap(_.therapyRecommendations.getOrElse(List.empty))

        (  
          validateEach(record.diagnoses),
          validateEach(record.hpoTerms),
          ifDefined(record.ngsReports)(validateEach(_)),
          record.carePlans.validateEach,
          ifDefined(record.therapies.map(_.flatMap(_.history.toList)))(validateEach(_)),
          ifDefined(record.followUps.filter(_.nonEmpty)){
            followUps =>
              val gmfcs = record.gmfcsStatus.getOrElse(List.empty) 
              (
                gmfcs must have (size (greaterThanOrEqual (followUps.size))) otherwise (
                  Error(s"Es sind ${followUps.size} Follow-ups deklariert, aber nur ${gmfcs.size} GMFCS-Status-Werte vorhanden: Bei jedem FU muss der GMFCS erfasst worden sein.")
                    at "GMFCS-Status"
                ),
                record.hpoTerms.validateEach {
                  hpoTerm =>
                    (
                      hpoTerm.status.map(_.history.size).getOrElse(0) must be (greaterThanOrEqual (followUps.size - 1)) otherwise (
                        Error(s"Beim jedem Follow-up muss für HPO-Terme (sofern nicht neu dazugekommen) der Veränderungs-Status erfasst worden sein, aber es kommen weniger als ${followUps.size-1} erwartete Werte vor") at "Status-Historie"
                      )
                    )
                    .map(_ => hpoTerm) on hpoTerm
                } 
              )
              .errorsOr(followUps)
          },
        )
        .errorsOr(record)
    }

}

object RDValidators extends RDValidators
{

  override implicit lazy val atc: CodeSystemProvider[ATC,Id,Applicative[Id]] =
    ATC.Catalogs
      .getInstance[Id]
      .get

  override implicit lazy val hgnc: CodeSystemProvider[HGNC,Id,Applicative[Id]] =
    HGNC.GeneSet
      .getInstance[Id]
      .get

  override implicit val hpo: CodeSystemProvider[HPO,Id,Applicative[Id]] =
    HPO.Ontology
      .getInstance[Id]
      .get

  override implicit lazy val icd10gm: CodeSystemProvider[ICD10GM,Id,Applicative[Id]] =
    ICD10GM.Catalogs
      .getInstance[Id]
      .get

  override implicit lazy val alphaIdSE: CodeSystemProvider[AlphaIDSE,Id,Applicative[Id]] =
    AlphaIDSE.Catalogs
      .getInstance[cats.Id]
      .get

  override implicit lazy val orphanet: CodeSystemProvider[Orphanet,Id,Applicative[Id]] =
    Orphanet.Ordo
      .getInstance[Id]
      .get

}
