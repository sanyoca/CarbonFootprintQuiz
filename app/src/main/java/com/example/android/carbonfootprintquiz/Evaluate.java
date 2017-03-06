package com.example.android.carbonfootprintquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class Evaluate extends AppCompatActivity {
    int intCorrect, intGivenAnswers[];
    String stringName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evaluate);

        Intent previousIntent = getIntent();

        // get the number of correct answers
        intCorrect = previousIntent.getIntExtra("correctAnswers", 0);
        // get the user's answers
        intGivenAnswers = previousIntent.getIntArrayExtra("userAnswers");
        // get the user's name
        stringName = previousIntent.getStringExtra("name");
        // get the total number of questions
        int intHowManyQuestions = Integer.valueOf(getString(R.string.howmanyquestions));

        // display the user's name
        TextView message = (TextView) findViewById(R.id.name_textview);
        message.setText(stringName);

        // display the evaluation texts
        TextView outOf = (TextView) findViewById(R.id.outof);
        outOf.setText(getString(R.string.outof) + " " + String.valueOf(intHowManyQuestions) + " " +getString(R.string.answered));

        TextView correct = (TextView) findViewById(R.id.correct);
        correct.setText(String.valueOf(intCorrect));
    }
}