var Dynamics = window.Dynamics || {};

/**
 * Contains AJAX polling for user talks monitoring
 *
 * requires Utils
 */
Dynamics.Dialogs = Dynamics.Dialogs || {
    startListen: function(userId,otherUserId,lastUpdateTime,fnUpdate){
        var _lastTime = lastUpdateTime;
        (function updateMessages() {
            setTimeout(function () {
                $.ajax({
                    type: 'GET',
                    dataType: 'json',
                    url: '/'+userId+'/talks/new?userToTalkId=' + (otherUserId ? otherUserId : '') + '&time=' + _lastTime,
                    success: function (data) {
                        if(data.length) {
                            Utils.playSound("msg-sound");
                            if(fnUpdate) fnUpdate(data);
                            _lastTime = data[data.length-1].time;
                        }
                    },
                    complete: updateMessages
                });
            }, 3000);
        })();
    }
};

/**
 * Contains AJAX polling for team talks monitoring
 *
 * requires Utils
 */
Dynamics.Topics = Dynamics.Topics || {
    startListen: function(topicId,lastUpdateTime,fnUpdate){
        var _lastTime = lastUpdateTime;
        (function updateMessages() {
            setTimeout(function () {
                $.ajax({
                    type: 'GET',
                    dataType: 'json',
                    url: '/groupcontroller/getnewtopicmessages?topicId=' + topicId + '&time=' + _lastTime,
                    success: function (data) {
                        if(data.length) {
                            Utils.playSound("msg-sound");
                            if(fnUpdate) fnUpdate(data);
                            _lastTime = data[data.length-1].time;
                        }
                    },
                    complete: updateMessages
                });
            }, 2000);
        })();
    }
};