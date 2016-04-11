;(function(ua){
	var u = ua.toLowerCase();

	function is(s){
		return u.indexOf(s) > -1;
	};

	var isTablet = is('tablet') || is('ipad');

	var html = document.documentElement,
		arr = [
			isTablet ? 'tablet' : '',
			is('mobile') ? 'mobile' : ''
		],
		cl = arr.join(' ');

	// html.className += ' ' + cl;
	var viewport = document.createElement('meta');
	viewport.setAttribute('name', 'viewport');
	if (isTablet){
		viewport.setAttribute('content', 'width=1280');
	}
	else if (is('mobile')){
		viewport.setAttribute('content', 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0');
	}
	
	document.getElementsByTagName('head')[0].appendChild(viewport);	
})(navigator.userAgent);
