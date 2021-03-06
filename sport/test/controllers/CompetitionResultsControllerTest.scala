package test

import play.api.test._
import play.api.test.Helpers._
import org.scalatest.Matchers
import org.scalatest.FlatSpec
import common.UsesElasticSearch

class CompetitionResultsControllerTest extends FlatSpec with Matchers with UsesElasticSearch {
  
  val url = "/football/competition/premierleague/results/2012/oct/20"
  val callbackName = "aFunction"
  
  "Competition Results Controller" should "200 when content type is competition results" in Fake {
    val result = football.controllers.CompetitionResultsController.renderFor("2012", "oct", "20", "premierleague")(TestRequest())
    status(result) should be(200)
  }

  it should "return JSONP when callback is supplied to competition results" in Fake {
    val fakeRequest = FakeRequest(GET, s"${url}?callback=$callbackName")
      .withHeaders("host" -> "localhost:9000")
      
    val result = football.controllers.CompetitionResultsController.renderFor("2012", "oct", "20", "premierleague")(fakeRequest)
    status(result) should be(200)
    header("Content-Type", result).get should be("application/javascript; charset=utf-8")
    contentAsString(result) should startWith(s"""${callbackName}({\"config\"""")
  }

  it should "return JSON when .json format is supplied to competition results" in Fake {
    val fakeRequest = FakeRequest(GET, s"${url}.json")
      .withHeaders("host" -> "localhost:9000")
      .withHeaders("Origin" -> "http://www.theorigin.com")
      
    val result = football.controllers.CompetitionResultsController.renderFor("2012", "oct", "20", "premierleague")(fakeRequest)
    status(result) should be(200)
    header("Content-Type", result).get should be("application/json; charset=utf-8")
    contentAsString(result) should startWith("{\"config\"")
  }
  
}