package model.commercial.jobs

import scala.xml.Elem
import conf.{Switches, CommercialConfiguration}
import model.commercial.{OptString, XmlAdsApi}
import org.joda.time.format.DateTimeFormat
import org.apache.commons.lang.StringEscapeUtils.unescapeHtml

object JobsApi extends XmlAdsApi[Job] {

  protected val switch = Switches.JobFeedSwitch

  protected val adTypeName = "Jobs"

  override protected val characterEncoding = "utf-8"

  // url changes daily so cannot be val
  protected def url = {
    val feedDate = DateTimeFormat.forPattern("yyyy-MM-dd").print(System.currentTimeMillis)
    val urlTemplate = CommercialConfiguration.getProperty("jobs.api.url.template")
    urlTemplate map (_ replace("yyyy-MM-dd", feedDate))
  }

  override protected val loadTimeout = 30000

  override def cleanResponseBody(body: String) = body.dropWhile(_ != '<')

  def parse(xml: Elem): Seq[Job] = {
    (xml \ "Job") map {
      job =>
        Job(
          (job \ "JobID").text.toInt,
          (job \ "JobTitle").text,
          unescapeHtml((job \ "ShortJobDescription").text),
          (job \ "RecruiterName").text,
          OptString((job \ "RecruiterLogoURL").text),
          ((job \ "Sectors" \ "Sector") map (_.text.toInt)).toSeq
        )
    }
  }
}
