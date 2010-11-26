(:
  module namespace explanation:
  -------------------------------------
		the module namespace consists of the base project uri (see .config/sausalito.xml) plus the module
		handler name (that is equal to the .xq file name)
:)
module namespace def = "http://watchmoviesonline.ethz.ch/default";

declare namespace xhtml = "http://www.w3.org/1999/xhtml";
declare namespace atom = "http://www.w3.org/2005/Atom";

import module namespace http = "http://www.28msec.com/modules/http";
import module namespace cookie = "http://www.28msec.com/modules/http/cookie";
import module namespace http-client = "http://expath.org/ns/http-client";

(: maximum value for ratings from TheMovieDB.org :)
declare variable $def:max-rating as xs:integer := 10;

declare sequential function def:movies ()
{
    declare $i := 1;
    
    (:
        FLWOR expressions processing data from remote sites must
        return a sequence of <div class="entry"> elements.
    :)
    
    if (fn:contains(http:get-parameters("sources"), "sidereeltv"))
    then
        for $link in def:sidereel(http:get-parameters("query"), "television")
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
        for $link in def:sidereel(http:get-parameters("query"), "movie")
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
    
    if (fn:contains(http:get-parameters("sources"), "tvshack"))
    then
        for $link in def:tvshack(http:get-parameters("query"))
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
};

declare sequential function def:meta-data()
{
    declare $ratings := def:ratings(http:get-parameters("movie"));
    declare $tweets := def:tweets(http:get-parameters("movie"));
    
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

(:
    Used to retrieve movie ratings from TheMovieDB.
:)
declare sequential function def:ratings ($movie as xs:string)
{
    declare
        $result := http-client:read(fn:concat("http://api.themoviedb.org/2.1/Movie.search/en/xml/5aacfe8b12e2c8b07e3b16ddc9fc3359/", $movie)),
        $i := 1;
    
    try {
        for $movie in $result//movie
        let
            $name := $movie/name/text(),
            $rating := $movie/rating/text() cast as xs:double,
            $imdb := $movie/imdb_id/text(),
            $stars := def:get-rating-stars($rating, $name)
        return (
            if ($i le 10)
            then (
                <div class="entry">
                    {$i}.
                    {
                        if ($imdb ne "")
                        then
                            <a href="http://www.imdb.com/title/{$imdb}" target="_blank">{$name}</a>
                        else
                            $name
                    }
                    <br />
                    {$stars}
                </div>,
                set $i := $i + 1
            )
            else
                ()
        )
    } catch err:XPTY0019 {
        "No ratings available."
    } catch err:XPTY0004 {
        "No ratings available."
    }
};

(:
    Used to retrieve current tweets based on the Twitter Search
    API.
:)
declare sequential function def:tweets ($movie as xs:string)
{
    declare
        $result := http-client:read(fn:concat("http://search.twitter.com/search.atom?q=", $movie)),
        $i := 1;
    
    for $tweet in $result//atom:entry
    let
        $name := $tweet/atom:author/atom:name/text(),
        $title := $tweet/atom:title/text(),
        $content := $tweet/atom:content/text(),
        $link := $tweet/atom:link[@type eq "text/html"]/@href
    return (
        if ($i le 10)
        then (
            <div class="entry">
                {$i}. <a href="{$link}">Link</a><br/>
                <small>{$title}</small><br />
                <small>{$name}</small><br />
            </div>,
            set $i := $i + 1
        )
        else
            ()
    )
};

(:
    This function is currently not in use due to request
    limitations by TheMovieDB.

declare sequential function def:movie-rating ($title as xs:string)
{
    declare $result := http-client:read(fn:concat("http://api.themoviedb.org/2.1/Movie.search/en/xml/5aacfe8b12e2c8b07e3b16ddc9fc3359/", $title));
    
    try {
        let
            $name := $result//movie[1]/name/text(),
            $rating := $result//movie[1]/rating/text() cast as xs:double
        return
            def:get-rating-stars($rating, $name)
    } catch err:XPTY0019 {
        "No rating"
    } catch err:XPTY0004 {
        "No rating"
    }
};
:)

declare sequential function def:get-rating-stars ($rating as xs:double, $name as xs:string)
{
    declare
        $r := fn:round($rating),
        $i := 1;
    
    (
        while ($i le $r) {(
            <img src="/star1.gif" title="rating for '{$name}' (by TheMovieDB.org)" />,
            set $i := $i + 1
        )},
    
        set $i := 1,
    
        while ($i le $def:max-rating - $r) {(
            <img src="/star2.gif" title="rating for '{$name}' (by TheMovieDB.org)" />,
            set $i := $i + 1
        )}
    )
};

(:
    Functions retrieving data from remote sites must return
    a sequence of link elements.
:)

declare function def:sidereel ($query as xs:string, $mode as xs:string)
{
    for $link in http-client:read(
        fn:concat("http://maxspeicher.orgfree.com/test/sidereel.php?m=", $mode, "&amp;q=", $query)
    )//div[@class eq "title"]
    
    let
        $href := $link/h2/a/@href,
        $text := $link/h2/a/text()
    
    return
        <a href="http://www.sidereel.com{$href}">
            {$text}
        </a>
};

declare function def:tvshack ($query as xs:string)
{
    for $link in http-client:read(
        fn:concat("http://tvshack.cc/search/", $query)
    )//xhtml:ul[@class eq "list-list"]/xhtml:li
    
    let
        $href := $link//xhtml:a[1]/@href,
        $text := $link//xhtml:a[1]/xhtml:strong/text()
    
    return
        <a href="{$href}">
            {$text}
        </a>
};