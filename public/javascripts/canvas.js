var ctx;
var enable = false;
var cursor = new Image();

var cursors = {};

//Cursor specifics
var relX = 0;
var relY = 0;
var oldX = 0;
var oldY = 0;
var cursorWidth = 22;
var cursorHeight = 30;

//Render canvas
function draw(){
	//Resize canvas to real size (elsewise the content gets stretched)
	ctx.canvas.width = parseInt($('#canvas').css('width'), 10);
	ctx.canvas.height = parseInt($('#canvas').css('height'), 10);
	ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);

	var factorX = ctx.canvas.width / 100;
	var factorY = ctx.canvas.height / 100;

	if(enable){
		for(var key in cursors){
			var x = cursors[key].x * factorX;
			var y = cursors[key].y * factorY;
			ctx.drawImage(cursor, x-2, y, cursorWidth, cursorHeight);
		}
	}

	window.requestAnimationFrame(draw);
}

$(document).ready(function(){

	//Save canvas context (used in draw)
	ctx = $('#canvas')[0].getContext('2d');

	//Download cursor image
	cursor.onload = function(){
		enable = true;
	}
	cursor.src = '/assets/images/cursor.png';

	//Update context
	api.register(ApiRequest.MouseMove, function(data){
		if(data.success){
			cursors[data.username] = { x: data.x, y: data.y };
		}
	});
    //Receive logout notifications
    api.register(ApiRequest.Logout, function(data){
        if(data.success){ delete cursors[data.username]; }
        else { alert(data.error_text); }
    });

	$('#chat').mousemove(function(e){
		var parentOffset = $(this).offset(); 
		relX = e.pageX - parentOffset.left;
		relY = e.pageY - parentOffset.top;
	});

	setInterval(function(){
		if(oldX != relX || oldY != relY){
			api.send(ApiRequest.MouseMove, {
				username: username,
				/* Transform x,y into a 100x100 box */
				x: (100*relX)/ctx.canvas.width,
				y: (100*relY)/ctx.canvas.height
			});
		}
		oldX = relX;
		oldY = relY;
	}, 20);

	//Start animation cycle
	window.requestAnimationFrame(draw);

});