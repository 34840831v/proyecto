<?php
	//archivo que controla si el usuario y contraseña que envia android existe o no,
	//si no existe entonces $res=0 y si existe &res=1, en este último caso, esto lo  
	//mandamos a al array $datos que es el que enviamos a android como un objeto json

	$hostname ="localhost";  //nuestro servidor de BD
	$database ="id9564461_proyecto"; //Nombre de nuestra base de datos
	$username ="id9564461_javi"; //Nombre de usuario de nuestra base de datos 
	$password ="123456";   //Contraseña de nuestra base de datos		

	$usu=$_REQUEST['usu'];
	
	// intentar conectar al servidor con el usuario y contraseña anteriores
	$cnx = new mysqli($hostname,$username,$password, $database);
	// ejecutamos el select y lo almacenamos en la variable $res
	$res=$cnx->query("select * from tabla_usuarios where usuario='$usu'");
	// el resultado de $res lo ponemos en un array para poder enviarlo en el json
	$datos=array();
	//llenamos el array con los datos de la variable $res
	foreach ($res as $row){ //lo que esta en $res lo pasamos a una fila nueva
		$datos[]=$row; //y lo mandamos a $datos
	}
	//envio los datos en un objeto json, el cual me permitira la comunicación entre dos programas
	echo json_encode($datos);//el objeto json lo retorno hacia el android
    mysqli_close($cnx);
?>


