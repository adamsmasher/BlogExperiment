import org.stringtemplate.v4._;

object BlogApp extends FCGIHandler {
  val log = System.err;
  val db = DB.connect();

  val indexPage = new IndexPage(db);
  val postPage = new PostPage(db);
  val postCommentAction = new PostCommentAction(db);
  val adminPage = new AdminPage(db);
  val deletePostPage = new DeletePostPage(db);

  def main(args: Array[String]) = {
    for(req <- get_request()) {
      val response = req dispatch Map(
        "/(index)?" -> indexPage.index,
        "/post" -> postPage.post,
        "/post_comment" -> postCommentAction.postComment,
        "/admin" -> adminPage.admin,
        "/admin/delete_post" -> deletePostPage.confirm
      )
      System.out.println(response);
    }
  }
}
