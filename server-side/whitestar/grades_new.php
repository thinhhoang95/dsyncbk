<?php
include_once "simple_html_dom.php";
function registerUser($mssv)
{
	$db=new mysqli("mysql.hostinger.vn","u969317228_share","123456","u969317228_share");
	$db->query("INSERT INTO whitestar_user (whitestar_user_mssv) VALUES ('".$mssv."')");
}
header('Content-Type: application/json');
// echo "Some rubbush data";
if(isset($_GET["mssv"])){
	$mssv=$_GET["mssv"];
	registerUser($mssv);
	$url="http://www.aao.hcmut.edu.vn/image/data/Tra_cuu/xem_bd"; //URL of data source
	// $url="http://thinhhoang.pe.hu/whitestar/sample.htm";
	// $url="http://localhost/whitestar/sample.htm";
	// Prepare post data
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
	$count=0;
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
			$count++;
			$colindex=0;
			$array[]=$row;
			unset($row);
		}
	}
	$finalOutput["count"]="$count";
	
	// Find Total Credits and Average Score
	
	foreach($html->find('td[width=40%]') as $iterator) {
			if (strpos($iterator->plaintext, 'Tổng số tín chỉ :') !== false) {
				$finalOutput["totalCredits"]=str_replace('Tổng số tín chỉ :','',$iterator->plaintext);
				break;
			}
	}
	
	foreach($html->find('td[width=40%]') as $iterator) {
			if (strpos($iterator->plaintext, 'Điểm trung bình tích lũy:') !== false) {
				$finalOutput["averageScore"]=str_replace('Điểm trung bình tích lũy:','',$iterator->plaintext);
				break;
			}
	}
	$finalOutput["data"]=$array;
	
	echo json_encode($finalOutput);
	// echo "Some rubbish data!";
} else {
	echo "Invalid parameters provided. Exiting with code 4025.";
}
?>