package net.eamelink.swaggerkit.play2

import net.eamelink.swaggerkit._
import net.eamelink.swaggerkit.SimpleTypes._
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import play.api.libs.json._

class WritersSpec extends Specification with SampleApiDocumentation {
  "The JSON output for a resource discovery page" should {

    val json = Writers.ApiDocumentationWriter.writes(apiDoc)

    "contain the base path" in {
      (json \ "basePath").as[String] must_== apiDoc.basePath
    }

    "contain the swagger version" in {
      (json \ "swaggerVersion").as[String] must_== apiDoc.swaggerVersion
    }

    "contain the api version" in {
      (json \ "apiVersion").as[String] must_== apiDoc.apiVersion
    }

    "list the proper number of apis" in {
      (json \ "apis").as[List[JsValue]] must have size (1)
    }

    "list the resource declaration path for a resource" in {
      ((json \ "apis")(0) \ "path").as[String] must_== apiDoc.apis(0).path
    }

    "list the description for a resource" in {
      ((json \ "apis")(0) \ "description").as[String] must_== apiDoc.apis(0).description
    }
  }
}

trait SampleApiDocumentation extends Scope with SchemaBuilder {
  lazy val apiDoc = ApiDocumentation(
    basePath = "http://api.example.com/",
    swaggerVersion = "1.1-SNAPSHOT",
    apiVersion = "1",
    apis = List(ResourceDeclaration(
      path = "/albums.{format}",
      description = "Operations on Albums",
      resourcePath = "/albums",
      basePath = "http://api.example.com/",
      swaggerVersion = "1.1-SNAPSHOT",
      apiVersion = "1",
      apis = List(
        albumsApi, albumApi),
      models = Map("Album" -> albumSchema))))

  lazy val albumsApi = Api("/albums") describedBy "An albums API" withOperations (listAlbums, createAlbum)
  lazy val albumApi = Api("/album/{albumId}") describedBy "An album API" withOperations (showAlbum, updateAlbum, deleteAlbum)

  lazy val listAlbums = Operation("listAlbums", GET, "List albums") takes (
    QueryParam("query", String) is "Filter by name",
    QueryParam("orderBy", String) is "The sort field. Defaults to 'id'" withValues ("id", "title")) // TODO: Maybe add sample data where this is populated by an Enumeration?

  lazy val createAlbum = Operation("createAlbum", POST, "Create a new album") takes (
    BodyParam(albumSchema))

  lazy val showAlbum = Operation("showAlbum", GET, "Show an album") takes (
    PathParam("albumId", String) is "The album id") note
    "This is just a sample note"

  lazy val updateAlbum = Operation("updateAlbum", PUT, "Update an album") takes () // TODO

  lazy val deleteAlbum = Operation("deleteAlbum", DELETE, "Delete an album") takes () // TODO

  lazy val albumSchema = Schema("Album") has (
    "id" -> Integer,
    "title" -> String,
    "photos" -> Array(photoSchema))

  lazy val photoSchema = Schema("Photo") has (
    "id" -> Integer,
    "title" -> String)
}