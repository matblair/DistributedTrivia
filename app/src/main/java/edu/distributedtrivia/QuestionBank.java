package edu.distributedtrivia;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class QuestionBank {
	private ArrayList<Question> questions;

	public QuestionBank(String filename){
			try {
			// read the json file
			FileReader reader = new FileReader(filename);

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

			// get an array from the JSON object
			JSONArray jsonQuestions= (JSONArray) jsonObject.get("questions");
			questions = new ArrayList<Question>();
			for(int i=0; i<jsonQuestions.size(); i++) {
				JSONObject jsonQuestion = (JSONObject) jsonQuestions.get(i);
				questions.add(new Question(jsonQuestion));
			}

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ParseException ex) {
			ex.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
	}

	public Question nextQuestion(){
		Random rand = new Random();
		int i = rand.nextInt(questions.size());
		return questions.remove(i);
	}
}