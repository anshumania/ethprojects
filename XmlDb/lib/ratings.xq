module namespace ratings = 'http://watchmoviesonline.ethz.ch/lib/ratings';

import module namespace http-client = "http://expath.org/ns/http-client";

(: maximum value for ratings from TheMovieDB.org :)
declare variable $ratings:max as xs:integer := 10;

(:
    Used to retrieve movie ratings from TheMovieDB.
:)
declare sequential function ratings:get ($movie as xs:string)
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
            $stars := ratings:get-stars($rating, $name)
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
    This function is currently not in use due to request
    limitations by TheMovieDB.

declare sequential function ratings:movie-rating ($title as xs:string)
{
    declare $result := http-client:read(fn:concat("http://api.themoviedb.org/2.1/Movie.search/en/xml/5aacfe8b12e2c8b07e3b16ddc9fc3359/", $title));
    
    try {
        let
            $name := $result//movie[1]/name/text(),
            $rating := $result//movie[1]/rating/text() cast as xs:double
        return
            ratings:get-stars($rating, $name)
    } catch err:XPTY0019 {
        "No rating"
    } catch err:XPTY0004 {
        "No rating"
    }
};
:)

declare sequential function ratings:get-stars ($rating as xs:double, $name as xs:string)
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
    
        while ($i le $ratings:max - $r) {(
            <img src="/star2.gif" title="rating for '{$name}' (by TheMovieDB.org)" />,
            set $i := $i + 1
        )}
    )
};
