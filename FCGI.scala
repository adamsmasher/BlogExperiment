import com.fastcgi;

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
            System.getProperty("SCRIPT_NAME"),
            FormEncoding.parseFormEncoded(
              System.getProperty("QUERY_STRING")));
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
}

class FCGIRequest(requestMethod:HTTPRequestMethod,
                  scriptName:String,
                  val fields:Map[String, String]) {
  def dispatch(handlers: Map[String, FCGIRequest => HTTPResponse])
    : HTTPResponse =
  {
    handlers find {case (r, handler) => this.scriptName matches r} match {
      case Some((r, handler)) => return handler(this);
      case None => return new HTTPResponse(
        HTMLMIME,
	    Array(HTTPStatusHeader(HTTP404Status)),
        "");
    }
  }

  def slurpBody() : Array[Byte] = {
    return requestMethod match {
      case GETMethod => return new Array(0);
      case POSTMethod(contentType, contentLength) =>
        val bodyBytes = new Array(contentLength) : Array[Byte];
        System.in.read(bodyBytes, 0, contentLength);
        return bodyBytes;
    }
  }
}

