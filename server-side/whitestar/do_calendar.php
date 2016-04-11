<?php
include_once "simple_html_dom.php";
include_once "news.php";
header('Content-Type: application/json');
if(isset($_GET["mssv"])){
	$mssv=$_GET["mssv"];
	$url="http://www.aao.hcmut.edu.vn/image/data/Tra_cuu/xem_lt"; //URL of data source
	//$url="http://thinhhoang.pe.hu/whitestar/sample.htm";
	//$url="http://localhost/whitestar/sample.htm";
	
	/* IMPORTANT
	   MODIFY THIS PART ONLY */

	$semester="20143"; //Semester code for remote querying
	$signature="20143B"; //Signature of semester (for update notification)
	
	/* IMPORTANT
	   END OF CHANGABLE CODE. DO NOT ATTEMPT TO CHANGE ANYTHING LATER ON */
	
	$fields=array(
	"mssv"=>urlencode($mssv),
	"HOC_KY"=>urlencode($semester));
	$fields_string="";
	foreach($fields as $key=>$value) { $fields_string .= $key.'='.$value.'&'; }
	rtrim($fields_string, '&');
	//Initialize connection
	$ch=curl_init();
	//Configure connection
	curl_setopt($ch,CURLOPT_URL, $url);
	curl_setopt($ch,CURLOPT_POST, count($fields)); //POST params
	curl_setopt($ch,CURLOPT_POSTFIELDS, $fields_string);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true); //Silent output
	$result = curl_exec($ch); //Execute connection, result is now stored in $result
	curl_close($ch); //Close connection
	//Now parsing the content
	$html=str_get_html($result);
	$colindex=0;
	$count=0;
	foreach($html->find('font[color=#0080FF]') as $bluefont)
	{
		$colindex++;
		//echo $bluefont->innertext.";";
		switch($colindex)
		{
			case 2:
				$row["TenMH"]=$bluefont->innertext;
				$row["Loai"]="GiuaKy";
				$currentTenMH=$bluefont->innertext;
				break;
			case 4:
				if($bluefont->innertext!="/") $row["Ngay"]=$bluefont->innertext; else $row["GKNgay"]="";
				break;
			case 5:
				if($bluefont->innertext!="") $row["Gio"]=$bluefont->innertext; else $row["GKGio"]="";
				break;
			case 6:
				if($bluefont->innertext!="") $row["Phong"]=$bluefont->innertext; else $row["GKPhong"]="";
				$array[]=$row;
				unset($row);
				break;
			case 7:
				$row["TenMH"]=$currentTenMH;
				$row["Loai"]="CuoiKy";
				if($bluefont->innertext!="/") $row["Ngay"]=$bluefont->innertext; else $row["CKNgay"]="";
				break;
			case 8:
				if($bluefont->innertext!="") $row["Gio"]=$bluefont->innertext; else $row["CKGio"]="";
				break;
			case 9:
				if($bluefont->innertext!="") $row["Phong"]=$bluefont->innertext; else $row["CKPhong"]="";
				$colindex=0;
				$array[]=$row;
				unset($row);
				break;
		}
	}
	$output=array("signature"=>$signature,"data"=>$array,"developermessagesignature"=>$developermessagesignature);
	echo json_encode($output);
} else {
	echo "Invalid parameters provided. Exiting with code 4025.";
}
?>