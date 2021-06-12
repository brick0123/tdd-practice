package numberguessing.console;

import java.util.stream.Stream;
import numberguessing.PositiveIntegerGenerator;

public final class AppModel {

  private static final String NEW_LINE = System.lineSeparator();
  public static final String SELECT_MODE_MESSAGE =
      "1: Single player game" + NEW_LINE +
          "2: Multiplayer game" + NEW_LINE +
          "3: Exit" + NEW_LINE + "Enter selection: ";

  private Processor processor;

  interface Processor {

    Processor run(String input);
  }

  private final PositiveIntegerGenerator generator;
  private final StringBuffer outputBuffer;
  private boolean completed;

  public AppModel(PositiveIntegerGenerator generator) {
    this.generator = generator;
    outputBuffer = new StringBuffer(SELECT_MODE_MESSAGE);
    completed = false;
    processor = this::processModeSelection;
  }

  public boolean isCompleted() {
    return completed;
  }

  public String flushOutput() {
    String output = outputBuffer.toString();
    outputBuffer.setLength(0);
    return output;
  }

  public void processInput(String input) {
    processor = processor.run(input);
  }

  private void print(String message) {
    outputBuffer.append(message);
  }

  private Processor processModeSelection(String input) {
    if ("1".equals(input)) {
      println("Single player game");
      println("I'm thinking of a number between 1 and 100.");
      print("Enter your guess: ");
      int answer = generator.generateLessThanOrEqualToHundred();
      return getSinglePlayerGameProcessor(answer, 1);
    } else if ("2".equals(input)) {
      println("Multiplayer game");
      print("Enter player names separated with commas: ");
      return startMultiplayerGame();
    } else {
      completed = true;
    }
    return null;
  }

  private void println(String message) {
    outputBuffer.append(message + NEW_LINE);
  }


  private Processor startMultiplayerGame() {
    return input -> {
      Object[] players = Stream.of(input.split(",")).map(String::trim).toArray();
      println("I'm thinking of a number between 1 and 100.");
      int answer = generator.generateLessThanOrEqualToHundred();
      return getMultiplayerGameProcessor(players, answer, 1);
    };
  }

  private Processor getMultiplayerGameProcessor(Object[] players, int answer, int tries) {
    Object player = players[(tries - 1) % players.length];
    print("Enter " + player + "'s guess: ");
    return input -> {
      int guess = Integer.parseInt(input);
      if (guess < answer) {
        println(player + "'s guess is too low.");
        return getMultiplayerGameProcessor(players, answer, tries + 1);
      } else if (guess > answer) {
        println(player + "'s guess is too high.");
        return getMultiplayerGameProcessor(players, answer, tries + 1);
      } else {
        print("Correct! ");
        println(player + " wins.");
        print(SELECT_MODE_MESSAGE);
        return this::processModeSelection;
      }
    };
  }

  private Processor getSinglePlayerGameProcessor(int answer, int triesCount) {
    return input -> {
      int guess = Integer.parseInt(input);
      if (guess < answer) {
        println("Your guess is too low.");
        print("Enter your guess: ");
        return getSinglePlayerGameProcessor(answer, triesCount + 1);
      } else if (guess > answer) {
        println("Your guess is too high.");
        print("Enter your guess: ");
        return getSinglePlayerGameProcessor(answer, triesCount + 1);
      } else {
        println("Correct! " + triesCount + (triesCount == 1 ? " guess." : " guesses."));
        print(SELECT_MODE_MESSAGE);
        return this::processModeSelection;
      }
    };
  }
}
