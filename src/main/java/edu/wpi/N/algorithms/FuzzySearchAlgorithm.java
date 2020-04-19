package edu.wpi.N.algorithms;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DbController;
import edu.wpi.N.entities.DbNode;
import java.util.LinkedList;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class FuzzySearchAlgorithm {

  /**
   * Function to output a list of Long Node Names that match user's input with consideration of
   * spelling errors
   *
   * @param userInput
   * @return
   */
  public static LinkedList<String> suggestWithCorrection(String userInput) throws DBException {

    if (userInput.length() > 0) {
      // initialize variables
      LinkedList<String> suggestions = new LinkedList<String>();
      // search for all nodes by long name
      LinkedList<DbNode> suggestedNodes = DbController.searchVisNode(-1, null, null, userInput);
      if (suggestedNodes.size() != 0) {
        for (DbNode node : suggestedNodes) {
          suggestions.add(node.getLongName());
        }
      } else {
        // if 5 or more letters in user's input
        if (userInput.replaceAll("\\s+", "").length() > 4) {
          // Get a single longest word in user's string
          String inputWord = getLongestWord(userInput);
          // // Do fuzzy search
          suggestions = performFuzzySearch(inputWord);
        }
      }
      return suggestions;
    } else return null;
  }

  /**
   * Helper function which corrects user input and outputs suggestions
   *
   * @param userInput: incorrect User input
   * @return: suggestions based on corrected user's input
   */
  private static LinkedList<String> performFuzzySearch(String userInput) throws DBException {
    userInput = userInput.toLowerCase();
    LinkedList<String> suggestions = new LinkedList<String>();

    double ratio = 0.8;

    // Get all the visible nodes from DB
    for (DbNode node : DbController.searchVisNode(-1, null, null, "")) {
      String fullLongName = node.getLongName();

      // Iterate through Long Name's words
      for (String s : fullLongName.toLowerCase().split(" ")) {

        // Check that the word is >= than (user's word size - 2)
        if (userInput.length() - 2 <= s.length()) {

          // calculate levenshtein distance between the 2 strings (input word and Long Name word)
          LevenshteinDistance distance = new LevenshteinDistance();
          int d = distance.apply(userInput, s);

          // calculate ratio
          double lensum = s.length() + userInput.length();
          if ((lensum - d) / (lensum) >= ratio) {
            suggestions.add(fullLongName);
          }
        }
      }
    }
    return suggestions;
  }

  /**
   * Outputs the longest single word from User's input
   *
   * @param userInput: user input as string
   * @return: the longest single word in user's input
   */
  private static String getLongestWord(String userInput) {
    String longestSoFar = "";

    for (String s : userInput.split(" ")) {
      if (s.length() > longestSoFar.length()) {
        longestSoFar = s;
      }
    }
    return longestSoFar;
  }
}
