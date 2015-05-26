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


public class ResultsActivity extends ActionBarActivity {

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
                    Globals.gs.nextRound(9);

                    //TODO Distribute new round state with everyone else

                    Intent i = new Intent();
                    i.setClass(ResultsActivity.this, QuestionActivity.class);
                    startActivity(i);
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
