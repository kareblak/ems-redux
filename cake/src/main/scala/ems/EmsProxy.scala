package ems

import unfiltered.request._
import unfiltered.response._
import unfiltered.filter.Plan
import dispatch._
import unfiltered.filter.request.ContextPath

/**
 * @author Erlend Hamnaberg<erlend.hamnaberg@arktekk.no>
 */

object EmsProxy extends Plan{
  val CollectionJson = "application/vnd.collection+json"

  def intent = {
    case ContextPath(_, "/ajax") & Params(p) => {
      p("href").headOption.map { href =>
        ContentType(CollectionJson) ~> ResponseString(Http(url(href) <:< Map("Accept" -> CollectionJson) as_str))
      }.getOrElse(NotFound)
    }
  }
}
