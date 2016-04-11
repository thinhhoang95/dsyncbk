;(function($){
    clickToDetail();
    ctrlCate();
    showHideMenu();
    hoverProduct();
    setHeightItem();

    $.fn.list_ticker = function(options){
        var defaults = {
          speed:4000,
          effect:'slide'
        };
        
        var options = $.extend(defaults, options);
        
        return this.each(function(){
          
          var obj = $(this);
          var list = obj.children();
          list.not(':first').hide();
          
          setInterval(function(){
            
            list = obj.children();
            list.not(':first').hide();
            
            var first_li = list.eq(0)
            var second_li = list.eq(1)
            
            if(options.effect == 'slide'){
                first_li.slideUp();
                second_li.slideDown(function(){
                    first_li.remove().appendTo(obj);
                });
            } else if(options.effect == 'fade'){
                first_li.fadeOut(function(){
                    second_li.fadeIn();
                    first_li.remove().appendTo(obj);
                });
            }
          }, options.speed)
        });
      };  

       $('#slide-news').list_ticker({
            speed:2500,
            effect:'fade'
        });
})(window.jQuery);

function clickToDetail(){
    $(document).delegate("[data-url]", 'click', function(e){
        e.preventDefault();
        var url = $(this).attr('data-url');
        if (url && url.length){
            window.location.href = url;
        }
    });
};

function ctrlCate(){
	var mainList = $('.categories').find('li');

	mainList.each(function(){
		mainList.find('ul').hide();
		$(this).mouseenter(function(){
			$(this).find('ul').show();
		}).mouseleave(function(){
			$(this).find('ul').hide();
		});
	});
};

function showHideMenu(){
    var $menu = $('.main-menu-block'),
        $ctrl = $('.m-menu-control').find('a');
    
    $ctrl.on('click', function(e){
        e.preventDefault();

        var $this = $(this);
        if ($this.hasClass('active')){
            $this.removeClass('active');
            $menu.hide();
        }else{
            $this.addClass('active');
            $menu.show();
        }
    });
};

function hoverProduct(){
    $('.img-box-item').mouseenter(function(){
        $(this).find('.content-hover').stop().animate({
            "height": 270,
            "padding":"40px 20px"
        });

    }).mouseleave(function(){
        $(this).find('.content-hover').stop().animate({
            "height": 58,
            "padding":"10px 20px"
        });
    });
}

function setHeightItem(){
    var items = $('li.only-pic'),
        hItem = items.height();
    items.each(function(){
        $(this).find('img').css('height',hItem);
    });
}
