import java.sql.Connection;

class DeletePostAction(db: Connection) {
  def deletePost(req: FCGIRequest): HTTPResponse = {
    val postId = for { 
      postIdStr <- req.fields.get("id")
      postId <- IntUtil.toInt(postIdStr)
    } yield postId;

    return postId match {
      case Some(postId) => doDeletePost(postId);
      case None         => BlogApp.badRequest();
    };
  }

  def redirectToAdmin() : HTTPResponse = {
    return new HTTPResponse(
      HTMLMIME,
      Array(),
      "<HTML>Redirect.</HTML>");
  }

  def doDeletePost(postId: Int) : HTTPResponse =
  {
    db.setAutoCommit(false);
    val deletePostSQL = db.prepareStatement("DELETE FROM post WHERE id = ?");
    val deleteCommentsSQL = db.prepareStatement(
      "DELETE FROM comment WHERE post_id = ?");
    deletePostSQL.setInt(1, postId);
    deleteCommentsSQL.setInt(1, postId);
    deleteCommentsSQL.executeUpdate();
    deletePostSQL.executeUpdate();
    db.commit();
    db.setAutoCommit(true);

    return redirectToAdmin();
  }
}
