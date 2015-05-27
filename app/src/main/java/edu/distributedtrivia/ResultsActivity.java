package edu.distributedtrivia;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import edu.distributedtrivia.paxos.PaxosHandler;
import edu.distributedtrivia.paxos.PaxosMessage;


public class ResultsActivity extends NotifiableActivity {

    boolean gameFinished;

    TextView victoryText;
    ListView resultsList;
    Button nextQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        gameFinished = (Globals.gs.getRoundNum() == Globals.gs.getNumRounds());

        victoryText = (TextView) this.findViewById(R.id.victoryText);
        if (gameFinished){
            victoryText.setText("Final Results");
        }else {
            String answerResult = (Globals.gs.isCorrectAnswer()) ? "corect" : "incorrect";
            victoryText.setText(Globals.gs.getPlayerAnswered().getName() + " answered " + answerResult);
        }

        nextQuestion = (Button) this.findViewById(R.id.nextQuestion);
        if (gameFinished) {
            nextQuestion.setText("Main Menu");
        }else {
                nextQuestion.setText("Next Question");
        }
        nextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameFinished){
                    Intent i = new Intent();
                    i.setClass(ResultsActivity.this, MainActivity.class);
                    startActivity(i);
                }else {
                    // Get the paxos handler and get ready to send
                    PaxosHandler handler = PaxosHandler.getHandler(Globals.userPlayer.getName());
                    int qID = Globals.gs.nextQuestion();
                    PaxosMessage message = handler.proposeQuestionMsg(qID);
                    handler.proposeNewRound(message);
                }
            }
        });

        resultsList = (ListView) this.findViewById(R.id.contestantList);
        ArrayAdapter adapter = new ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                Globals.gs.getPlayers()
        );

        resultsList.setAdapter(adapter);
    }

//    public void startCountdownTimer(){
//        Thread t = new Thread(new Runnable(){
//            @Override
//            public void run() {
//                try {
//                    countdownTimer = INITIAL_COUNTDOWN;
//                    while(noProposal && (countdownTimer>0)){
//                        // Set the value on the text field!
//                        Thread.sleep(100);
//                        countdownTimer -= 1;
//                        break;
//                    }
//                    verifyWinner();
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//            }});
//        t.start();
//    }

    public void nextScreen() {
        Intent i = new Intent();
        i.setClass(ResultsActivity.this, QuestionActivity.class);
        startActivity(i);
    }


    // Method to update
    public void notifyActivity(PaxosHandler.Actions action){
        switch(action){
            case NEXT_SCREEN:
                nextScreen();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_results, menu);
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
}
