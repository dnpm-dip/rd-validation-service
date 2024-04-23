package de.dnpm.dip.rd.validation.impl



import de.dnpm.dip.coding.Coding
import de.dnpm.dip.coding.icd.ICD10GM
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



  def invalidate(record: RDPatientRecord): RDPatientRecord =
    record.copy(
      patient =
        invalidate(record.patient)
    )

}
