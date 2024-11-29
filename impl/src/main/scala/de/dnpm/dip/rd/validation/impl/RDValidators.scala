package de.dnpm.dip.rd.validation.impl


import cats.{
  Applicative,
  Id
}
import de.ekut.tbi.validation.Validator
import de.ekut.tbi.validation.dsl._
import de.dnpm.dip.coding.CodeSystemProvider
import de.dnpm.dip.coding.icd.ICD10GM
import de.dnpm.dip.coding.hgnc.HGNC
import de.dnpm.dip.model.Patient
import de.dnpm.dip.service.validation.{
  Issue,
  Validators
}
import de.dnpm.dip.rd.model._
import Issue.Path


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

  implicit val omim: CodeSystemProvider[OMIM,Id,Applicative[Id]]

  implicit val orphanet: CodeSystemProvider[Orphanet,Id,Applicative[Id]]


  implicit def diagnosisValidator(
    implicit patient: Patient
  ): Validator[Issue,RDDiagnosis] =
    diagnosis =>
      (
        validate(diagnosis.patient) at "Patient",
        validateEach(diagnosis.categories) at "Krankheits-Kategorie",
        diagnosis.onsetAge must be (defined) otherwise (
          MissingValue("Alter der Erstmanifestation")
        )
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
        ifDefined(ngs.smallVariants)(validateEach(_)),
        ifDefined(ngs.structuralVariants)(validateEach(_)),
        ifDefined(ngs.copyNumberVariants)(validateEach(_))
      )
      .errorsOr(ngs) on ngs



  val patientRecordValidator: Validator[Issue,RDPatientRecord] = {
    record =>

      implicit val patient =
        record.patient

      (  
        validate(patient),
        validate(record.diagnosis),
        validateEach(record.hpoTerms),
        ifDefined(record.ngsReports)(validateEach(_))
      )
      .errorsOr(record)

  }

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

  override implicit lazy val omim: CodeSystemProvider[OMIM,Id,Applicative[Id]] =
    OMIM.Catalog
      .getInstance[Id]
      .get

  override implicit lazy val orphanet: CodeSystemProvider[Orphanet,Id,Applicative[Id]] =
    Orphanet.Ordo
      .getInstance[Id]
      .get

}
