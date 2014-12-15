package ua.knure.fb2reader.Book;

public class SimpleSyllables implements SyllablesPartitionable {

    @Override
    public String[] getWordSyllables(String word, int maxLengthOfWord) {
        String tempWord = word;
        StringBuilder strBuildFirstPart = new StringBuilder();
        StringBuilder strBuildSecondPart = new StringBuilder();
        for (int j = 0; j < tempWord.length() / 2; j++) {
            strBuildFirstPart.append(tempWord.charAt(j));
        }
        for (int j = tempWord.length() / 2; j < tempWord.length(); j++) {
            strBuildSecondPart.append(tempWord.charAt(j));
        }
        strBuildFirstPart.append("-");
        String[] wordParts = new String[2];
        wordParts[0] = strBuildFirstPart.toString();
        wordParts[1] = strBuildSecondPart.toString();
        return wordParts;

    }
}
