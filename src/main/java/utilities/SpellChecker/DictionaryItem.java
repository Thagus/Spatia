package utilities.SpellChecker;

import java.util.ArrayList;
import java.util.List;

class DictionaryItem {
    List<Integer> suggestions;
    int count;

    DictionaryItem(){
        this.suggestions = new ArrayList<>();
        this.count = 0;
    }
}
