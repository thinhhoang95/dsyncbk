;(function($){
	if (!jQuery) return;

	var $html = $('html'),
		$doc = $(document);

	var isDevice = $html.hasClass('mobile') || $html.hasClass('table');

	function iSelectBox(){
		$('.selectbox-item-block').each(function(){
			var $sel = $(this);

			var $optSelected = $sel.children('.item-slected'),
				$optContainer = $sel.children('.item-list'),
				$opts = $optContainer.find("li > a"),
				maxHeight = $optContainer.height(),
				paddingTop = parseInt($optContainer.css('paddingTop'), 10),
				paddingBottom = parseInt($optContainer.css('paddingBottom'), 10),
				duration = 300,
				easing = "easeOutExpo",
				curText = $optSelected.html(),
				timeleave = null;


			if ($optContainer.is(":hidden")){
				$optContainer.show();
				maxHeight = $optContainer.height();
				setTimeout(function(){
					$optContainer.hide();
				}, 0);
			}


			function select_mouseenter(){
				clearTimeout(timeleave);
				$sel.addClass('open');
				show();
			};

			function select_mouseleave(){
				clearTimeout(timeleave);
				timeleave = setTimeout(function(){
					hide();
				}, 350);
			};

			function select($opt){
				return function(e){
					e.preventDefault();
					e.stopPropagation();

					curText = $opt.text();
					$optSelected.html(curText);
				};
			};

			function show(onComplete){
				$sel.addClass('open');
				$optContainer.stop().show().animate({
					height: maxHeight,
					paddingTop: paddingTop,
					paddingBottom: paddingBottom
				}, duration, easing, onComplete);
			};

			function hide(onComplete){
				$sel.removeClass('open');
				$optContainer.stop().animate({
					height: 0,
					paddingTop: 0,
					paddingBottom: 0
				}, duration, easing, function(){
					$(this).hide();

					typeof onComplete == 'function' && onComplete();
				});
			};

			if (isDevice){
				function sel_click(){
					$doc.off("click.kSelect");
					if ($sel.hasClass('open')){
						hide();
					}
					else{
						show();
						$doc.on("click.kSelect", function(e){
							if (!$(e.target).closest($sel).length){
								hide();
							}
						});
					}
				};

				$optSelected.on({
					"click": sel_click
				});
			}
			else{
				$sel.on({
					"mouseenter": select_mouseenter,
					"mouseleave": select_mouseleave
				});	
			}

			$.each($opts, function(){
				var $opt = $(this);

				$opt.on("click", select($opt));
			});
		});
	};

	$doc.ready(function(){
		iSelectBox();
	});

})(window.jQuery);
