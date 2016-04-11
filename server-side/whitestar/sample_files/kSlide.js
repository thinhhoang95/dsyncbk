;(function($, utils){
	if (!jQuery || !utils) return;

	var $doc = $(document),
		$html = $('html'),
		$win = $(window);

	var translate = utils.translate;

	function initEduSlide(){
		$('.edu-slider-block, .stutent-block').each(function(){
			var $container = $(this);

			var $preview = $container.children('.slider-preview'),
				$control = $container.children('.control-slider'),
				$navCtrl = $('<ul class="button-controls mobile"></ul>').appendTo($container),
				$prev = $control.find('.prev'),
				$next = $control.find('.next'),
				$pPrev = $prev.parent(),
				$pNext = $next.parent(),
				$slider1 = $preview.find('.slider-line-1').addClass('desktop'),
				$slider2 = $preview.find('.slider-line-2').addClass('desktop'),
				$sliderMobile = $('<ul class="slider slider-mobile">').html($slider1.html() + $slider2.html()).appendTo($preview),
				ctrls = [],
				$items1 = $slider1.children(),
				$items2 = $slider2.children();

			$sliderMobile.children('.only-pic').remove();

			var $itemsMobile = $sliderMobile.children(),
				$fItemMobile = $itemsMobile.eq(0),
				$fItem = $items1.eq(0),
				mgRigth = parseInt($fItem.css('marginRight'));

			var trans1 = 0,
				trans2 = 0,
				ctrlW = $control.outerWidth(true),
				noScreen = 1,
				curScreen = 0,
				curScreenMobile = 0,
				screenWidth = $preview.width(),
				slideWidth1 = getSlideWidth($items1),
				slideWidth2 = getSlideWidth($items2) - ctrlW,
				maxSlide1 = slideWidth1 * noScreen,
				maxSlide2 = slideWidth2 * noScreen,
				duration = 500,
				easing = "easeOutExpo";

			var wW = $win.width(),
				timeout = null,
				fW = $fItemMobile.outerWidth(),
				noShowItem = 1,
				noSceenMobile = $itemsMobile.length;

			var mobileView = {
				outerMargin: 4.375, //unit %
				itemWidth: 48.9726, //unit %
				betweenItem: 2.05479, //unit %
				defWidth: 141,
				defHeight: 77,
				ratio: 77/141,
				border: 2,
				noItemRows: 3
			};

			function update(){
				updatePreview();
				updateItem();
				updateSlider();
				updateControl();
			};

			function getSlideWidth(items){
				var sw = 0,
					tw = 0;
				for (var i = 0, l = items.length; i <= l; i++) {
					tw = sw + $(items[i]).outerWidth(true);
					if (tw > screenWidth) break;
					sw = tw;
				};
				return sw;
			};

			function updateItem(){
				if ($container.hasClass("mobile")){
					screenWidth = $preview.width();
					var iw = Math.round(screenWidth * (mobileView.itemWidth/100)) - mobileView.border,
						ih = Math.round(iw * mobileView.ratio),
						im = Math.round(screenWidth * (mobileView.betweenItem/100));

					$itemsMobile.css({
						width: iw,
						height: ih,
						marginRight: im,
						marginBottom: im
					});

					fW = $fItemMobile.outerWidth(true);
				}
			};

			function updateControl(){
				if ($container.hasClass("mobile")){
					var i = noShowItem,
						w = 0;

					var strHTML = "";
					for (; i < noSceenMobile; i++) {
						strHTML += '<li class="'+(i==(curScreenMobile + noShowItem)?"active":"")+'"><a href="#"></a></li>';
						w += 25;
					};
					$navCtrl.html(strHTML);
					if (w <= screenWidth){
						$navCtrl.width(w);
					}
					else{
						$navCtrl.width(Math.min(w/2, screenWidth));
					}

					ctrls = $navCtrl.children();
					ctrls.each(function(idx){
						var $ctrl = $(this);

						$ctrl.off('click')
							.on('click', ctrlClick($ctrl, idx));
					});
				}
			};

			function ctrlClick($ctrl, index){
				return function(e){
					e.preventDefault();
					e.stopPropagation();

					slide(index);
				}
			};

			function updatePreview(){
				$preview.removeAttr('style');
				if (!$container.hasClass("mobile")){
					mgRigth = parseInt($fItem.css('marginRight'));
					screenWidth = $preview.width() + mgRigth;
					ctrlW = $control.outerWidth(true) + mgRigth;
					slideWidth1 = getSlideWidth($items1);
					slideWidth2 = slideWidth1 - ctrlW;
				}
			};

			function updateSlider(){
				if ($container.hasClass("mobile")){
					var l = $itemsMobile.length,
						w = 0;
					noSceenMobile = Math.ceil(l/3);

					for (var i = 0; i < noSceenMobile; i++) {
						w += $($itemsMobile[i]).outerWidth(true);
					};

					var r = l%3;
					if (r == 1){
						$itemsMobile.eq((noSceenMobile-r)*2).css("margin-right", fW);
					}

					$sliderMobile.width(w);
					$preview.css('height', 'auto');
				}
				else{
					var i = $items1.length,
						j = $items2.length,
						w1 = 0,
						w2 = 0;

					while(--i >= 0){
						w1 += $($items1[i]).outerWidth(true);
					}

					while(--j >= 0){
						w2 += $($items2[j]).outerWidth(true);
					}

					$slider1.width(w1);
					$slider2.width(w2);

					noScreen = Math.round(w1/screenWidth);
					maxSlide1 = w1 - slideWidth1;
					maxSlide2 = w2 - slideWidth2;
				}
			};

			function slide(index){
				if ($container.hasClass("mobile")){
					var trans = index * fW;
					$sliderMobile.stop().animate({
						prop:-trans
					},{
						duration: duration,
						easing: easing,
						step: function(now, fx){
							translate($sliderMobile[0], now, 0);
						}
					});
					curScreenMobile = index;
				}
				else{
					trans1 = Math.min(index * slideWidth1, maxSlide1);
					trans2 = Math.min(index * slideWidth2, maxSlide2);

					$slider1.stop().animate({
						prop:-trans1
					},{
						duration: duration,
						easing: easing,
						step: function(now, fx){
							translate($slider1[0], now, 0);
						}
					});
					$slider2.stop().animate({
						prop:-trans2
					},{
						duration: duration,
						easing: easing,
						step: function(now, fx){
							translate($slider2[0], now, 0);
						}
					});
					curScreen = index;
				}
				status();
			};

			function status(){
				if ($container.hasClass('mobile')){
					ctrls.removeClass('active');
					ctrls.eq(curScreenMobile).addClass('active');
				}else{
					if (curScreen <= 0){
						$pNext.removeClass('hide').addClass('full');
						$pPrev.addClass('hide');
					}
					else if (curScreen >= noScreen-1){
						$pNext.addClass('hide');
						$pPrev.removeClass('hide').addClass('full');
					}
					else{
						$pNext.removeClass('hide').removeClass('full');
						$pPrev.removeClass('hide').removeClass('full');
					}
				}
			};

			function next(){
				var nextScreen = $container.hasClass("mobile") ? curScreenMobile + 1 : curScreen + 1;
				if (nextScreen >= ($container.hasClass("mobile") ? noSceenMobile : noScreen)) return;

				slide(nextScreen);
			};

			function prev(){
				var nextScreen = $container.hasClass("mobile") ? curScreenMobile - 1 : curScreen - 1;
				if (nextScreen < 0) return;

				slide(nextScreen);
			};

			function resize(){
				clearTimeout(timeout);
				timeout = setTimeout(function() {
					updateScreen();
					update();
					status();
				}, 200);
			};

			function updateScreen(){
				wW = $win.width();
				$container.removeClass("min").removeClass('mobile');

				if (wW < 720){
					$container.addClass("mobile");
				}
				if (wW < 418){
					$container.addClass("min");
				}
			};
			updateScreen();
			update();
			status();

			$next.on('click', function(e){
				e.preventDefault();
				e.stopPropagation();

				next();
			});

			$prev.on('click', function(e){
				e.preventDefault();
				e.stopPropagation();

				prev();
			});

			if ('ontouchstart' in window){
                var el = $preview[0];
                var moveX, moveY;
                var drag = false;
                el.addEventListener('touchstart',function(e){
                    moveX = e.touches[0].pageX;
                    moveY = e.touches[0].pageY;
                    drag = false;
                }, false);
                el.addEventListener('touchmove',function(e){
                    var deltaX = moveX - e.changedTouches[0].pageX;
                    if (Math.abs(deltaX) > 0){
                        if (Math.abs(deltaX) > 10){
                            e.preventDefault();
                        }
                    }
                    drag = true;
                }, false);
                el.addEventListener('touchend',function(e){
                    if(drag){
                        moveX = moveX - e.changedTouches[0].pageX;
                        moveY = moveY - e.changedTouches[0].pageY;
                        if(Math.abs(moveX)>80){
                            if(moveX<0) prev();
                            else next();
                        }
                    }
                    drag = false;
                }, false);
            }

			$win.on('resize.kSlide', resize);
			$win.on('orientationchange.kSlide', resize);
		});
	};

	$doc.ready(function(){
		initEduSlide();
	});

})(window.jQuery, window.Utils);
