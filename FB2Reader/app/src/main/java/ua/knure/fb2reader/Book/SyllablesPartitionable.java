package ua.knure.fb2reader.Book;

/**
 * Created by Александр on 12.11.2014.
 */
public interface SyllablesPartitionable {
    public String[] getWordSyllables(String word, int maxLengthOfWord);
}
