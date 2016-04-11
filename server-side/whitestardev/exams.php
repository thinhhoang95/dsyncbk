<?php
include_once "simple_html_dom.php";
// include_once "news.php";
header('Content-Type: application/json');

function databaseLookup($MaMH, $Loai, $Nhom, $mssv)
{
	$db=new mysqli("mysql.hostinger.vn","u969317228_share","123456","u969317228_share");
	// $db=new mysqli("localhost","root","","whitestar");
	$db->set_charset("utf8");
	
	$sql="SELECT * FROM whitestar_collab_calendar_log INNER JOIN whitestar_collab_calendar ON whitestar_collab_calendar_log.upvoted_id=whitestar_collab_calendar.id WHERE MaMH='$MaMH' AND Loai='$Loai' AND Nhom='$Nhom' AND mssv='$mssv' ORDER BY whitestar_collab_calendar_log.id DESC LIMIT 1";
	$result=$db->query($sql);
	
	if($result->num_rows!=0)
	{
		// User has upvoted something, show that!
		$row=$result->fetch_array(MYSQLI_ASSOC);
		$ret["Loai"]=$row["Loai"];
		$ret["Ngay"]=$row["Ngay"];
		$ret["Gio"]=$row["Gio"];
		$ret["Phong"]=$row["Phong"];
	} else {
		// If user hasn't upvoted anything
		$sql="SELECT * FROM whitestar_collab_calendar WHERE MaMH='$MaMH' AND Loai='$Loai' AND Nhom='$Nhom' ORDER BY Vote DESC,id DESC LIMIT 1";
		$result=$db->query($sql);
		if($result->num_rows!=0)
		{
			$row=$result->fetch_array(MYSQLI_ASSOC);
			$ret["Loai"]=$row["Loai"];
			$ret["Ngay"]=$row["Ngay"];
			$ret["Gio"]=$row["Gio"];
			$ret["Phong"]=$row["Phong"];
		} else
		{
			$ret=null;
		}
	}
	return $ret;
}

// Takes input like "27/07"
// and outputs "2016-07-27"
function standardizeDate($strDate)
{
	
	/** IMPORTANT: MODIFY THESE VALUES ACOORDING TO THE CURRENT SEMESTER **/
	
	$REFERENCEMONTH = 9; // 9 FOR 1ST SEMESTER, 1 FOR 2ND SEMESTER AND 5 FOR 3RD SEMESTER
	$REFERENCEYEAR = 2015; // START OF THE SEMESTER
	
	$aDate = explode("/",$strDate);
	$day=$aDate[0];
	$month=$aDate[1];
	if($month<$REFERENCEMONTH) $year = $REFERENCEYEAR; else $year = $REFERENCEYEAR+1;
	
	$oDate = new DateTime();
	$oDate->setDate($year,$month,$day);
	return $oDate->format("Y-m-d");
}

function standardizeTime($strTime)
{
	$aTime = explode("g",$strTime);
	$hour=$aTime[0];
	$minute=$aTime[1];
	$oTime = new DateTime();
	$oTime->setTime($hour,$minute,0);
	return $oTime->format("H:i:s");
}

if(isset($_GET["mssv"])){
	$mssv=$_GET["mssv"];
	// $url="http://www.aao.hcmut.edu.vn/image/data/Tra_cuu/xem_lt"; //URL of data source
	$url = "http://thinhhoang.pe.hu/whitestardev/sample_exam.htm";
	
	/* IMPORTANT
	   MODIFY THIS PART ONLY */

	$semester="20151"; //Semester code for remote querying
	
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
			case 1:
				$MaMH = $bluefont->innertext;
				$row["MaMH"]=$MaMH;
				break;
			case 2:
				$row["TenMH"]=$bluefont->innertext;
				$row["Loai"]="GiuaKy";
				$currentTenMH=$bluefont->innertext;
				break;
			case 3:
				$Nhom=$bluefont->innertext;
				$row["Nhom"]=$Nhom;
				break;
			case 4:
				if($bluefont->innertext!="/") 
				{
					$row["Ngay"]=standardizeDate($bluefont->innertext); 
				} else 
				{
					$row["Official"]="0";
					// Lookup in the database to see if there's something
					$ret=databaseLookup($MaMH,"GiuaKy",$Nhom,$mssv);
					if($ret==null) {$row["Ngay"]=""; $row["Gio"]=""; $row["Phong"]=""; } else {
						$row["Ngay"]=$ret["Ngay"];
						$row["Gio"]=$ret["Gio"];
						$row["Phong"]=$ret["Phong"];
					}
				}
				break;
			case 5:
				if($bluefont->innertext!="") $row["Gio"]=standardizeTime($bluefont->innertext);
				break;
			case 6:
				if($bluefont->innertext!="") $row["Phong"]=$bluefont->innertext;
				if (!isset($row["Official"])) $row["Official"]="1";
				$array[]=$row;
				unset($row);
				break;
			case 7: // Here!
				$row["MaMH"]=$MaMH;
				$row["TenMH"]=$currentTenMH;
				$row["Loai"]="CuoiKy";
				if($bluefont->innertext!="/") $row["Ngay"]=standardizeDate($bluefont->innertext); else
				{
					$row["Official"]="0";
					$ret=databaseLookup($MaMH,"CuoiKy",$Nhom,$mssv);
					if($ret==null) {$row["Ngay"]=""; $row["Gio"]=""; $row["Phong"]="";} else {
						$row["Ngay"]=$ret["Ngay"];
						$row["Gio"]=$ret["Gio"];
						$row["Phong"]=$ret["Phong"];
					}
				}
				break;
			case 8:
				if($bluefont->innertext!="") $row["Gio"]=standardizeTime($bluefont->innertext);
				break;
			case 9:
				if($bluefont->innertext!="") $row["Phong"]=$bluefont->innertext;
				$row["Nhom"]=$Nhom;
				if (!isset($row["Official"])) $row["Official"]="1";
				$colindex=0;
				$array[]=$row;
				unset($row);
				break;
		}
	}
	$output=array("data"=>$array);
	echo json_encode($array);
} else {
	echo "Invalid parameters provided. Exiting with code 4025.";
}
?>