//Display new messages
function receiveMessages(msgs){
    msgs.reverse(); //Order messages correct for view
    for(var key in msgs){
        var msg = msgs[key];
        var message = $('<div />', { id: 'message-' + msg.id, class: 'message' });
        message.append($('<a />',
            { id: 'person-' + msg.id, class: 'person', href: '#'}).text(msg.username));
        message.append($('<span />').addClass('time').text(api.formatTime(msg.time)));
        message.append($('<span />').addClass('message-content').text(msg.msg));
        message.addClass(user.id == msg.userid ? 'own' : 'their');
        $('#chat').append(message);
        //Scroll to bottom
        $("#chat").scrollTop($("#chat")[0].scrollHeight);
    }
}

$(document).ready(function(){

	$('#connect-form').submit(function(){
		$('#connect-form button').prop('disabled', true);
		api.connect(
			//Success
			function(){
				api.send(ApiRequest.Login, { username: $('#username').val() });
				$('#connect-form').slideUp();
			},
			//Fail
			function(){
				$('#connect-form button').prop('disabled', false);
			}
		);
		return false;
	});

    //Receive message notifications
    api.register(ApiRequest.SendMessage, function(data){
        if(data.s){ receiveMessages(data.msgs); }
        else { alert(data.error_text); }
    });

});