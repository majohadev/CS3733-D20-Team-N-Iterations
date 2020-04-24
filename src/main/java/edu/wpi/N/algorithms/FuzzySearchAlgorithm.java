package edu.wpi.N.algorithms;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DoctorDB;
import edu.wpi.N.database.MapDB;
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
    int inputLength = userInput.replaceAll("\\s+", "").length();
    int lowestDistanceSoFar = 1000;
    userInput = userInput.trim().toLowerCase();

    if (userInput.length() > 1) {

      // search for all nodes by long name
      LinkedList<DbNode> suggestedNodes = MapDB.searchVisNode(-1, null, null, userInput);
      if (suggestedNodes.size() != 0) {

        for (DbNode node : suggestedNodes) {

          // Identify in which order to put the suggestions (most relevant -> less relevant)
          LevenshteinDistance distance = new LevenshteinDistance();
          int d = distance.apply(userInput, node.getLongName());
          if (d < lowestDistanceSoFar) {
            suggestions.addFirst(node);
            lowestDistanceSoFar = d;
          } else {
            suggestions.add(node);
          }
        }
      } else {
        // if 5 or more letters in user's input (not including space)
        if (inputLength > 4) {
          // // Do fuzzy search
          suggestions = performFuzzySearchOnLocations(userInput);
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
    String[] userInputByWord = userInput.split(" ");
    double bestRatioSoFar = 0;

    double ratio = 0.85;

    // Get all the visible nodes from DB
    for (DbNode node : MapDB.searchVisNode(-1, null, null, "")) {
      String fullLongName = node.getLongName();
      String[] longNameWords = fullLongName.toLowerCase().split(" ");
      // Iterate through Long Name's words
      for (String location : longNameWords) {
        double r = 0;
        // Calculate ration of every Long Name to Every user's word
        for (String userWord : userInputByWord) {

          // Check that the word is >= than (user's word size - 1)
          if (userWord.length() - 1 <= location.length()) {

            // calculate levenshtein distance between the 2 strings (input word and Long Name word)
            LevenshteinDistance distance = new LevenshteinDistance();
            int d = distance.apply(userWord, location);

            // calculate ratio
            double lensum = location.length() + userWord.length();
            r = r + (lensum - d) / (lensum);
          }
        }

        if (r >= ratio) {
          // add the suggestions in proper order based on how relevant they are (most -> least)
          if (r >= bestRatioSoFar) {
            suggestions.addFirst(node);
            bestRatioSoFar = r;
          } else {
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
    int inputLength = userInput.replaceAll("\\s+", "").length();
    int lowestDistanceSoFar = 1000;
    userInput = userInput.trim();

    if (userInput.length() > 1) {
      // search for all nodes by long name
      LinkedList<Doctor> suggestedDoctors = DoctorDB.searchDoctors(userInput);
      if (suggestedDoctors.size() != 0) {
        for (Doctor doc : suggestedDoctors) {

          // Identify in which order to put the suggestions (most relevant -> less relevant)
          LevenshteinDistance distance = new LevenshteinDistance();
          int d = distance.apply(userInput, doc.getName());
          if (d < lowestDistanceSoFar) {
            suggestions.addFirst(doc);
            lowestDistanceSoFar = d;
          } else {
            suggestions.add(doc);
          }
        }
      } else {
        // if 5 or more letters in user's input (not including space)
        if (userInput.replaceAll("\\s+", "").length() > 4) {
          // Get a single longest word in user's string
          String inputWord = getLongestWord(userInput);
          // // Do fuzzy search
          suggestions = performFuzzySearchOnDoctors(inputWord, inputLength);
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
  private static LinkedList<Doctor> performFuzzySearchOnDoctors(String userInput, int numInputWords)
      throws DBException {
    userInput = userInput.toLowerCase();
    LinkedList<Doctor> suggestions = new LinkedList<Doctor>();

    double ratio = 0.8;
    double bestRatioSoFar = 0;

    // Get all the visible nodes from DB
    for (Doctor doc : DoctorDB.getDoctors()) {
      String fullName = doc.getName();
      String[] longNameWords = fullName.toLowerCase().split(" ");

      // Iterate through Long Name's words
      for (String s : fullName.toLowerCase().split(" ")) {

        // Check that the word is >= than (user's word size - 2)
        if (userInput.length() - 2 <= s.length()) {

          // calculate levenshtein distance between the 2 strings (input word and Long Name word)
          LevenshteinDistance distance = new LevenshteinDistance();
          int d = distance.apply(userInput, s);

          // calculate ratio
          double lensum = s.length() + userInput.length();
          double r = (lensum - d) / (lensum);
          if (r >= ratio) {
            // add the suggestions in proper order based on how relevant they are (most -> least)
            if (r >= bestRatioSoFar && numInputWords == longNameWords.length) {
              suggestions.addFirst(doc);
              bestRatioSoFar = r;
            } else {
              suggestions.add(doc);
            }
          }
        }
      }
    }
    // suggestions
    return suggestions;
  }
}
