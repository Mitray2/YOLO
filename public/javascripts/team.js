/**
 * requires Utils
 *
 */
var Team = window.Team || {

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
        //var favH = $(".topic-fav-link-hovered");
        /*if(favH.length>0) */$(document).on('click', ".topic-fav-link-hovered",Team.removeFromFavourites);

        //var fav = $(".topic-fav-link");
        /*if(fav.length>0) */$(document).on('click', ".topic-fav-link", Team.addToFavourites);

        //var banH = $(".topic-ban-link-hovered");
        /*if(banH.length>0) */$(document).on('click', ".topic-ban-link-hovered", Team.removeFromBlacklist);

        //var ban = $(".topic-ban-link");
        /*if(ban.length>0)*/ $(document).on('click', ".topic-ban-link", Team.addToBlacklist);
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
});