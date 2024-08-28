package de.dnpm.dip.rd.mvh.impl


import scala.concurrent.Future
import de.dnpm.dip.rd.model.RDPatientRecord
import de.dnpm.dip.service.mvh.InMemRepository


final class FakeRepositoryProvider extends RepositoryImplProvider
{
  override def getInstance =
    new InMemRepository[Future,RDPatientRecord] with RepositoryImpl

}
