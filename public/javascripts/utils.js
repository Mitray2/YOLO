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
    isScrolledToBottom: function(){
        var wintop = $(window).scrollTop(), docheight = $(document).height(), winheight = $(window).height();
        var scrolltrigger = 0.95;

        return (wintop/(docheight-winheight)) > scrolltrigger;
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
    playSound: function(soundElId){
        //document.getElementById("msg-sound").innerHTML='<embed src="'+sound+'" hidden="true" autostart="true" loop="false" />';
        document.getElementById(soundElId).play();
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