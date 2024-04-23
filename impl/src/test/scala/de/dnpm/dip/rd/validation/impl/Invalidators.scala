package de.dnpm.dip.rd.validation.impl



import de.dnpm.dip.coding.{
  Code,
  Coding
}
import de.dnpm.dip.coding.icd.ICD10GM
import de.dnpm.dip.coding.hgvs.HGVS
import de.dnpm.dip.model.{
  Id,
  Patient,
  Reference
}
import de.dnpm.dip.rd.model._


trait Invalidators
{

  def invalidate(patient: Patient) =
    patient.copy(
      dateOfDeath = None,
      healthInsurance = None
    )

  def invalidate(diag: RDDiagnosis): RDDiagnosis =
    diag.copy(
      categories = diag.categories.map(
        coding => coding.copy(code = Code[RDDiagnosis.Category]("xxxxx"))
      )
    )


  def invalidate(ngs: RDNGSReport): RDNGSReport = {

    def invalidate(v: SmallVariant): SmallVariant =
      v.copy(
        proteinChange = v.proteinChange.map(_ => Coding[HGVS.Protein]("G12C"))
      )

    ngs.copy(
      smallVariants = ngs.smallVariants.map(_.map(invalidate))
    )

  }


  def invalidate(record: RDPatientRecord): RDPatientRecord =
    record.copy(
      patient = invalidate(record.patient),
      diagnosis = invalidate(record.diagnosis),
      ngsReports = record.ngsReports.map(_.map(invalidate))
    )

}
