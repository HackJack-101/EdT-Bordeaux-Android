if(document.getElementById('container') != null)
{
	var container = document.getElementById('container');
	container.style.width='100%';
	var banner = document.getElementById('bandeau');
	var img = banner.getElementsByTagName('img')[0];
	img.style.width = window.innerWidth + 'px'
	container.style.margin = '0';
	container.style.padding = '0';
	banner.style.margin = '0';
}