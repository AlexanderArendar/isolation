import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import org.scalajs.dom.ext.Ajax
import scalatags.JsDom.all._
import org.scalajs.dom
import dom.{html, _}
import org.scalajs.dom.html.Button

import scalajs.concurrent.JSExecutionContext.Implicits.queue

@JSExportTopLevel("Client")
object Client {

  def clear(canvas:html.Canvas):Unit = {
    val renderer = document.getElementById("canvas").asInstanceOf[html.Canvas].getContext("2d").asInstanceOf[CanvasRenderingContext2D]
    renderer.fillStyle = "black"
    renderer.fillRect(0, 0, canvas.width, canvas.height)
  }

  def renderPointsWithColor(points:Seq[Point], color:String = "white"):Unit = {
    val c = document.getElementById("canvas").asInstanceOf[html.Canvas]
    val renderer = c.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
    renderer.fillStyle = color
    points foreach {p =>
      renderer.fillRect(p.x - 2, p.y - 2, 4, 4)
      println(p)
    }
  }

  def renderPoints(points:Seq[Point]):Unit = {
    val c = document.getElementById("canvas").asInstanceOf[html.Canvas]
    clear(c)
    renderPointsWithColor(points)
  }

  def refreshData:Unit = Ajax.get("/samples") foreach {req =>
    val points = upickle.default.read[Seq[Point]](req.responseText)
    renderPoints(points)
  }

  @JSExport
  def main(target:html.Div):Unit = {
    val c = canvas(
      id := "canvas"
    ).render
    target.appendChild(c)
    c.width = c.parentElement.clientWidth
    c.height = c.parentElement.clientHeight
    c.onclick = {e:MouseEvent =>
      val canvasLeft = c.getBoundingClientRect().left
      val canvasTop = c.getBoundingClientRect().top
      val x = e.clientX - canvasLeft
      val y = e.clientY - canvasTop
      Ajax.post("/add", upickle.default.write[Point](Point(x,y))).foreach(_ => refreshData)
    }
    val button = document.getElementById("process").asInstanceOf[Button]
    button.onclick = {_ =>
      Ajax.get("/process") foreach{req =>
        val firstGroup:Seq[Point] = upickle.default.read[Seq[Seq[Point]]](req.responseText).head
        renderPointsWithColor(firstGroup, "red")
      }
    }
    refreshData
  }
}
