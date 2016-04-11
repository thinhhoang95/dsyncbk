var Utils = (function(){
	var vendor = getVendor(),
		hasTransform = supportTransform();

	function getVendor(){
		var el = document.createElement('div'),
			prefixes = ["Webkit", "O", "ms", "Moz"],
			prefix = "",
			i = prefixes.length;

		while(--i >= 0){
			var css = prefixes[i] + "Transform";
			if (css in el.style){
				prefix = prefixes[i];
				break;
			}
		}
		return prefix;
	};

	function supportTransform(){
		return (vendor + "Transform") in document.createElement('div').style;
	};

	function translate(elm, x, y){
		if (hasTransform){
			elm.style[vendor + "Transform"] = 'translate(' + x + 'px,' + y + 'px)';
		}
		else{
			elm.style.marginLeft = x + 'px';
			elm.style.marginTop = y + 'px';
		}
	};

	return {
		vendor: getVendor(),
		translate: translate
	}
})();