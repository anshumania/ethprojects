module namespace sources = 'http://watchmoviesonline.ethz.ch/lib/sources';

declare namespace xhtml = "http://www.w3.org/1999/xhtml";

import module namespace tidy = "http://www.zorba-xquery.com/modules/tidy";
import module namespace http-client = "http://expath.org/ns/http-client";

(:
    Functions retrieving data from remote sites must return
    a sequence of hyperlink elements.
:)

declare sequential function sources:sidereel ($query as xs:string, $mode as xs:string)
{
    declare $resp := http-client:read(fn:concat("http://www.sidereel.com/_", $mode, "/search?q=", $query));
    declare $html := fn:string($resp[2]);
    
    set $html := fn:substring-after($html, "&lt;/head&gt;"),
    set $html := fn:substring-before($html, "&lt;/html&gt;"),
    
    for $link in tidy:tidy($html)//xhtml:div[@class eq "title"]
    
    let
        $href := $link/xhtml:h2/xhtml:a/@href,
        $text := $link/xhtml:h2/xhtml:a/text()
    
    return
        <a href="http://www.sidereel.com{$href}">
            {$text}
        </a>
};

declare function sources:tvshack ($query as xs:string)
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
