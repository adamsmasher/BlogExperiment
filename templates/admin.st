admin(entries) ::= <<
<html>
  <head>
    <title>admin</title>
  </head>
  <body>
    <p><a href="/admin/new_post">New Post</a></p>
    <table>
      $entries:admin_entry()$
    </table>
  </body>
</html>
>>
