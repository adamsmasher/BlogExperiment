import com.fastcgi;
import scala.collection.immutable.HashMap;

trait FCGIHandler {
  def get_request() : Iterator[FCGIRequest] = {
    var accepted = false;
    return new Iterator[FCGIRequest] {
      def next() : FCGIRequest = {
        // we pump in the next request by calling hasNext()
        // no need to do this if we've already done it
        if(!accepted) {
          // pump in the next request
          if(!hasNext())
            sys.error("no more requests");
        }
        val req = new FCGIRequest(
          HTTPRequestMethodParser.fromString(
            System.getProperty("REQUEST_METHOD")),
            System.getProperty("PATH_INFO"),
            parseQueryString(System.getProperty("QUERY_STRING")));
        accepted = false;
        return req;
      }
      def hasNext(): Boolean = {
        if(accepted) {
          return true;
        }
        else {
          val result = new com.fastcgi.FCGIInterface().FCGIaccept();
          if(result >= 0) {
            accepted = true;
            return true;
          }
          else
            return false;
        }
      }
    }
  }

  def parseQueryString(qs: String) : Map[String, String] = {
    return new HashMap();
  }
}

class FCGIRequest(requestMethod:HTTPRequestMethod,
                  pathInfo:String,
                  queryString:Map[String, String]) {
  def dispatch(handlers: PartialFunction[String, FCGIRequest => HTTPResponse])
    : HTTPResponse =
  {
    if(handlers isDefinedAt this.pathInfo)
      handlers(this.pathInfo)(this);
    else
      return HTTP404Response;
  }
}

abstract class HTTPResponse;
case object HTTP404Response extends HTTPResponse {
  override def toString() : String = {
    return "Status: 404 Not Found\n"
           "Content-type: text/html\n"
           "Content-Length: 0\n\n";
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
case object JSONMIME extends MIMEType;
case object HTMLMIME extends MIMEType;
case object JavascriptMIME extends MIMEType;
case object XMLMime extends MIMEType;

object MIMETypeParser {
  def fromString(s:String) : MIMEType = {
    return s match {
      case "application/json" => JSONMIME;
      case "text/html" => HTMLMIME;
      case "application/javascript" => JavascriptMIME;
      case "text/xml" => XMLMime;
    }
  }
}
