package edu.wpi.N.algorithms;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DbController;
import edu.wpi.N.database.DoctorController;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Doctor;
import java.util.LinkedList;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class FuzzySearchAlgorithm {

  /**
   * Function to output a list of DbNodes that match user's input with consideration of spelling
   * errors
   *
   * @param userInput
   * @return list of suggested DbNodes (locations)
   */
  public static LinkedList<DbNode> suggestLocations(String userInput) throws DBException {
    // initialize variables
    LinkedList<DbNode> suggestions = new LinkedList<DbNode>();

    if (userInput.length() > 1) {

      // search for all nodes by long name
      LinkedList<DbNode> suggestedNodes = DbController.searchVisNode(-1, null, null, userInput);
      if (suggestedNodes.size() != 0) {
        for (DbNode node : suggestedNodes) {
          suggestions.add(node);
        }
      } else {
        // if 5 or more letters in user's input (not including space)
        if (userInput.replaceAll("\\s+", "").length() > 4) {
          // Get a single longest word in user's string
          String inputWord = getLongestWord(userInput);
          // // Do fuzzy search
          suggestions = performFuzzySearchOnLocations(inputWord);
        }
      }
    }
    return suggestions;
  }

  /**
   * Helper function which corrects user input and outputs suggested locations
   *
   * @param userInput: incorrect User input
   * @return: suggestions based on corrected user's input
   */
  private static LinkedList<DbNode> performFuzzySearchOnLocations(String userInput)
      throws DBException {
    userInput = userInput.toLowerCase();
    LinkedList<DbNode> suggestions = new LinkedList<DbNode>();

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
            suggestions.add(node);
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

  /**
   * Function to output a list of Doctors that match user's input with consideration of spelling
   * errors
   *
   * @param userInput
   * @return List of suggested doctors
   */
  public static LinkedList<Doctor> suggestDoctors(String userInput) throws DBException {

    // initialize variables
    LinkedList<Doctor> suggestions = new LinkedList<Doctor>();

    if (userInput.length() > 1) {
      // search for all nodes by long name
      LinkedList<Doctor> suggestedDoctors = DoctorController.searchDoctors(userInput);
      if (suggestedDoctors.size() != 0) {
        for (Doctor doc : suggestedDoctors) {
          suggestions.add(doc);
        }
      } else {
        // if 5 or more letters in user's input (not including space)
        if (userInput.replaceAll("\\s+", "").length() > 4) {
          // Get a single longest word in user's string
          String inputWord = getLongestWord(userInput);
          // // Do fuzzy search
          suggestions = performFuzzySearchOnDoctors(inputWord);
        }
      }
    }
    return suggestions;
  }

  /**
   * Helper function which corrects user input and outputs suggested locations
   *
   * @param userInput: incorrect User input
   * @return: suggestions based on corrected user's input
   */
  private static LinkedList<Doctor> performFuzzySearchOnDoctors(String userInput)
      throws DBException {
    userInput = userInput.toLowerCase();
    LinkedList<Doctor> suggestions = new LinkedList<Doctor>();

    double ratio = 0.8;

    // Get all the visible nodes from DB
    for (Doctor doc : DoctorController.searchDoctors("")) {
      String fullName = doc.getName();

      // Iterate through Long Name's words
      for (String s : fullName.toLowerCase().split(" ")) {

        // Check that the word is >= than (user's word size - 2)
        if (userInput.length() - 2 <= s.length()) {

          // calculate levenshtein distance between the 2 strings (input word and Long Name word)
          LevenshteinDistance distance = new LevenshteinDistance();
          int d = distance.apply(userInput, s);

          // calculate ratio
          double lensum = s.length() + userInput.length();
          if ((lensum - d) / (lensum) >= ratio) {
            suggestions.add(doc);
          }
        }
      }
    }
    // suggestions
    return suggestions;
  }
}
