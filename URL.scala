object QueryString {
  def parseQueryString(qs:String) : Map[String, String] = {
    return Map {
      qs split '&' map {
	(s:String) => s.splitAt(s indexOf '=') match {
	  case (k, ev) => (k, ev drop 1)
	}
      } : _*
    }
  }
}
