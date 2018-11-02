case class Point(x:Double, y:Double)
object Point{
  implicit def serializer = upickle.default.macroRW[Point]
}

object Settings{
  val WIDTH = 1000
  val HEIGHT = 500
  val MINIMAL_DISTANCE = 50d
}