<?php
include_once "simple_html_dom.php";
header('Content-Type: application/json');
if(isset($_GET["mssv"])){
	$mssv=$_GET["mssv"];
	$url="http://www.aao.hcmut.edu.vn/image/data/Tra_cuu/xem_bd"; //URL of data source
	//$url="http://thinhhoang.pe.hu/whitestar/sample.htm";
	//Prepare post data
	$fields=array(
	"mssv"=>urlencode($mssv),
	"HOC_KY"=>urlencode("d.hk_nh is not NULL"));
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
			case 1:
				$row["MaMH"]=$bluefont->innertext;
				break;
			case 2:
				$row["TenMH"]=$bluefont->innertext;
				break;
			case 3:
				$row["Nhom"]=$bluefont->innertext;
				break;
			case 4:
				$row["SoTC"]=$bluefont->innertext;
				break;
			case 5:
				$row["DiemKT"]=$bluefont->innertext;
				break;
			case 6:
				$row["DiemThi"]=$bluefont->innertext;
				break;
			case 7:
				$row["DiemTK"]=$bluefont->innertext;
				break;
		}
		//$row[]=$bluefont->innertext;
		if($colindex==7)
		{
			//echo "<br>";
			$colindex=0;
			$array[]=$row;
			unset($row);
			if(count($array)==25) break;
		}
	}
	echo json_encode($array);
} else {
	echo "Invalid parameters provided. Exiting with code 4025.";
}
?>