import java.sql.Connection;
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
      case None                      => BlogApp.badRequest()
    };
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
    return for {
      row <- DB.executeQueryNonEmpty(getPostTitleSQL)
    } yield row.getString("title");
  }
}
