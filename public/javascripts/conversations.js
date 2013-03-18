var Templates = window.Templates || {
    talkMsgTpl: function(data) {},
    conversationMsgTpl: function(data) {},
    popupMsgTpl: function(data) {},
    popupMsgItemTpl: function(data) {}
};

var Dynamics = window.Dynamics || {

};

Dynamics.Messages = Dynamics.Messages || {
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
                            //console.log(JSON.stringify(data));
                            if(fnUpdate) fnUpdate(data);

                            _lastTime = data[data.length-1].time;
                            //console.log('newLastTime = ' + _lastTime);
                        }
                    },
                    complete: updateMessages
                });
            }, 5000);
        })();
    }
};

var Utils = {
    feed0: function(n){
        return (n > 9 ? "" : "0") + n;
    },
    formatTime: function(time){
        var date = new Date(time);
        return date.getFullYear() + "-" + this.feed0(date.getMonth()+1) + "-" +
            this.feed0(date.getDate()) + " " +
            this.feed0(date.getHours()) + ":" + this.feed0(date.getMinutes()); // + ":" + date.getSeconds();
    },

    nl2br: function(str) {
        return str.split("\n").join("<br />");
    },
    scrollToBottom: function(el){
        //console.log(el.get(0).scrollHeight + ' - ' + el.height());
        el.scrollTop(el.get(0).scrollHeight - el.height());
    }
};

/** CNTRL + ENTER event */
(function(){
    $.fn.ctrlEnter = function (btns, fn) {
        var thiz = $(this);
        btns = $(btns);

        function performAction (e) {
            fn.call(thiz, e);
        }

        thiz.bind("keydown", function (e) {
            if (e.keyCode === 13 && e.ctrlKey) {
                performAction(e);
                e.preventDefault();
            }
        });
    };
})();

/** Register handlebars helpers */
(function(){
    Handlebars.registerHelper("formatTime", function(time){
        return Utils.formatTime(time);
    });
    Handlebars.registerHelper("nl2br", function(str){
        return new Handlebars.SafeString(Utils.nl2br(str));
    });
    Handlebars.registerHelper('ifCond', function(v1, v2, op, options) {
        var dif = v1 - v2;
        var sgn = dif < 0 ? -1 : (dif > 0 ? 1 : 0);
        if(sgn == op) {
            return options.fn(this);
        }
        return options.inverse(this);
    });
})();