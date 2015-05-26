package edu.distributedtrivia;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jburd on 24/05/15.
 */
public class Globals {
    public static final int MIN_ROUNDS = 3;
    public static final int MAX_ROUNDS = 10;
    public static final int DEFAULT_ROUNDS = 5;
    public static final int CORRECT_SCORE = 100;
    public static final int INCORRECT_SCORE = 50;
    public static final String TRIVIA_PATH = "trivia_questions.json";

    public static GameState gs;
    public static Player userPlayer;
    public static boolean host;
    public static List<String> userNames = new ArrayList<String>();
}
