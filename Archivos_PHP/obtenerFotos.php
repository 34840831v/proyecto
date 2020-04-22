<?php

	$hostname ="localhost";  //nuestro servidor de BD
	$database ="id9564461_proyecto"; //Nombre de nuestra base de datos
	$username ="id9564461_javi";       //Nombre de usuario de nuestra base de datos 
	$password ="guiaTuristica@2020";   //Contraseña de nuestra base de datos

	// 	VARIABLE QUE ALMACENA LA CONEXION A LA DB
	$mysqli = new mysqli($hostname,$username,$password, $database);	

    if($mysqli) {
        $sql = "SELECT * FROM tabla_fotos";
        $query = $mysqli->query($sql);
        $data = array();

        while($r = $query->fetch_assoc()) {
            $data[] = $r;
        }

        echo json_encode(array("Fotos" => $data));
	}
?>