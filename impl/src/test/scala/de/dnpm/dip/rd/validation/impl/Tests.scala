package de.dnpm.dip.rd.validation.impl


import scala.util.Random
import scala.util.chaining._
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.must.Matchers._
import org.scalatest.Inspectors._
import cats.{
  Applicative,
  Id
}
import de.ekut.tbi.generators.Gen
import de.dnpm.dip.rd.model.RDPatientRecord
import de.dnpm.dip.rd.validation.api.RDValidationService
import de.dnpm.dip.service.Data._
import de.dnpm.dip.service.validation.ValidationService.{
  Validate,
  Filter
}
import de.dnpm.dip.rd.gens.Generators._
import play.api.libs.json.Json.{ 
  toJson,
  prettyPrint
}



class Tests extends AsyncFlatSpec with Invalidators
{

  implicit val rnd: Random =
    new Random

  val record =
    Gen.of[RDPatientRecord].next
      .pipe(invalidate)

  lazy val serviceLoad =
    RDValidationService.getInstance

  lazy val service =
    serviceLoad.get


  "Loading RDValidationService" must "have worked" in {
    assert(serviceLoad.isSuccess)
  }


  "Validation of invalidated RDPatientRecord" must "have failed" in {

    for {
      outcome <- (service ! Validate(record))

      result <-
        outcome match {
          case Left(UnacceptableIssuesDetected(report)) =>
            toJson(report) pipe prettyPrint pipe println
            for {
              infos <- service ? Filter.empty
            } yield infos must not be (empty)

          case r => fail()
       }
    } yield result

  }

}
