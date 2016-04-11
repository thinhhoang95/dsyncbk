// JavaScript Document

$(document).ready(function()
{
	$('#tblData').hide();
	$('#search').keyup(function(event)
	{
	 if (event.keyCode == '13') 
	 {		
	 	if($(this).val()=='')
		{$('#tblData').hide();}
		else {
	 		$('#tblData').show();
			searchTable($(this).val());			
		}
	 }

	});
});

function searchTable(inputVal)
{
	var table = $('#tblData');
	table.find('tr').each(function(index, row)
		{
			var allCells = $(row).find('td');
			if(allCells.length > 0)
			{
				var found = false;
				allCells.each(function(index, td)
				{
					var regExp = new RegExp(inputVal, 'i');
					if(regExp.test($(td).text()))
					{
						found = true;
						return false;
					}
				});
				if(found == true)$(row).show();else $(row).hide();
			}
		});
}