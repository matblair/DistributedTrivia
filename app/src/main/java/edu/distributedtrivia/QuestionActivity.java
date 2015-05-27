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
import edu.distributedtrivia.paxos.PaxosMessage;


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

    private Button answerA;
    private Button answerB;
    private Button answerC;
    private Button answerD;
    private Button buzzer;


    // Timer for countdown to choose answer
    private boolean noProposal;
    private int countdownTimer;
    private Answer chosenAnswer;

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
                chosenAnswer = roundQuestion.getA();
                startCountdownTimer();
            }
        });

        answerB = (Button) this.findViewById(R.id.answerB);
        answerB.setText("B: " + roundQuestion.getB().getAnswer());
        answerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chosenAnswer = roundQuestion.getB();
                startCountdownTimer();
            }
        });

        answerC = (Button) this.findViewById(R.id.answerC);
        answerC.setText("C: " + roundQuestion.getC().getAnswer());
        answerC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chosenAnswer = roundQuestion.getC();
                startCountdownTimer();
            }
        });

        answerD = (Button) this.findViewById(R.id.answerD);
        answerD.setText("D: " + roundQuestion.getD().getAnswer());
        answerD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chosenAnswer = roundQuestion.getD();
                startCountdownTimer();
            }
        });

        disableAnswers();

        buzzer = (Button) this.findViewById(R.id.go);
        buzzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buzzTime = System.nanoTime() - startTime;

                Toast.makeText(QuestionActivity.this, Long.toString(buzzTime), Toast.LENGTH_LONG).show();
                notifyBuzz();

                enableAnswers();
                buzzer.setEnabled(false);
            }
        });

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
    }

    public void startCountdownTimer(){
        Thread t = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    System.out.println("I have started!");
                    countdownTimer = INITIAL_COUNTDOWN;
                    while(noProposal && (countdownTimer>0)){
                        // Set the value on the text field!
                        Thread.sleep(100);
                        countdownTimer -= 1;
                        break;
                    }
                    verifyWinner();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }});
        t.start();
    }


    // Method to update the view
    public void notifyActivity(PaxosHandler.Actions action){
        switch (action){
            case PROPOSAL:
                // We have received a proposal, we don't have to wait anymore!
                // The case when someone you won!
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QuestionActivity.this, "To Slow!! Waiting For Results", Toast.LENGTH_LONG).show();
//                        nextScreen();
                    }});
                noProposal = false;
                break;
            case FIRST:
                // The case when someone you won!
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QuestionActivity.this, "Yay you won this round!!", Toast.LENGTH_LONG).show();
//                        nextScreen();
                    }});
                break;
            case ANSWERED:
                // The case when someone else won, not you
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QuestionActivity.this, "Sorry, someone else won that question!", Toast.LENGTH_LONG).show();
//                        nextScreen();
                    }});

                break;
            case BUZZED:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Yo I got here!");
                        Toast.makeText(QuestionActivity.this, "Someone else buzzed in!", Toast.LENGTH_LONG).show();
                    }});
                break;
        }
    }

    public void nextScreen(){
        Intent i = new Intent();
        i.setClass(QuestionActivity.this, ResultsActivity.class);
        startActivity(i);
    }

    public void verifyWinner() {
        // Pick the fastest person
        String fastest = Globals.gs.getFastestPlayer();

        // Get the handler
        PaxosHandler handler = PaxosHandler.getHandler(Globals.userPlayer.getName());
        PaxosMessage message = handler.proposeWinnerMsg(fastest);

        // Propose new round
        handler.proposeNewRound(message);
    }

    public void generalisedAnswer(Answer answer) {
        // We know we won, lets see what we have to do
        boolean result = roundQuestion.verifyAnswer(answer);

        // We want to update our score
        Globals.userPlayer.updateScore(result);
        Globals.gs.updatePlayer(Globals.userPlayer, result);

        // Get the score
        PaxosHandler handler = PaxosHandler.getHandler(Globals.userPlayer.getName());
        PaxosMessage message = handler.proposeScoreMsg(Globals.userPlayer.getName(), result);
        handler.proposeNewRound(message);
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
