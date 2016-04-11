<?php
include_once "simple_html_dom.php";
function registerUser($mssv)
{
	$db=new mysqli("mysql.hostinger.vn","u969317228_share","123456","u969317228_share");
	$db->query("INSERT INTO whitestar_user (whitestar_user_mssv) VALUES ('".$mssv."')");
}
header('Content-Type: application/json');
if(isset($_GET["mssv"])){
	$mssv=$_GET["mssv"];
	//registerUser($mssv);
	$url="http://www.aao.hcmut.edu.vn/image/data/Tra_cuu/xem_tkb"; //URL of data source
	//$url="http://localhost/whitestar/sample_timetable.html";
	//$url="http://localhost/whitestar/sample.htm";
	//Prepare post data
	$fields=array(
	"mssv"=>urlencode($mssv),
	"HOC_KY"=>urlencode("20152"));
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
	foreach($html->find('font[color=#0080FF]') as $bluefont)
	{
		$colindex++;
		//echo $bluefont->innertext.";";
		switch($colindex)
		{
			case 2:
				$row["TenMH"]=$bluefont->innertext;
				break;
			case 5:
				$row["Nhom"]=$bluefont->innertext;
				break;
			case 7:
				$row["Thu"]=$bluefont->innertext;
				break;
			case 8:
				$tietArray=explode("-",$bluefont->innertext);
				$row["TietTu"]=$tietArray[0];
				$row["TietDen"]=$tietArray[1];
				break;
			case 9:
				$row["Phong"]=$bluefont->innertext;
				break;
		}
		//$row[]=$bluefont->innertext;
		if($colindex==10)
		{
			//echo "<br>";
			$colindex=0;
			if($row["TietTu"]!="") $array[]=$row;
			unset($row);
		}
	}
	echo json_encode($array);
} else {
	echo "Invalid parameters provided. Exiting with code 4025.";
}
?>