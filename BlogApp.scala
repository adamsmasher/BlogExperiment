import org.stringtemplate.v4._;

object BlogApp extends FCGIHandler {
  val log = System.err;
  val db = DB.connect();

  val indexPage = new IndexPage(db);
  val postPage = new PostPage(db);
  val postCommentAction = new PostCommentAction(db);

  def main(args: Array[String]) = {
    for(req <- get_request()) {
      val response = req dispatch Map(
        "/(index)?" -> indexPage.index,
        "/post" -> postPage.post,
        "/post_comment" -> postCommentAction.postComment
      )
      System.out.println(response);
    }
  }
}
