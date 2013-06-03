/**
 * requires Utils
 *
 */
var Team = window.Team || {

    likeMsg: function(){
        var msgId = $(this).attr("data-msg-id") || null;
        if(msgId){
            var $likebox = $(this).closest(".likes");
            var $likescount = $likebox.find(".likes-count");

            $.ajax({
                type: 'POST',
                dataType: 'json',
                url: '/likes/topicmessage/'+msgId,
                success: function (data) {
                    if(data.status == 200) {

                        if($likescount.length){
                            console.log($likescount.text());
                            $likescount.text(parseInt($likescount.text()) + 1);
                        }else{
                            console.log('likes-count = 1');
                            $likebox.prepend("<span class='likes-count'>1</span>")
                        }

                        $likebox.find(".like-it").remove().andSelf().append("<span class='like-it'></span>");
                    }
                }
            });
        }

        return false;
    },

    _updateFavs: function(team, type, category, onComplete){
        $.ajax({
            type: type,
            dataType: 'json',
            url: '/teams/'+team+'/'+category,
            success: function (data) { /* ok */ },
            complete: onComplete
        });
    },


    addToFavourites: function(){
        var teamId = $(this).attr("data-team") || null;
        Team._updateFavs(teamId,'POST','favourites',function(){Team._makeTeamTopicsFavourite(teamId)});
    },

    addToBlacklist: function(){
        var teamId = $(this).attr("data-team") || null;
        Team._updateFavs(teamId,'POST','blacklist',function(){Team._makeTeamTopicsBlacklist(teamId)});
    },

    removeFromFavourites: function(){
        var teamId = $(this).attr("data-team") || null;
        Team._updateFavs(teamId,'DELETE','favourites',function(){Team._undoTeamFavouriteTopics(teamId)});
    },

    removeFromBlacklist: function(){
        var teamId = $(this).attr("data-team") || null;
        Team._updateFavs(teamId,'DELETE','blacklist',function(){Team._undoTeamBlacklistTopics(teamId)});
    },


    _makeTeamTopicsFavourite: function(teamId){
        var topics = $(".team-"+teamId+"-topic");
        /*if(topics.length){
            topics.find(".topic-fav-link").hide();
            topics.find(".topic-fav-link-hovered").show();
            topics.find(".topic-ban-link").fadeOut(200);
        } else {
            $(".topic-fav-link").hide();
            $(".topic-fav-link-hovered").show();
            $(".topic-ban-link").fadeOut(200);
        }*/
        if(topics.length){
            topics.remove();
        }else{
            $(".topic-ban-link").fadeOut(200);
            $(".topic-fav-link").hide();
            $(".topic-fav-link-hovered").show();

        }
    },

    _undoTeamFavouriteTopics: function(teamId){
        var topics = $(".team-"+teamId+"-topic");
        /*if(topics.length){
            var isOnFavTab = $("#tt-category").find(":selected").val();

            if(isOnFavTab == 1){
                topics.remove();
                if($("li.search-result-item").length < 1){
                    location.href = "/teamtrack";
                }
            }else{
                topics.find(".topic-fav-link-hovered").hide();
                topics.find(".topic-fav-link").show();
                topics.find(".topic-ban-link").fadeIn(200);
            }
        } else {
            $(".topic-fav-link-hovered").hide();
            $(".topic-fav-link").show();
            $(".topic-ban-link").fadeIn(200);
        }*/
        if(topics.length){
            topics.remove();

            var isOnFavTab = $("#tt-category").find(":selected").val();
            if(isOnFavTab == 1 && $("li.search-result-item").length < 1){
                location.href = "/teamtrack";
            }
        }else{
            $(".topic-ban-link").fadeIn(200);
            $(".topic-fav-link-hovered").hide();
            $(".topic-fav-link").show();
        }
    },

    _makeTeamTopicsBlacklist: function(teamId){
        var topics = $(".team-"+teamId+"-topic");
        if(topics.length){
            topics.remove();
        }else{
            $(".topic-fav-link").fadeOut(200);
            $(".topic-ban-link").hide();
            $(".topic-ban-link-hovered").show();
        }
    },

    _undoTeamBlacklistTopics: function(teamId){
        var topics = $(".team-"+teamId+"-topic");
        if(topics.length){
            topics.remove();

            var isOnFavTab = $("#tt-category").find(":selected").val();
            if(isOnFavTab == 2 && $("li.search-result-item").length < 1){
                location.href = "/teamtrack";
            }
        }else{
            $(".topic-fav-link").fadeIn(200);
            $(".topic-ban-link-hovered").hide();
            $(".topic-ban-link").show();
        }
    },

    bindFavButtonsHandlers: function(){
        $(document).on('click', ".topic-fav-link-hovered",Team.removeFromFavourites);
        $(document).on('click', ".topic-fav-link", Team.addToFavourites);
        $(document).on('click', ".topic-ban-link-hovered", Team.removeFromBlacklist);
        $(document).on('click', ".topic-ban-link", Team.addToBlacklist);
    },

    bindLikeHandlers: function(){
        $(document).on('click', "a.like-it", Team.likeMsg);
    },

    bindQuoteHandlers: function(){
        var getSelectedText = function() {
            if (window.getSelection) {
                return window.getSelection().toString();
            } else if (document.selection) {
                return document.selection.createRange().text;
            }
            return '';
        };

        $(document).on("mousedown", function() {
            $(".quote-tip").fadeOut();
        });

        $(document).on("click", ".quote-it", function() {
            var $this = $(this);
            var $msgBox = $this.closest("li");
            var msgWriter = $msgBox.find(".writer").text();

            var selection = $this.hasClass("quote-all") ? $msgBox.find(".msgRead").text() : getSelectedText();
            if(selection) {
                if(typeof(writeEnabled) == "function") writeEnabled(true);
                var $inputBox = $(".answer-expand").find(".writeComment");
                $inputBox.val($inputBox.val() + "<blockquote>"+selection+"<span>"+msgWriter+"</span></blockquote>\n")
                         .trigger('autosize').focus();
            }
            return false;
        });

        $(document).on("mouseup", ".msgRead", function() {
            var _this = this;
            var selection = getSelectedText();
            if(selection) {
                var $msgBox = $(_this).closest("li");
                var $quoteTip = $msgBox.find(".quote-tip");
                $quoteTip.fadeIn(300);
            }
        });
    },


    startNewEventsListening: function(teamId,lastUpdateTime,fnUpdate){
        var _lastTime = lastUpdateTime;
        (function updateMessages() {
            setTimeout(function () {
                $.ajax({
                    type: 'GET',
                    dataType: 'json',
                    url: '/teams/'+teamId+'/events/new?time=' + _lastTime,
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

$(document).ready(function(){
    Team.bindFavButtonsHandlers();
    Team.bindLikeHandlers();
    Team.bindQuoteHandlers();
});