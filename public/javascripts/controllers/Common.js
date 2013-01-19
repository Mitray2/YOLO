$(document).ready(function() {
	
	$('#sign-in').click(function(e){
		showCloseEnterDialog(e, true);
	});
	$('#overlay').click(function(e){
		showCloseEnterDialog(e, false);
	});
	$('#close').click(function(e){
		showCloseEnterDialog(e, false);
	});
	
	$('#forget-password').click(showRecoverPasswordForm);
	$('#showEnterForm').click(showEnterForm);
	
	function showRecoverPasswordForm(e){
		e.preventDefault();
		$('#popup-enter, #popup-enter-ttl').slideUp(500, function(){
			$('#popup-enter-ttl').html(internationalization.password_repair)
			$('#enterForm').hide();
			$('#recoverEmailForm').show();
		});
		$('#popup-enter, #popup-enter-ttl').slideDown(500);
	}
	
	function showEnterForm(e){
		e.preventDefault();
		$('#popup-enter, #popup-enter-ttl').slideUp(500, function(){
			$('#popup-enter-ttl').html(internationalization.system_enter)
			$('#recoverEmailForm').hide();
			$('#enterForm').show();
		});
		$($('#popup-enter, #popup-enter-ttl')).slideDown(500);
	}
	
	function showCloseEnterDialog(e, show){
		e.preventDefault();
		if(show == true){
			$('#popup-enter').fadeIn(1000);
			$('#overlay').fadeIn(1000);
		} else {
			$('#popup-enter').fadeOut(1000);
			$('#overlay').fadeOut(1000, function(){
				$('#recoverEmailForm').hide();
				$('#enterForm').show();
			});
		}
	}
	
});