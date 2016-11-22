package utilities;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

import javax.swing.JOptionPane;

/**
 * Created by Thagus on 21/11/16.
 */
public class LanguageDetector {

    public static void initialize(){
        try{
            DetectorFactory.loadProfile(TermExtractor.class.getResource("/languageProfiles").getFile());
        } catch (LangDetectException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error loading language profiles", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Identifies the language of a given text
     * @param text the text we want the language for
     * @return the language
     */
    public static String detectLanguage(String text) {
        try {
            Detector detector = DetectorFactory.create();
            detector.append(text);
            return detector.detect();
        } catch (LangDetectException e) {
            e.printStackTrace();
        }
        System.out.println("Unknown language in: " + text);
        return "unknown";
    }
}
