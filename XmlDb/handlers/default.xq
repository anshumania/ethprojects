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

declare sequential function def:index ()
{
    declare $i as xs:integer := 0;
    
    (:
        FLWOR expressions processign data from remote sites must
        return a sequence of <div class="entry"> elements.
    :)
    
    for $link in def:sidereel(http:get-parameters("query"))
    return (
        set $i := $i + 1,
        <div class="entry">
            {$i}. {$link}<br />
            <small>SideReel.com</small>
        </div>
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