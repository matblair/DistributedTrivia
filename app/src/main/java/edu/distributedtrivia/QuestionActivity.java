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

import edu.distributedtrivia.paxos.PaxosHandler;


public class QuestionActivity extends NotifiableActivity {
    // Static Variables
    private static final int INITIAL_COUNTDOWN=30;

    // Class variables
    private Long startTime;
    private Long buzzTime;
    private Question roundQuestion;

    // View elements
    private TextView roundNumber;
    private TextView currentPosition;
    private TextView questionText;
    private TextView timeRemaining;

    private Button answerA;
    private Button answerB;
    private Button answerC;
    private Button answerD;
    private Button buzzer;
    private Button slowBuzzer;

    // Timer for countdown to choose answer
    private boolean showTimer;
    private int countdownTimer;

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

        timeRemaining = (TextView) this.findViewById(R.id.timeRemaining);
        timeRemaining.setVisibility(View.INVISIBLE);
        timeRemaining.setText("");

        answerA = (Button) this.findViewById(R.id.answerA);
        answerA.setText("A: " + roundQuestion.getA().getAnswer());
        answerA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generalisedAnswer(roundQuestion.getA());
            }
        });

        answerB = (Button) this.findViewById(R.id.answerB);
        answerB.setText("B: " + roundQuestion.getB().getAnswer());
        answerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generalisedAnswer(roundQuestion.getB());
            }
        });

        answerC = (Button) this.findViewById(R.id.answerC);
        answerC.setText("C: " + roundQuestion.getC().getAnswer());
        answerC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generalisedAnswer(roundQuestion.getC());
            }
        });

        answerD = (Button) this.findViewById(R.id.answerD);
        answerD.setText("D: " + roundQuestion.getD().getAnswer());
        answerD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generalisedAnswer(roundQuestion.getD());
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
                buzzer.setEnabled(false);
                slowBuzzer.setEnabled(false);
            }
        });

        slowBuzzer = (Button) this.findViewById(R.id.goSlow);
        slowBuzzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buzzTime = System.nanoTime() - startTime;
                notifyBuzz();

                enableAnswers();
                buzzer.setEnabled(false);
                slowBuzzer.setEnabled(false);
            }
        });
        if (Globals.gs.isPaxos()){
            slowBuzzer.setVisibility(View.INVISIBLE);
        }else{
            slowBuzzer.setVisibility(View.VISIBLE);
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

    public void notifyBuzz(){
        buzzTime = System.nanoTime() - startTime;
        Toast.makeText(QuestionActivity.this, Long.toString(buzzTime), Toast.LENGTH_LONG).show();

        // Get the paxos handler and get ready to send
        PaxosHandler handler = PaxosHandler.getHandler(Globals.userPlayer.getName());
        handler.sendTime(Globals.userPlayer.getName(), buzzTime);

        // Update feedback
        countdownTimer = INITIAL_COUNTDOWN;
        startCountdownTimer();
    }

    public void startCountdownTimer(){
        Thread t = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    timeRemaining.setVisibility(View.VISIBLE);
                    timeRemaining.setText(countdownTimer);
                    while(showTimer && (countdownTimer>0)){
                        // Set the value on the text field!
                        Thread.sleep(1000);
                        countdownTimer -= 1;
                        timeRemaining.setText(countdownTimer);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }});
        t.start();
    }


    // Method to update the view
    public void notifyActivity(PaxosHandler.Actions action){
        switch (action){
            case ANSWERED:
                showTimer = false;
                break;
            case BUZZED:
                Toast.makeText(QuestionActivity.this, "Someone else buzzed in!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void generalisedAnswer(Answer answer){
        boolean result = roundQuestion.verifyAnswer(answer);
        Globals.userPlayer.updateScore(result);
        Globals.gs.updatePlayer(Globals.userPlayer, result);
        Intent i = new Intent();
        i.setClass(QuestionActivity.this, ResultsActivity.class);
        startActivity(i);
    }

    public void enableAnswers(){
        answerA.setEnabled(true);
        answerB.setEnabled(true);
        answerC.setEnabled(true);
        answerD.setEnabled(true);
    }

    public void disableAnswers(){
        answerA.setEnabled(false);
        answerB.setEnabled(false);
        answerC.setEnabled(false);
        answerD.setEnabled(false);
    }
}
