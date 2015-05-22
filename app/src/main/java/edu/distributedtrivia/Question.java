package edu.distributedtrivia;

import org.json.simple.JSONObject;


public class Question {
	private String question;
	private Answer a;
	private Answer b;
	private Answer c;
	private Answer d;
	private char answer;
	
	public Question (JSONObject jsonQuestion){
		question = (String) jsonQuestion.get("question");
		JSONObject answers = (JSONObject) jsonQuestion.get("answers");
		a = new Answer('a', (String) answers.get("a"));
		b = new Answer('b', (String) answers.get("b"));
		c = new Answer('c', (String) answers.get("c"));
		d = new Answer('d', (String) answers.get("d"));
		String jsonAnswer = (String) jsonQuestion.get("answer");
		answer = jsonAnswer.charAt(0);
	}

	public String getQuestion() {
		return question;
	}

	public Answer getA() {
		return a;
	}

	public Answer getB() {
		return b;
	}

	public Answer getC() {
		return c;
	}

	public Answer getD() {
		return d;
	}
	
	public boolean verifyAnswer(Answer proposed) {
		if(proposed.getId() == answer) {
			return true;
		}
		return false;
	}
	
	
}
