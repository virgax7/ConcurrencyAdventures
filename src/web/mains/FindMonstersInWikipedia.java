package web.mains;

import web.crawling.OccurrencesWebCrawler;

import java.io.IOException;

/*
    The goal for this class is to crawl starting from https://en.wikipedia.org/wiki/MapleStory
    and find all the times that the word monster(case insensitive) was mentioned
    in half a minute of search time and also the links that contain it
 */
public class FindMonstersInWikipedia {
    public static void main(String[] args) {
        try {
            final OccurrencesWebCrawler crawler = new OccurrencesWebCrawler(
                    "(m|M)(o|O)(n|N)(s|S)(t|T)(e|E)(r|R)",
                    "/wiki/MapleStory",
                    2,
                    "/wiki/(?!File|Wiki|Special)\\w+",
                    "https://en.wikipedia.org");
            crawler.start();
            System.out.println(crawler.getCountMap());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

