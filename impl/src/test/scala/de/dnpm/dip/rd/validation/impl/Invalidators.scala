package de.dnpm.dip.rd.validation.impl


import java.time.YearMonth
import de.dnpm.dip.coding.Code
import de.dnpm.dip.coding.hgvs.HGVS
import de.dnpm.dip.model.Patient
import de.dnpm.dip.rd.model._


trait Invalidators
{

  def invalidate(patient: Patient) =
    patient.copy(
      birthDate = YearMonth.now.minusYears(135)
    )

  def invalidate(diag: RDDiagnosis): RDDiagnosis =
    diag.copy(
      codes = diag.codes.map(
        coding => coding.copy(code = Code[RDDiagnosis.Systems]("xxxxx"))
      )
    )


  def invalidate(ngs: RDNGSReport): RDNGSReport = {

    def invalidate(v: SmallVariant): SmallVariant =
      v.copy(
        proteinChange = v.proteinChange.map(_ => Code[HGVS.Protein]("G12C"))
      )

    ngs.copy(
      results = ngs.results.map(
        r => r.copy(
          smallVariants = r.smallVariants.map(_.map(invalidate))
        )
      )
    )

  }


  def invalidate(record: RDPatientRecord): RDPatientRecord =
    record.copy(
      patient = invalidate(record.patient),
      diagnoses = record.diagnoses.map(invalidate),
      ngsReports = record.ngsReports.map(_.map(invalidate))
    )

}
