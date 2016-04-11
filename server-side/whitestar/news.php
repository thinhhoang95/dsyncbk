<?php
$developermessagesignature="8"; //Developer message signature is different for each different messasge
$developermessage="Học kỳ mới bắt đầu từ thứ hai, 17/8. Đã cập nhật thời khóa biểu cho học kỳ mới, các bạn vui lòng mở thời khóa biểu của ứng dụng để tải về máy."; //Content, do not use rich HTML
if(isset($_GET["echo"]))
{
	echo $developermessage;
}
?>