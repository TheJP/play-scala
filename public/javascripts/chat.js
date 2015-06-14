var username = '';

//Display new messages
function receiveMessages(msgs){
    msgs.reverse(); //Order messages correct for view
    for(var key in msgs){
        var msg = msgs[key];
        var message = $('<p />').text(msg);
        $('#chat').append(message);
        //Scroll to bottom
        $("#chat").scrollTop($("#chat")[0].scrollHeight);
    }
}

$(document).ready(function(){

	$('#connect-form button').prop('disabled', false);

	$('#connect-form').submit(function(){
		$('#connect-form button').prop('disabled', true);
		api.connect(
			//Success
			function(){
				api.send(ApiRequest.Login, { username: $('#username').val() });
				username = $('#username').val();
				$('#connect').slideUp();
				$('#send').slideDown(function(){ $('#send input').focus(); });
			},
			//Fail
			function(){
				$('#connect-form button').prop('disabled', false);
				$('#connect').slideDown();
				$('#send').slideUp();
			}
		);
		return false;
	});

	$('#send-form').submit(function(){
		api.send(ApiRequest.SendMessage, { msg: $('#message').val() });
		$('#message').val('')
		return false;
	})

    //Receive message notifications
    api.register(ApiRequest.SendMessage, function(data){
        if(data.success){ receiveMessages([data.username + ': ' + data.msg]); }
        else { alert(data.error_text); }
    });
    //Receive login notifications
    api.register(ApiRequest.Login, function(data){
        if(data.success){ receiveMessages([data.username + ' joined the chat']); }
        else { alert(data.error_text); }
    });
    //Receive logout notifications
    api.register(ApiRequest.Logout, function(data){
        if(data.success){ receiveMessages([data.username + ' left the chat']); }
        else { alert(data.error_text); }
    });

});