(:
  module namespace explanation:
  -------------------------------------
		the module namespace consists of the base project uri (see .config/sausalito.xml) plus the module
		handler name (that is equal to the .xq file name)
:)
module namespace def = "http://watchmoviesonline.ethz.ch/default";

import module namespace ratings = "http://watchmoviesonline.ethz.ch/lib/ratings";
import module namespace tweets = "http://watchmoviesonline.ethz.ch/lib/tweets";
import module namespace sources = "http://watchmoviesonline.ethz.ch/lib/sources";

import module namespace http = "http://www.28msec.com/modules/http";

declare sequential function def:movies ()
{
    declare $i := 1;
    
    (:
        FLWOR expressions processing data from remote sites must
        return the movie name of the first result, followed by "///"
        and a sequence of <div class="entry"> elements.
    :)
    
    if (fn:contains(http:get-parameters("sources"), "sidereeltv"))
    then
        for $link in sources:sidereel(http:get-parameters("query"), "television")
        return (
            if ($i eq 1)
            then
                fn:concat($link/text(), "///")
            else
                (),
            <div class="entry">
                {$i}. {$link} | <a href="javascript:getData('{fn:replace($link/text(), "'", "\\'")}');">more info</a><br />
                <small>SideReel.com</small>
            </div>,
            set $i := $i + 1
        )
    else (
    ),
    
    if (fn:contains(http:get-parameters("sources"), "sidereelmovie"))
    then
        for $link in sources:sidereel(http:get-parameters("query"), "movie")
        return (
            if ($i eq 1)
            then
                fn:concat($link/text(), "///")
            else
                (),
            <div class="entry">
                {$i}. {$link} | <a href="javascript:getData('{fn:replace($link/text(), "'", "\\'")}');">more info</a><br />
                <small>SideReel.com</small>
            </div>,
            set $i := $i + 1
        )
    else (
    )
    
    (:
        Not available anymore. "Seized by U.S. Immigration and Customs
        Enforcement."
        
    if (fn:contains(http:get-parameters("sources"), "tvshack"))
    then
        for $link in sources:tvshack(http:get-parameters("query"))
        return (
            if ($i eq 1)
            then
                fn:concat($link/text(), "///")
            else
                (),
            <div class="entry">
                {$i}. {$link} | <a href="javascript:getData('{fn:replace($link/text(), "'", "\\'")}');">more info</a><br />
                <small>TVshack.cc</small>
            </div>,
            set $i := $i + 1
        )
    else(
    )
    :)
};

declare sequential function def:meta-data()
{
    declare $ratings := ratings:get(http:get-parameters("movie"));
    declare $tweets := tweets:get(http:get-parameters("movie"));
    
    <div class="subtitle">Ratings</div>,
    if ($ratings)
    then
        $ratings
    else
        "No ratings available.",
    <br />,<br />,
    <div class="subtitle">Latest Tweets</div>,
    if ($tweets)
    then
        $tweets
    else
        "No tweets available."
};
