package de.dnpm.dip.rd.validation.impl


import scala.util.chaining._
import cats.{
  Applicative,
  Id
}
import cats.data.Ior
import cats.syntax.validated._
import de.ekut.tbi.validation.{
  Validator,
  NegatableValidator
}
import de.ekut.tbi.validation.dsl._
import de.dnpm.dip.util.DisplayLabel
import de.dnpm.dip.util.Displays
import de.dnpm.dip.coding.{
  CodedEnum,
  Coding,
  CodeSystem,
  CodeSystemProvider
}
import de.dnpm.dip.coding.icd.ICD10GM
import de.dnpm.dip.coding.hgnc.HGNC
import de.dnpm.dip.coding.hgvs.HGVS
import de.dnpm.dip.model.{
  Patient,
  Reference,
}
import de.dnpm.dip.service.validation.{
  HasId,
  Issue,
  Validators
}
import de.dnpm.dip.rd.model._
import Issue.{
  Error,
  Info,
  Path,
  Warning
}
import Path.root
import Path.syntax._


trait RDValidators extends Validators
{

  implicit val hpoTermNode: Path.Node[HPOTerm] =
    Path.Node("HPO-Term")
/*
  implicit val performanceStatusNode: Path.Node[PerformanceStatus] =
    Path.Node("Performance-Status")

  implicit val tumorSpecimenNode: Path.Node[TumorSpecimen] =
    Path.Node("Tumor-Probe")

  implicit val tumorCellContentNode: Path.Node[TumorCellContent] =
    Path.Node("Tumor-Zellgehalt")

  implicit val tumorMorphologyNode: Path.Node[TumorMorphology] =
    Path.Node("Tumor-Morphologie")

  implicit val histologyReportNode: Path.Node[HistologyReport] =
    Path.Node("Histologie-Bericht")

  implicit val ngsReportNode: Path.Node[NGSReport] =
    Path.Node("NGS-Bericht")
*/

  implicit val variantNode: Path.Node[Variant] =
    Path.Node("Variante")

  implicit val smallVariantNode: Path.Node[SmallVariant] =
    Path.Node("Einfache Variante")

  implicit val structuralVariantNode: Path.Node[StructuralVariant] =
    Path.Node("Struktur-Variante")

  implicit val cnvNode: Path.Node[CopyNumberVariant] =
    Path.Node("CopyNumberVariant")


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
          Warning("Fehlende Angabe") at "Alter der Erstmanifestation"
        )
      )
      .errorsOr(diagnosis) on diagnosis


 implicit def hpoTermValidator(
    implicit patient: Patient
  ): Validator[Issue,HPOTerm] =
    ObservationValidator[HPOTerm]


 def VariantValidator[V <: Variant: Path.Node](
    implicit patient: Patient
  ): Validator[Issue,V] =
    variant =>
      (
        validate(variant.patient) at "Patient",
        ifDefined(variant.proteinChange.map(_.code.value))(
          code => code must matchRegex (HGVS.Protein.threeLetterAA) otherwise (
            Error(s"Ungültiger Code '$code', erwarte 3-Buchstaben-Format") at "Amino-Säure-Austausch"
          )
        )
      )
      .errorsOr(variant) on variant




  val patientRecordValidator: Validator[Issue,RDPatientRecord] = {
    record =>

      implicit val patient =
        record.patient

      (  
        validate(patient),
        validate(record.diagnosis),
        validateEach(record.hpoTerms)
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
