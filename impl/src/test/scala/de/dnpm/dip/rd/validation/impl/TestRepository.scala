package de.dnpm.dip.rd.validation.impl


import scala.concurrent.Future
import de.dnpm.dip.rd.model.RDPatientRecord
import de.dnpm.dip.service.validation.InMemRepository


class TestRepositoryProvider extends RDValidationRepositoryProvider
{

  override def getInstance = 
    new InMemRepository[Future,RDPatientRecord] with RDValidationRepository

}
