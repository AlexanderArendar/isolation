import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.{head => _, _}
import akka.stream.ActorMaterializer
import upickle.default._
import scalatags.Text.all._


object Server {

  def generateSamples:Seq[Point] = Seq(Point(200, 200))

  var samples = generateSamples

  def cluster:Seq[Seq[Point]] = {
    Seq(neighbours(samples(0), samples.tail.toSet).toSeq ++ Seq(samples(0)))
  }

  def distance(p1:Point, p2:Point):Double = Math.sqrt(Math.pow(Math.abs(p1.x - p2.x), 2) + Math.pow(Math.abs(p1.y - p2.y), 2))

  def neighbours(p:Point, points:Set[Point]):Set[Point] = {
    for{
      point <- points
      if(distance(point, p) <= Settings.MINIMAL_DISTANCE)
    } yield point
  }

  def main(args: Array[String]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val boot =
      "Client.main(document.getElementById('ui'))"

    val skeleton =
      html(
        head(
          script(src:="/app-fastopt.js"),
          link(
            rel:="stylesheet",
            href:="/server.css"
          )
        ),
        body(
          onload:=boot,
          h2("Samples rendered below"),
          button(
            id:="process",
            "Process"
          ),
          div(raw("&nbsp;")),
          div(
            id:="ui",
            width:=Settings.WIDTH,
            height:=Settings.HEIGHT
          )
        )
      )

    val route =
      path("index") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, skeleton.render))
        }
      } ~
      getFromResourceDirectory("") ~
      path("samples"){
        complete(HttpEntity(ContentTypes.`application/json`, write(samples)))
      } ~
      path("add"){
        post{
          entity(as[String]){p =>
            samples = samples :+ upickle.default.read[Point](p)
            complete("")
          }
        }
      } ~
      path("process"){
        get{
          complete(HttpEntity(ContentTypes.`application/json`, write(cluster)))
        }
      }
    Http().bindAndHandle(route, "localhost", 8080)
  }
}
