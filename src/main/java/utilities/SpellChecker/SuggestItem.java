package utilities.SpellChecker;

class SuggestItem {
    String term;
    int distance;
    int count;

    SuggestItem() {
        this.term = "";
        this.distance = 0;
        this.count = 0;
    }

    @Override
    public boolean equals(Object obj) {
        return term.equals(((SuggestItem)obj).term);
    }

    @Override
    public int hashCode() {
        return term.hashCode();
    }
}
