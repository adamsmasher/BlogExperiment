import java.net.URLDecoder;

object FormEncoding {
  def parseFormEncoded(qs:String) : Map[String, String] = {
    return Map {
      qs split '&' map {
        (s:String) => s.splitAt(s indexOf '=') match {
          case (k, ev) => (k, URLDecoder.decode(ev drop 1))
        }
      } : _*
    }
  }
}
