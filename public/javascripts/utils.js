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
    asHtml: function(){

    },
    linkify: function linkify(inputText) {
        var replacedText, replacePattern1, replacePattern2, replacePattern3;

        //URLs starting with http://, https://, or ftp://
        replacePattern1 = /(\b(https?|ftp):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/gim;
        replacedText = inputText.replace(replacePattern1, '<a href="$1" target="_blank">$1</a>');

        //URLs starting with "www." (without // before it, or it'd re-link the ones done above).
        replacePattern2 = /(^|[^\/])(www\.[-A-Z0-9+&@#\/%?=~_|!:,.;]+(\b|$))/gim;
        replacedText = replacedText.replace(replacePattern2, '$1<a href="http://$2" target="_blank">$2</a>');

        //Change email addresses to mailto:: links.
        replacePattern3 = /(\w+@[a-zA-Z_]+?\.[a-zA-Z]{2,6})/gim;
        replacedText = replacedText.replace(replacePattern3, '<a href="mailto:$1">$1</a>');

        return replacedText; //.split("\n").join("<br />");
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
        var player = document.getElementById(soundElId);
        if(player) player.play();
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