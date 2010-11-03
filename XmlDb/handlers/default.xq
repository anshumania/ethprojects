(:
  module namespace explanation:
  -------------------------------------
		the module namespace consists of the base project uri (see .config/sausalito.xml) plus the module
		handler name (that is equal to the .xq file name)
:)
module namespace def = "http://watchmoviesonline.ethz.ch/default";

declare namespace xhtml = "http://www.w3.org/1999/xhtml";

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
    
    if (fn:contains(http:get-parameters("sources"), "sidereel"))
    then
        for $link in def:sidereel(http:get-parameters("query"))
        let $rating := def:movie-rating($link/text())
        return (
            <div class="entry">
                {$i}. {$link}<br />
                <small>SideReel.com</small><br />
                {$rating}
            </div>,
            set $i := $i + 1
        )
    else (
    ),
    
    if (fn:contains(http:get-parameters("sources"), "tvshack"))
    then
        for $link in def:tvshack(http:get-parameters("query"))
        let $rating := def:movie-rating($link/text())
        return (
            <div class="entry">
                {$i}. {$link}<br />
                <small>TVshack.cc</small><br />
                {$rating}
            </div>,
            set $i := $i + 1
        )
    else(
    )
};

declare sequential function def:ratings ()
{
    declare
        $result := http-client:read(fn:concat("http://api.themoviedb.org/2.1/Movie.search/en/xml/5aacfe8b12e2c8b07e3b16ddc9fc3359/", http:get-parameters("query"))),
        $i := 1;
    
    try {
        for $movie in $result//movie
        let
            $name := $movie/name/text(),
            $rating := $movie/rating/text() cast as xs:double,
            $imdb := $movie/imdb_id/text(),
            $stars := def:get-rating-stars($rating, $name)
        return (
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
    } catch err:XPTY0019 {
        "No ratings available."
    } catch err:XPTY0004 {
        "No ratings available."
    }
};

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

declare function def:sidereel ($query as xs:string)
{
    for $link in http-client:read(
        fn:concat("http://www.sidereel.com/_search?siteSearchType=sidereel&amp;x=0&amp;y=0&amp;searchQuery=", $query)
    )//xhtml:div[@class eq "medium"]
    
    let
        $href := $link//xhtml:a/@href,
        $text := $link//xhtml:a/text()
    
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