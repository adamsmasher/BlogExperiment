import java.sql.Connection;
import java.sql.SQLException;
import org.postgresql.util.PSQLState;

class PostPage(db: Connection) {
  val findPostSQL = db.prepareStatement(
    "SELECT * FROM post WHERE id = ?");
  
  def post(req: FCGIRequest): HTTPResponse = {
    val post = for {
      postIdStr <- req.fields.get("id")
      postId <- IntUtil.toInt(postIdStr)
      post <- findPost(postId)
    } yield post;
    return post match {
      case Some(post) => postResponse(post)
      case None       => postNotFound()
    };
  }

  def findPost(id:Int) : Option[Post] = {
    findPostSQL.setInt(1, id);
    try {
      val row = findPostSQL.executeQuery();
      row.next();
      return Some(Post.fromRow(row));
    } catch {
      case e:SQLException => {
	// The cursor will be in an invalid state after next()
	// if the result set is empty
	if(e.getSQLState() equals
	   PSQLState.INVALID_CURSOR_STATE.getState())
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

  def postNotFound() : HTTPResponse = {
    return new HTTPResponse(
      HTMLMIME,
      Array(),
      "<HTML>Not found.</HTML>");
  }

  def postResponse(post:Post) : HTTPResponse = {
    val template = Templates.get("post");

    template.add("post", post);

    return new HTTPResponse(
      HTMLMIME,
      Array(),
      template.render());
  }
}
