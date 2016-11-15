package crawling;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import dataObjects.Document;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import model.ModelDatabase;
import org.jsoup.Jsoup;

/**
 * Created by Thagus on 14/11/16.
 */
public class Crawler extends WebCrawler{
    private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");
    private static final Pattern SOUND_EXTENSIONS = Pattern.compile(".*\\.(mp3|ogg)$");
    private static final Pattern COMPRESSED_EXTENSIONS = Pattern.compile(".*\\.(zip|rar|gz|tar)$");
    private static final Pattern DOCUMENT_EXTENSIONS = Pattern.compile(".*\\.(pdf|doc|docx|xls|xlsx)$");
    private static final Pattern OTHER_EXTENSIONS = Pattern.compile(".*\\.(css|js)$");
    private ModelDatabase db = ModelDatabase.instance();

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
                OTHER_EXTENSIONS.matcher(href).matches() ||
                DOCUMENT_EXTENSIONS.matcher(href).matches())

                && href.contains("udlap.mx");
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

            if(text.length()<=0){   //There is no use for an empty website
                return;
            }

            String html = htmlParseData.getHtml();

            org.jsoup.nodes.Document htmlDocument = Jsoup.parse(html);
            String title = htmlDocument.getElementsByTag("title").get(0).text();

            Document document = new Document(url, title, text);
            feedDatabase(document);

            logger.info("Title: {}", title);
            logger.info("Text length: {}", text.length());
            logger.info("Html length: {}", html.length());
        }

        logger.info("=============");
    }

    /**
     * Counts the words on the document in order to calculate the TF of each term in the document
     * Adds the document and its terms to the database
     *
     * @param doc The document to be added
     */
    private void feedDatabase(Document doc){
        boolean insertCheck = db.opDocuments.addDocument(doc.getUrl(), doc.getTitle(), doc.getText());

        //The document was correctly added if there is no duplicate key
        if(insertCheck) {
            HashMap<String, Integer> wordCountLocal = doc.countWords();    //Request the count of words for the inserted document
            for(Map.Entry<String, Integer> termEntry : wordCountLocal.entrySet()){      //For each term obtained from the document (Key String is the term, Value Integer is the TF)
                //Write the term, with the document id and the TF
                db.opInvertedIndex.addTerm(doc.getUrl(), termEntry.getKey(), termEntry.getValue());
            }
        }
    }
}
