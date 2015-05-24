package edu.distributedtrivia;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class QuestionActivity extends ActionBarActivity {

    Long startTime;
    Long buzzTime;
    Question roundQuestion;

    TextView roundNumber;
    TextView currentPosition;
    TextView questionText;
    Button answerA;
    Button answerB;
    Button answerC;
    Button answerD;
    Button buzzer;
    Button slowBuzzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        roundQuestion = Globals.gs.getCurrentQuestion();

        roundNumber = (TextView) this.findViewById(R.id.roundNumber);
        roundNumber.setText("Question " + Globals.gs.getRoundNum() + " of " + Globals.gs.getNumRounds());

        currentPosition = (TextView) this.findViewById(R.id.currentPosition);
        currentPosition.setText("Postion: " + Globals.userPlayer.getPosition());

        questionText = (TextView) this.findViewById(R.id.questionText);
        questionText.setText(roundQuestion.getQuestion());

        answerA = (Button) this.findViewById(R.id.answerA);
        answerA.setText("A: " + roundQuestion.getA().getAnswer());
        answerA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = roundQuestion.verifyAnswer(roundQuestion.getA());
                Globals.userPlayer.updateScore(result);
                Globals.gs.updatePlayer(Globals.userPlayer, result);

                //TODO replicate state across contenstents

                Intent i = new Intent();
                i.setClass(QuestionActivity.this, ResultsActivity.class);
                startActivity(i);
            }
        });

        answerB = (Button) this.findViewById(R.id.answerB);
        answerB.setText("B: " + roundQuestion.getB().getAnswer());
        answerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = roundQuestion.verifyAnswer(roundQuestion.getB());
                Globals.userPlayer.updateScore(result);
                Globals.gs.updatePlayer(Globals.userPlayer, result);

                //TODO replicate state across contenstents

                Intent i = new Intent();
                i.setClass(QuestionActivity.this, ResultsActivity.class);
                startActivity(i);
            }
        });

        answerC = (Button) this.findViewById(R.id.answerC);
        answerC.setText("C: " + roundQuestion.getC().getAnswer());
        answerC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = roundQuestion.verifyAnswer(roundQuestion.getC());
                Globals.userPlayer.updateScore(result);
                Globals.gs.updatePlayer(Globals.userPlayer, result);

                //TODO replicate state across contenstents

                Intent i = new Intent();
                i.setClass(QuestionActivity.this, ResultsActivity.class);
                startActivity(i);
            }
        });

        answerD = (Button) this.findViewById(R.id.answerD);
        answerD.setText("D: " + roundQuestion.getD().getAnswer());
        answerD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = roundQuestion.verifyAnswer(roundQuestion.getD());
                Globals.userPlayer.updateScore(result);
                Globals.gs.updatePlayer(Globals.userPlayer, result);

                //TODO replicate state across contenstents

                Intent i = new Intent();
                i.setClass(QuestionActivity.this, ResultsActivity.class);
                startActivity(i);
            }
        });

        disableAnswers();

        buzzer = (Button) this.findViewById(R.id.go);
        buzzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buzzTime = System.nanoTime() - startTime;
                Toast.makeText(QuestionActivity.this, Long.toString(buzzTime), Toast.LENGTH_LONG).show();

                //TODO Reach consensus on timing

                enableAnswers();
            }
        });

        slowBuzzer = (Button) this.findViewById(R.id.goSlow);
        slowBuzzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buzzTime = System.nanoTime() - startTime;
                Toast.makeText(QuestionActivity.this, Long.toString(buzzTime), Toast.LENGTH_LONG).show();

                //TODO Reach consensus without Paxos

                enableAnswers();
            }
        });
        if (Globals.gs.isPaxos()){
            slowBuzzer.setVisibility(View.VISIBLE);
        }else{
            slowBuzzer.setVisibility(View.INVISIBLE);
        }

        startTime = System.nanoTime();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void enableAnswers(){
        answerA.setClickable(true);
        answerB.setClickable(true);
        answerC.setClickable(true);
        answerD.setClickable(true);
    }

    public void disableAnswers(){
        answerA.setClickable(false);
        answerB.setClickable(false);
        answerC.setClickable(false);
        answerD.setClickable(false);
    }
}
