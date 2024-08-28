package de.dnpm.dip.rd.mvh.impl

import java.nio.file.Files.createTempDirectory
import org.scalatest.flatspec.AnyFlatSpec
import de.dnpm.dip.rd.mvh.api.RDMVHService


class Tests extends AnyFlatSpec
{

  lazy val serviceLoad =
    RDMVHService.getInstance


  "Loading RDMVHService" must "have worked" in {
    assert(serviceLoad.isSuccess)
  }

}
