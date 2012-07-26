delete_post(id, title) ::= <<
<html>
  <head>
    <title>Delete Post</title>
  </head>
  <body>
    <h1>Are you sure you want to delete this post?</h1>
    <h2>$title$</h2>
    <p>
      <form action="do_delete_post?id=$id$" method="delete">
        <input type="submit" value="Delete" />
      </form>
    <a href="/admin">Don't Delete</a>
    </p>
  </body>
</html>
>>
