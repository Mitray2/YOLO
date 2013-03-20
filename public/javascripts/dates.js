window.Dates = {
    setTimezoneCookie: function(){

        var timezone_cookie = "tz";
        var opts = { path:'/' };

        // if the timezone cookie not exists create one.
        if (!$.cookie(timezone_cookie)) {

            // check if the browser supports cookie
            var test_cookie = 'test-cookie';
            $.cookie(test_cookie, true, opts);

            // browser supports cookie
            if ($.cookie(test_cookie)) {

                // delete the test cookie
                $.cookie(test_cookie, null, opts);

                // create a new cookie
                var tzOffset = -new Date().getTimezoneOffset()/60;
                $.cookie(timezone_cookie, tzOffset, opts);

                // re-load the page
                //location.reload();
            }
        }
        // if the current timezone and the one stored in cookie are different
        // then store the new timezone in the cookie and refresh the page.
        else {

            var storedOffset = parseInt($.cookie(timezone_cookie));
            var currentOffset = -new Date().getTimezoneOffset()/60;

            // user may have changed the timezone
            if (storedOffset !== currentOffset) {
                $.cookie(timezone_cookie, currentOffset, opts);
                //location.reload();
            }
        }
    }
};

/** Put client timezone to cookies */
(function(){
    Dates.setTimezoneCookie()
})();