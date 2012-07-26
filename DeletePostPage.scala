import java.sql.Connection;
import java.sql.SQLException;
import org.postgresql.util.PSQLState;
import scala.reflect.BeanProperty;

class DeletePostPage(db: Connection) {
  val getPostTitleSQL = db.prepareStatement(
    "SELECT title FROM post WHERE id = ?");
  
  def confirm(req: FCGIRequest): HTTPResponse = {
    val postInfo = for {
      postIdStr <- req.fields.get("id")
      postId <- IntUtil.toInt(postIdStr)
      postTitle <- getPostTitle(postId)
    } yield (postId, postTitle);
    return postInfo match {
      case Some((postId, postTitle)) => confirmResponse(postId, postTitle)
      case None                    => badRequest()
    };
  }

  def badRequest() : HTTPResponse = {
    return new HTTPResponse(
      HTMLMIME,
      Array(),
      "<HTML>Bad request.</HTML>");
  }

  def confirmResponse(postId:Int, postTitle:String) : HTTPResponse = {
    val template = Templates.get("delete_post");
    
    template.add("id", postId);
    template.add("title", postTitle);

    return new HTTPResponse(
      HTMLMIME,
      Array(),
      template.render());
  }

  def getPostTitle(postId:Int) : Option[String] = {
    getPostTitleSQL.setInt(1, postId);
    try {
      val row = getPostTitleSQL.executeQuery();
      row.next();
      return Some(row.getString("title"));
    } catch {
      case e:SQLException => {
        // The cursor will be in an invalid state after next()
        // if the result set is empty
        if(e.getSQLState() equals PSQLState.INVALID_CURSOR_STATE.getState())
        {
          return None;
        }
        else {
          BlogApp.log.println(e.getSQLState());
          BlogApp.log.println(e);
          throw e;
        }
      }
    }
  }
}
