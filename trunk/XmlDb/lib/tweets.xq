module namespace tweets = 'http://watchmoviesonline.ethz.ch/lib/tweets';

declare namespace atom = "http://www.w3.org/2005/Atom";

import module namespace http-client = "http://expath.org/ns/http-client";

(:
    Used to retrieve current tweets based on the Twitter Search
    API.
:)
declare sequential function tweets:get ($movie as xs:string)
{
    declare
        $result := http-client:read(fn:concat("http://search.twitter.com/search.atom?q=", $movie)),
        $i := 1;
    
    for $tweet in $result//atom:entry
    let
        $name := $tweet/atom:author/atom:name/text(),
        $title := $tweet/atom:title/text(),
        $link := $tweet/atom:link[@type eq "text/html"]/@href
    return (
        if ($i le 10)
        then (
            <div class="entry">
                <strong>@{$name}: </strong>
                <span>{$title} </span>
                <a href="{$link}">[link]</a><br /><br />
            </div>,
            set $i := $i + 1
        )
        else (
        )
    )
};
