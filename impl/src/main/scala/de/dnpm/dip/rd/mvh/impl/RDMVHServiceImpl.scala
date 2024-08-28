package de.dnpm.dip.rd.mvh.impl


import scala.concurrent.Future
import cats.Monad
import de.dnpm.dip.service.mvh.{
  BaseMVHService,
  Repository,
  UseCase
}
import de.dnpm.dip.rd.mvh.api.{
  RDMVHService,
  RDMVHServiceProvider
}
import de.dnpm.dip.rd.model.RDPatientRecord



class RDMVHServiceProviderImpl extends RDMVHServiceProvider
{
  override def getInstance: RDMVHService =
    RDMVHServiceImpl.instance
}


object RDMVHServiceImpl
{
  val instance =
    new RDMVHServiceImpl(
      RepositoryImpl.getInstance.get
    )
}


class RDMVHServiceImpl(
  repo: Repository[Future,Monad[Future],RDPatientRecord]
)
extends BaseMVHService(
  UseCase.RD,
  repo
)
with RDMVHService

