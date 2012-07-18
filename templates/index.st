index(entries, next_page, previous_page) ::= <<
<html>
  <head>
    <title>blog</title>
  </head>
  <body>
    $entries:index_entry()$
    <p>
      $if(previous_page)$<a href="index?page=$previous_page$">Previous</a>$endif$
      $if(next_page)$<a href="index?page=$next_page$">Next</a>$endif$
    </p>
  </body>
</html>
>>
