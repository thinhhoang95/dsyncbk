<?php
include_once "simple_html_dom.php";
header('Content-Type: application/json');

$db=new mysqli("mysql.hostinger.vn","u969317228_share","123456","u969317228_share");
//$db=new mysqli("localhost","root","","whitestar");
$db->set_charset("utf8");

function listAvailableOptions($MaMH,$Loai,$Nhom,$mssv)
{
	global $db;
	$ret=null;
	$sql="SELECT whitestar_collab_calendar.*, CASE WHEN EXISTS (SELECT mssv FROM whitestar_collab_calendar_log_disable_button WHERE whitestar_collab_calendar_log_disable_button.upvoted_id=whitestar_collab_calendar.id AND mssv='$mssv') THEN 1 ELSE 0 END AS Upvoted FROM whitestar_collab_calendar WHERE MaMH='$MaMH' AND Nhom='$Nhom' AND Loai='$Loai' LIMIT 10";
	$result=$db->query($sql);
	while($row=$result->fetch_array(MYSQLI_ASSOC))
	{
		$retrow["Ngay"]=$row["Ngay"];
		$retrow["Gio"]=$row["Gio"];
		$retrow["Phong"]=$row["Phong"];
		$retrow["Vote"]=$row["Vote"];
		$retrow["Upvoted"]=$row["Upvoted"];
		$retrow["id"]=$row["id"];
		$ret[]=$retrow;
		unset($retrow);
	}
	return $ret;
}

function upvote($id,$mssv,$flag)
{
	global $db;
	if($flag==false)
	{
		$sql="INSERT INTO whitestar_collab_calendar_log (mssv, upvoted_id) VALUES ('$mssv','$id')"; // COLAB_LOG WILL DECIDE IF THE EXAM WILL BE AUTOMATICALLY DISPLAYED OR NOT!
		$db->query($sql);
	}
	$sql="INSERT INTO whitestar_collab_calendar_log_disable_button (mssv, upvoted_id) VALUES ('$mssv','$id')"; // COLAB_LOG_DISABLE_BUTTON IS WHERE TO CHECK IF USER HAS UPVOTED SOMETHING TO GRAY OUT THE OPTION TO UPVOTE AGAIN (DISABLE +1 BUTTON)
		$db->query($sql);
	$sql="UPDATE whitestar_collab_calendar SET Vote=Vote+1 WHERE id='$id'";
	$db->query($sql);
}

function addExam($MaMH, $Nhom, $Loai, $Ngay, $Gio, $Phong, $mssv, $flag)
{
	global $db;
	$sql="INSERT INTO whitestar_collab_calendar (MaMH, Nhom, Loai, Ngay, Gio, Phong) VALUES ('$MaMH','$Nhom','$Loai','$Ngay','$Gio','$Phong')";
	$result=$db->query($sql);
	$id=$db->insert_id;
	upvote($id,$mssv,$flag);
}

function unupvote($id,$mssv)
{
	global $db;
	$sql="DELETE FROM whitestar_collab_calendar_log WHERE mssv='$mssv' AND upvoted_id='$id'";
	$db->query($sql);
	$sql="UPDATE whitestar_collab_calendar SET Vote=Vote-1 WHERE id='$id'";
	$db->query($sql);
}

if(isset($_GET["action"]))
{
	$action=$_GET["action"];
	switch($action)
	{
		case "list":
			// List all available options
			$ret=listAvailableOptions($_GET["MaMH"],$_GET["Loai"],$_GET["Nhom"],$_GET["mssv"]);
			if(isset($ret))
			{
				echo json_encode($ret);
			} else {
				echo "[]";
			}
		break;
		case "add":
		// Add a new exam into Whitestar's knowledge base
		if(isset($_POST["donotlog"])) $flag=true; else $flag=false;
		addExam($_POST["MaMH"],$_POST["Nhom"],$_POST["Loai"],$_POST["Ngay"],$_POST["Gio"],$_POST["Phong"],$_POST["mssv"],$flag);
		break;
		case "upvote":
		// Upvote an already-existed exam
		if(isset($_GET["donotlog"])) $flag=true; else $flag=false;
		upvote($_GET["id"],$_GET["mssv"], $flag);
		break;
		case "unupvote":
		unupvote($_GET["id"],$_GET["mssv"]);
		break;
	}
} else {
	die("Please provide action for Whitestar. Exiting with code 4027.");
}
?>