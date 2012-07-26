import java.sql.Connection;
import java.sql.SQLException;

class PostCommentAction(db: Connection) {
  var postCommentSQL = db.prepareStatement(
    "INSERT INTO comment (post_id, title, contents) VALUES (?, ?, ?)");

  def postComment(req: FCGIRequest): HTTPResponse = {
    val bodyFields = FormEncoding.parseFormEncoded(
        new String(req.slurpBody(), "UTF-8"));

    val params = for { 
      postIdStr <- req.fields.get("post_id")
      postId <- IntUtil.toInt(postIdStr)
      commentTitle <- bodyFields.get("comment_title")
      commentBody <- bodyFields.get("comment_body")
    } yield (postId, commentTitle, commentBody);

    return params match {
      case Some((postId, commentTitle, commentBody)) =>
        doPostComment(postId, commentTitle, commentBody)
      case None                                      => BlogApp.badRequest()
    };
  }

  def doPostComment(postId: Int, commentTitle: String, commentBody: String) :
    HTTPResponse =
  {
    postCommentSQL.setInt(1, postId);
    postCommentSQL.setString(2, commentTitle);
    postCommentSQL.setString(3, commentBody);
    try {
        postCommentSQL.executeUpdate();
    } catch {
      case e:SQLException => {
	    BlogApp.log.println(e.getSQLState());
        BlogApp.log.println(e);
        return BlogApp.badRequest();
      }
    }

    return HTTPResponse.seeOther("/post?id="+postId);
  }
}
