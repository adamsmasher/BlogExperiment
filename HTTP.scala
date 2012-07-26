class HTTPResponse(contentType: MIMEType,
		   additionalHeaders: Array[HTTPHeader],
		   body: String)
{
  override def toString() : String = {
    return (contentType.toString() + "\n" +
            additionalHeaders.mkString("\n") + "\n" +
            body);
  }
}

abstract class HTTPHeader {
  def name() : String;
  def value() : String;
  override def toString() : String = {
    return name() + ":" + value();
  }
}

case class HTTPStatusHeader(status: HTTPStatus)
     extends HTTPHeader
{
  override def name() : String = { return "Status" };
  override def value() : String = { return status.toString(); }
}

abstract class HTTPStatus;
case object HTTP404Status extends HTTPStatus {
  override def toString() : String = { return "404 Not Found"; }
}
case object HTTP200Status extends HTTPStatus {
  override def toString() : String = { return "200 OK" }
}

case class HTTPContentTypeHeader(contentType: MIMEType)
	 extends HTTPHeader
{
  override def name() : String = { return "Content-type"; }
  override def value() : String = {
    return contentType.toString();
  }
}

abstract class HTTPRequestMethod;
case object GETMethod extends HTTPRequestMethod;
case class POSTMethod(contentType:MIMEType, contentLength:Int)
     extends HTTPRequestMethod;

object HTTPRequestMethodParser {
  def fromString(s:String) : HTTPRequestMethod = {
    return s match {
      case "GET" => GETMethod;
      case "POST" => POSTMethod(
        MIMETypeParser.fromString(System.getProperty("CONTENT_TYPE")),
        Integer.parseInt(System.getProperty("CONTENT_LENGTH")))
     }
  }
}

abstract class MIMEType;
case object JSONMIME extends MIMEType {
  override def toString() : String = { return "application/json"; }
}
case object HTMLMIME extends MIMEType {
  override def toString() : String = { return "text/html"; }
}
case object JavascriptMIME extends MIMEType {
  override def toString() : String = {
    return "application/javascript";
  }
}
case object XMLMime extends MIMEType {
  override def toString() : String = { return "text/xml"; }
}
case object FormURLEncodedMIME extends MIMEType {
  override def toString() : String = {
    return "application/x-www-form-urlencoded";
  }
}

object MIMETypeParser {
  def fromString(s:String) : MIMEType = {
    return s match {
      case "application/json" => JSONMIME;
      case "text/html" => HTMLMIME;
      case "application/javascript" => JavascriptMIME;
      case "text/xml" => XMLMime;
      case "application/x-www-form-urlencoded" => FormURLEncodedMIME;
    }
  }
}
