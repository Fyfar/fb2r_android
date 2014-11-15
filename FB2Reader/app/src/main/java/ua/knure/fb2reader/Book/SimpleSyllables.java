package ua.knure.fb2reader.Book;

/**
 * Created by Александр on 15.11.2014.
 */
public class SimpleSyllables implements SyllablesPartitionable {

    @Override
    public String[] getWordSyllables(String word, int maxLengthOfWord) {
        StringBuilder chars = new StringBuilder();
        String[] result = new String[2];
        for (int i = 0; i < maxLengthOfWord && i < word.length(); i++) {
            chars.append(word.charAt(i));
        }
        result[0] = chars.toString();
        chars = new StringBuilder();
        chars.append('-');
        for (int i = maxLengthOfWord; i < word.length(); i++) {
            chars.append(word.charAt(i));
        }
        result[1] = chars.toString();
        return result;
    }
}
