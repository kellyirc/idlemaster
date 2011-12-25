<html>
<head>
</head>
<body>
<?php
$page = file_get_contents("players.dat");
echo $page;
var_dump(json_decode($json));
?>
</body>
</html>