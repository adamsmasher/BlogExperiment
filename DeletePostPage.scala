import java.sql.Connection;
import java.sql.SQLException;
import org.postgresql.util.PSQLState;
import scala.reflect.BeanProperty;

class DeletePostPage(db: Connection) {
  val getPostInfoSQL = db.prepareStatement(
    "SELECT title FROM post WHERE id = ?");
  
  def confirm(req: FCGIRequest): HTTPResponse = {
    val postInfo = for {
      postIdStr <- req.fields.get("id")
      postId <- IntUtil.toInt(postIdStr)
      postInfo <- getPostInfo(postId)
    } yield postInfo;
    return postInfo match {
      case Some(postInfo) => confirmResponse(postInfo)
      case None           => badRequest()
    };
  }

  def badRequest() : HTTPResponse = {
    return new HTTPResponse(
      HTMLMIME,
      Array(),
      "<HTML>Bad request.</HTML>");
  }

  def confirmResponse(postInfo:PostInfo) : HTTPResponse = {
    val template = Templates.get("delete_post");
    
    template.add("id", postInfo.id);
    template.add("title", postInfo.title);

    return new HTTPResponse(
      HTMLMIME,
      Array(),
      template.render());
  }

  def getPostInfo(postId:Int) : Option[PostInfo] = {
    getPostInfoSQL.setInt(1, postId);
    try {
      val row = getPostInfoSQL.executeQuery();
      row.next();
      return Some(new PostInfo(postId, row.getString("title")));
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

  class PostInfo(@BeanProperty val id:Int, @BeanProperty val title:String) {
  }
}


