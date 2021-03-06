package no.java.ems

import security.{JAASAuthenticator, User, Authenticator}
import storage.{MongoSetting, MongoDBStorage}
import unfiltered.request._
import unfiltered.filter.Plan
import javax.servlet.http.HttpServletRequest
import no.java.unfiltered._
import scala.util.Properties
import net.hamnaberg.json.collection.{ValueProperty, Query, Link, JsonCollection}
import unfiltered.filter.request.ContextPath
import unfiltered.response.{Pass, NotFound}

class Resources(override val storage: MongoDBStorage, auth: Authenticator) extends Plan with EventResources with ContactResources with AttachmentHandler with ChangelogResources {
  import auth._

  def this() = this(Resources.storage, JAASAuthenticator)

  def intent = {
    case Authenticated(f) => f((u: Option[User]) => pathMapper(u))
  }

  private def pathMapper(implicit u: Option[User]): Plan.Intent = {
    case req@ContextPath(_, Seg(Nil)) => handleRoot(req)
    case req@ContextPath(_, Seg("changelog" :: Nil)) => handleChangelog(req)
    case req@ContextPath(_, Seg("contacts" :: Nil)) => handleContactList(req)
    case req@ContextPath(_, Seg("contacts" :: id :: Nil)) => handleContact(id, req)
    case req@ContextPath(_, Seg("contacts" :: id :: "photo" :: Nil)) => handleContactPhoto(id, req)
    case req@ContextPath(_, Seg("events" :: Nil)) => handleEventList(req)
    case req@ContextPath(_, Seg("events" :: id :: Nil)) => handleEvent(id, req)
    case req@ContextPath(_, Seg("events" :: eventId :: "slots" :: Nil)) => handleSlots(eventId, req)
    case req@ContextPath(_, Seg("events" :: eventId :: "rooms" :: Nil)) => handleRooms(eventId, req)
    case req@ContextPath(_, Seg("events" :: eventId :: "publish" :: Nil)) => publish(eventId, req)
    case req@ContextPath(_, Seg("events" :: eventId :: "sessions" :: Nil)) => handleSessionList(eventId, req)
    case req@ContextPath(_, Seg("events" :: eventId :: "sessions" :: id :: Nil)) => handleSession(eventId, id, req)
    case req@ContextPath(_, Seg("events" :: eventId :: "sessions" :: sessionId :: "attachments" :: Nil)) => handleSessionAttachments(eventId, sessionId, req)
    case req@ContextPath(_, Seg("events" :: eventId :: "sessions" :: sessionId :: "speakers" :: Nil)) => handleSpeakers(eventId, sessionId, req)
    case req@ContextPath(_, Seg("events" :: eventId :: "sessions" :: sessionId :: "speakers" :: speakerId :: "photo" :: Nil)) => handleSpeakerPhoto(eventId, sessionId, speakerId, req)
    case req@ContextPath(_, Seg("binary" :: id :: Nil)) => handleAttachment(id, req)
    case _ => Pass
  }

  def handleRoot(req: HttpRequest[HttpServletRequest]) = {
    val builder = BaseURIBuilder.getBuilder(req)
    CollectionJsonResponse(JsonCollection(
      RequestURIBuilder.getBuilder(req).emptyParams().build(),
      List(
        Link(builder.segments("contacts").build(), "contact collection"),
        Link(builder.segments("events").build(), "event collection")
      ),
      Nil,
      List(Query(builder.segments("changelog").build(), "changelog", Some("Changelog"), List(
        ValueProperty("type", Some("Type")),
        ValueProperty("from", Some("From DateTime"))
      )))
    ))
  }
}

object Resources {
  object storage extends MongoDBStorage {
    val MongoSetting(db) = Properties.envOrNone("MONGOLAB_URI")
  }

  def apply(authenticator: Authenticator) = new Resources(storage, authenticator)

}
