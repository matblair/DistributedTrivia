package edu.distributedtrivia;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class QuestionBank {
    private ArrayList<Question> questions;

    public QuestionBank(Context context) {
        try {

            // read the json file
            String json = loadJSONFromAsset(context);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(json);

            // get an array from the JSON object
            JSONArray jsonQuestions = (JSONArray) jsonObject.get("questions");
            questions = new ArrayList<Question>();
            for (int i = 0; i < jsonQuestions.size(); i++) {
                JSONObject jsonQuestion = (JSONObject) jsonQuestions.get(i);
                questions.add(new Question(jsonQuestion));
            }

        } catch (ParseException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public int randomQuestionID() {
        int max = questions.size();
        Random rand = new Random();
        return rand.nextInt(max);
    }

    public Question nextQuestion(int ID) throws UnknownQuestion{
        Question response;

        Iterator<Question> iterator = questions.iterator();
        while(iterator.hasNext()){
            Question q = iterator.next();
            if(q.getID() == ID) {
                response = q;
                iterator.remove();
                System.out.println("Foudn question with id " + ID);
                return q;
            }
        }
        // Requested ID doesn't exist
        throw new UnknownQuestion("Couldn't find a question with the ID: " + ID);
    }

    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(Globals.TRIVIA_PATH);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
}