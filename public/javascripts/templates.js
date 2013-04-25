var Templates = window.Templates || {
    talkMsgTpl: function(data) {},
    conversationMsgTpl: function(data) {},

    popupMsgTpl: function(data) {},
    popupMsgItemsTpl: function(data) {},
    popupMsgItemTpl: function(data) {},

    topicMsgTpl: function(data) {},
    topicMsgsTpl: function(data) {},

    popupEventTpl: function(data) {},
    popupEventItemsTpl: function(data) {},
    popupEventItemTpl: function(data) {},

    teamActivityMessages: []
};

/** Register handlebars helpers
 *
 * requires Utils
 * */
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
    Handlebars.registerHelper("teamActivity", function(action){
        return new Handlebars.SafeString(Templates.teamActivityMessages[action] || "");
    });
})();