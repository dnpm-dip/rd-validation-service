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



  val patientRecordValidator: Validator[Issue,RDPatientRecord] =
    PatientRecordValidator[RDPatientRecord] combineWith {
      record =>
    
        implicit val patient = record.patient
    
        (  
          validateEach(record.diagnoses),
          validateEach(record.hpoTerms),
          ifDefined(record.ngsReports)(validateEach(_))
        )
        .errorsOr(record)
    }

/*
  val patientRecordValidator: Validator[Issue,RDPatientRecord] = {
    record =>

      implicit val patient =
        record.patient

      (  
        validate(patient),
        validateEach(record.diagnoses),
        validateEach(record.hpoTerms),
        ifDefined(record.ngsReports)(validateEach(_))
      )
      .errorsOr(record)
  }
*/

}

object RDValidators extends RDValidators
{

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
