package edu.distributedtrivia;

public class Answer {
	private char id;
	private String answer;
		
	public Answer (char c, String answer){
		this.id = c;
		this.answer = answer;
	}

	public char getId() {
		return id;
	}

	public String getAnswer() {
		return answer;
	}
	
}
