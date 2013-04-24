var Dynamics = window.Dynamics || {};

var Templates = window.Templates || {
    talkMsgTpl: function(data) {},
    conversationMsgTpl: function(data) {},
    popupMsgTpl: function(data) {},
    popupMsgItemTpl: function(data) {},
    topicMsgTpl: function(data) {},
    topicMsgsTpl: function(data) {}
};

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
                            Utils.playSound("/public/sounds/CLACK.WAV");
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
                            Utils.playSound("/public/sounds/CLACK.WAV");
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

var Utils = {
    feed0: function(n){
        return (n > 9 ? "" : "0") + n;
    },
    formatTime: function(time){
        var date = new Date(time);
        return date.getFullYear() + "-" + this.feed0(date.getMonth()+1) + "-" +
            this.feed0(date.getDate()) + " " +
            this.feed0(date.getHours()) + ":" + this.feed0(date.getMinutes()) + ":" + this.feed0(date.getSeconds());
    },

    nl2br: function(str) {
        return str.split("\n").join("<br />");
    },
    scrollInit: function(el){
        var maxH = parseInt(el.css("max-height"));
        var curH = el.get(0).scrollHeight;

        if(curH > maxH){
            el.slimScroll({
                height: 'auto',
                color: '#aaa',
                alwaysVisible: true
            });
            Utils.scrollToBottom(el);
        }
    },
    scrollToBottom: function(el){
        //el.scrollTop(el.get(0).scrollHeight - el.height());
        el.slimScroll({
            height: 'auto',
            color: '#aaa',
            alwaysVisible: true,
            scrollTo: el.get(0).scrollHeight - el.height()
        });
    },
    scrollToElem: function(cont,el){
        var scrollToY = el.position().top - 20;
        cont.slimScroll({
            height: 'auto',
            color: '#aaa',
            alwaysVisible: true,
            scrollTo: scrollToY
        });
    },
    limitTo: function(str,maxLen) {
        return str.length > maxLen ? str.substr(0,maxLen) + "\n..." : str;
    },
    playSound: function(sound){
        //document.getElementById("msg-sound").innerHTML='<embed src="'+sound+'" hidden="true" autostart="true" loop="false" />';
        document.getElementById("msg-sound").play();
        return false;
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
/*    Handlebars.registerHelper('OR', function(v1, v2, options) {
        if(v1 || v2) {
            return options.fn(this);
        }
        return options.inverse(this);
    });*/

    Handlebars.registerHelper('limitTo', Utils.limitTo);
    Handlebars.registerHelper('limitTo_nl2br', function(str,maxLen){
        return new Handlebars.SafeString(Utils.nl2br(Utils.limitTo(str,maxLen)));
    });
    Handlebars.registerHelper('hasEditRights', function(isAdmin,userId,authorId,options){
        return (isAdmin || userId == authorId) ? options.fn(this) : options.inverse(this);
    });
})();