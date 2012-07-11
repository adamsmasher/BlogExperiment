index(posts, next_page) ::= <<
<html>
  <head>
    <title>blog</title>
  </head>
  <body>
    $posts:post()$
    <p>
      $if(next_page)$<a href="index?page=$next_page$">Next</a>$endif$
    </p>
  </body>
</html>
>>
