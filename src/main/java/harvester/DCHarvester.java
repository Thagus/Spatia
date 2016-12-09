package harvester;

import dataObjects.Document;
import model.ModelDatabase;
import org.dom4j.Element;
import se.kb.oai.OAIException;
import se.kb.oai.pmh.Header;
import se.kb.oai.pmh.IdentifiersList;
import se.kb.oai.pmh.OaiPmhServer;
import se.kb.oai.pmh.Record;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Thagus on 08/12/16.
 */
public class DCHarvester {
    private ModelDatabase db;

    public DCHarvester(){
        db = ModelDatabase.instance();
    }

    /**
     * Method to harvest an OAI-PMH collection that follows the Dublin Core standard
     * @param repository the URL of the OAI-PMH collection
     */
    public void harvest(String repository) throws OAIException, IOException {
        OaiPmhServer server = new OaiPmhServer(repository);

        //To list all identifiers in the repository that has "oai_dc" metadata
        IdentifiersList list = server.listIdentifiers("oai_dc");
        boolean more = true;
        while (more) {
            for (Header header : list.asList()) {
                //Read the record of the given identifier
                Record record = server.getRecord(header.getIdentifier(), "oai_dc");

                String url = "";
                String title = "";
                String text = "";

                //Now use the dom4j to handle the metadata
                Element root = record.getMetadata();
                //Iterate through every element withing the record to obtain the metadata
                for (Iterator i = root.elementIterator(); i.hasNext(); ) {
                    Element element = (Element) i.next();

                    String elementName = element.getName();
                    String elementText = element.getText();

                    switch (elementName) {
                        case "identifier":
                            if(url.length()==0)
                                url = elementText;
                            break;
                        case "title":
                            if(title.length()==0)
                                title = elementText;
                            break;
                        default:
                            text += elementText + "";
                            break;
                    }
                }

                System.out.println(url);
                System.out.println(title);
                System.out.println(text);
                System.out.println();

                Document document = new Document(url, title, text.trim());
                feedDatabase(document);
            }
            if (list.getResumptionToken() != null)
                list = server.listIdentifiers(list.getResumptionToken());
            else
                more = false;
        }
    }

    /**
     * Counts the words on the document in order to calculate the TF of each term in the document
     * Adds the document and its terms to the database
     *
     * @param doc The document to be added
     */
    private synchronized void feedDatabase(Document doc){
        if(doc.getUrl()!=null && doc.getUrl().length()>0 && !doc.getLanguage().equals("unknown")) {
            boolean insertCheck = db.opDocuments.addDocument(doc.getUrl(), doc.getTitle(), doc.getText(), doc.getLanguage());

            //The document was correctly added if there is no duplicate key
            if (insertCheck) {
                HashMap<String, Integer> wordCountLocal = doc.countWords();    //Request the count of words for the inserted document
                for (Map.Entry<String, Integer> termEntry : wordCountLocal.entrySet()) {      //For each term obtained from the document (Key String is the term, Value Integer is the TF)
                    //Write the term, with the document id and the TF
                    db.opInvertedIndex.addTerm(doc.getUrl(), termEntry.getKey(), termEntry.getValue());
                }
            }
        }
    }
}
