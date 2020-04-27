package edu.wpi.N.algorithms;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DoctorDB;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.employees.Doctor;
import java.util.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
    LinkedList<DbNode> suggestions = new LinkedList<DbNode>();
    // Get rid of any pretexts
    List<String> userInputByWord = new ArrayList<String>(Arrays.asList(userInput.split(" ")));
    int numUserWords = userInputByWord.size();
    userInputByWord.removeIf(
        s -> {
          return (s.equals("and") || s.equals("&") || s.equals("of") || s.equals("to"));
        });

    double bestRatioSoFar = 0;

    double ratio = 0.8;

    // Get all the visible nodes from DB
    for (DbNode node : MapDB.searchVisNode(-1, null, null, "")) {

      String fullLongName = node.getLongName();
      String[] longNameWords = fullLongName.toLowerCase().split(" ");
      double r = 0;

      // Iterate through Long Name's words
      for (String location : longNameWords) {
        // Calculate ration of every Long Name to Every user's word
        for (String userWord : userInputByWord) {

          // Check that the word is >= than (user's word size - 1)
          if (userWord.length() - 1 <= location.length()) {

            // calculate levenshtein distance between the 2 strings (input word and Long Name word)
            LevenshteinDistance distance = new LevenshteinDistance();
            int d = distance.apply(userWord, location);

            // calculate ratio
            double lensum = location.length() + userWord.length();
            double currentRatio = (lensum - d) / (lensum);
            if (currentRatio >= ratio) {
              r = r + currentRatio;
            }
          }
        }
      }

      // add the suggestions in proper order based on how relevant they are (most -> least)
      if (r >= bestRatioSoFar && r >= ratio && numUserWords <= longNameWords.length) {
        suggestions.addFirst(node);
        bestRatioSoFar = r;
      } else if (r >= ratio) {
        suggestions.add(node);
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
    userInput = userInput.trim().toLowerCase();

    if (userInput.length() > 1) {

      // search for all nodes by long name
      LinkedList<Doctor> suggestedNodes = DoctorDB.searchDoctors(userInput);
      if (suggestedNodes.size() != 0) {

        for (Doctor doc : suggestedNodes) {

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
        if (inputLength > 4) {
          // // Do fuzzy search
          suggestions = performFuzzySearchOnDoctors(userInput);
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

    LinkedList<Doctor> suggestions = new LinkedList<Doctor>();

    // Get rid of any pretexts
    List<String> userInputByWord = new ArrayList<String>(Arrays.asList(userInput.split(" ")));
    int numUserWords = userInputByWord.size();
    userInputByWord.removeIf(
        s -> {
          return (s.equals("and") || s.equals("&") || s.equals("of") || s.equals("to"));
        });

    double ratio = 0.8;
    double bestRatioSoFar = 0;

    // Get all the visible nodes from DB
    for (Doctor doc : DoctorDB.getDoctors()) {
      String fullName = doc.getName();
      String[] fullNameWords = fullName.toLowerCase().split(" ");

      double r = 0;

      // Iterate through Long Name's words
      for (String doctorName : fullNameWords) {

        // Calculate ratio of every Long Name to Every user's word
        for (String userWord : userInputByWord) {

          // Check that the word is >= than (user's word size - 1)
          if (userWord.length() - 1 <= doctorName.length()) {

            // calculate levenshtein distance between the 2 strings (input word and Long Name word)
            LevenshteinDistance distance = new LevenshteinDistance();
            int d = distance.apply(userWord, doctorName);

            // calculate ratio
            double lensum = doctorName.length() + userWord.length();
            double currentRatio = (lensum - d) / (lensum);
            if (currentRatio >= ratio) {
              r = r + currentRatio;
            }
          }
        }
      }

      // add the suggestions in proper order based on how relevant they are (most -> least)
      if (r >= bestRatioSoFar && r >= ratio && numUserWords <= fullNameWords.length) {
        suggestions.addFirst(doc);
        bestRatioSoFar = r;
      } else if (r >= ratio) {
        suggestions.add(doc);
      }
    }
    return suggestions;
  }
}
