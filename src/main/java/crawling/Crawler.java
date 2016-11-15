package crawling;

import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.Header;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * Created by Thagus on 14/11/16.
 */
public class Crawler extends WebCrawler{
    private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");
    private static final Pattern SOUND_EXTENSIONS = Pattern.compile(".*\\.(mp3|ogg)$");
    private static final Pattern COMPRESSED_EXTENSIONS = Pattern.compile(".*\\.(zip|rar|gz|tar)$");
    private static final Pattern OTHER_EXTENSIONS = Pattern.compile(".*\\.(css|js)$");

    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        //Ignore the url if has the defined extensions
        return !(IMAGE_EXTENSIONS.matcher(href).matches() ||
                SOUND_EXTENSIONS.matcher(href).matches() ||
                COMPRESSED_EXTENSIONS.matcher(href).matches() ||
                OTHER_EXTENSIONS.matcher(href).matches());
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();

        logger.info("URL: {}", url);

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();

            logger.info("Title: {}", htmlParseData.getTitle());

            logger.info("Text length: {}", text.length());
            logger.info("Html length: {}", html.length());
        }


        logger.info("=============");
    }
}
