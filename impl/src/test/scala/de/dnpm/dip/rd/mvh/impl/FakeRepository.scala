package de.dnpm.dip.rd.mvh.impl


import java.nio.file.Files.createTempDirectory
import scala.concurrent.Future
import de.dnpm.dip.rd.model.RDPatientRecord
import de.dnpm.dip.service.mvh.FSBackedRepository


final class FakeRepositoryProvider extends RepositoryImplProvider
{
  override def getInstance = {

    val dataDir = createTempDirectory("rd_mvh_fs_backed_repo_test_dir").toFile
    dataDir.deleteOnExit

    new FSBackedRepository[Future,RDPatientRecord](dataDir) with RepositoryImpl
  }

}
