package com.example.android.carbonfootprintquiz;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Evaluate extends AppCompatActivity {
    int intCorrect, intGivenAnswers[];
    String stringName, goodAnswer, badAnswer;
    TextView questionNoToDisplay, questionToDisplay, answerEvaluation, correction;
    View line;

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

        // display the user's name
        TextView message = (TextView) findViewById(R.id.name_textview);
        message.setText(stringName);

        // get the total number of questions
        int intHowManyQuestions = Integer.valueOf(getString(R.string.howmanyquestions));

        // display the evaluation texts
        TextView outOf = (TextView) findViewById(R.id.outof);
        outOf.setText(getString(R.string.outof) + " " + String.valueOf(intHowManyQuestions) + " " + getString(R.string.answered));
        TextView correct = (TextView) findViewById(R.id.correct);
        correct.setText(String.valueOf(intCorrect));

        // get the view, into the evaluation will be inserted
        LinearLayout listOfAnswers = (LinearLayout) findViewById(R.id.listofanswers);

        // get the question string array from strings.xml
        Resources questionsRes = getResources();
        String[] questions = questionsRes.getStringArray(R.array.questions);

        // get the answers for each questions from strings.xml
        Resources optionsRes = getResources();
        String[] options = optionsRes.getStringArray(R.array.options);

        // get the number of answers for each question from strings.xml
        Resources numberOfAnswersRes = getResources();
        String[] numbers = numberOfAnswersRes.getStringArray(R.array.numberofanswers);

        // get the correct answers for each questions from strings.xml
        Resources correctRes = getResources();
        String[] correctAnswers = correctRes.getStringArray(R.array.correctanswers);
        LinearLayout lLO;
        int i, j, o;
        String questionNo;
        for (i = 0; i < intHowManyQuestions; i++) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            // set the view that will contain the question, the answer and the evaluation of it (bad/good), let's call it holderview
            lLO = new LinearLayout(this);
            lLO.setBackgroundResource(R.drawable.rounded_corners);
            lLO.setOrientation(LinearLayout.VERTICAL);
            lLO.setLayoutParams(lp);
            lLO.setElevation(8.0f);
            lLO.setPadding(20, 20, 20, 20);

            // Question X
            questionNoToDisplay = new TextView(this);
            questionNo = String.valueOf(i + 1);
            questionNoToDisplay.setText(getString(R.string.question) + " " + questionNo);
            questionNoToDisplay.setGravity(Gravity.CENTER_HORIZONTAL);
            lp.setMargins(0, 20, 0, 20);
            questionNoToDisplay.setLayoutParams(lp);
            questionNoToDisplay.setTypeface(null, Typeface.BOLD);
            questionNoToDisplay.setTextColor(0xff000000);

            // add 'Question X' to the view
            lLO.addView(questionNoToDisplay);

            // The question
            questionToDisplay = new TextView(this);
            questionToDisplay.setText(questions[i]);
            questionToDisplay.setTextColor(0xff000000);
            questionToDisplay.setLayoutParams(lp);

            // add question to the view
            lLO.addView(questionToDisplay);

            // where the answers of the current question start?
            // o will contain the index for the "options" string-array, pointing to the first option of the current question
            o = 0;
            j = 0;
            while (j < i) {
                o = o + Integer.valueOf(numbers[j]);
                j++;
            }

            // get the good and bad answers
            goodAnswer = options[o + Integer.valueOf(correctAnswers[i]) - 1];
            badAnswer = options[o + intGivenAnswers[i + 1] - 1];

            // the given answer equals to the correct answer?
            if (intGivenAnswers[i + 1] == Integer.valueOf(correctAnswers[i])) {
                // yes, it does. display it with "style"
                // the answer
                answerEvaluation = new TextView(this);
                answerEvaluation.setText(getString(R.string.wascorrect) + ": " + goodAnswer + ".");
                answerEvaluation.setBackgroundColor(0xff4beb60); // originally 0xff00ff00 - "angry green"
                // add it to the view
                lLO.addView(answerEvaluation);
            } else {
                // nope, but style is still needed
                // the user's answer
                answerEvaluation = new TextView(this);
                answerEvaluation.setText(getString(R.string.wasincorrect) + ": " + badAnswer + ".");
                answerEvaluation.setBackgroundColor(0xfff2a0a0); // originally 0xffff0000 - "agressive red"
                answerEvaluation.setTextColor(0xffffffff);
                // the correct answer
                correction = new TextView(this);
                correction.setText(getString(R.string.thecorrectis) + ": " + goodAnswer + ".");
                correction.setBackgroundColor(0xff4beb60); // originally 0xff00ff00 - "angry green"
                lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 10, 0, 0);
                correction.setLayoutParams(lp);
                // add both of them to the view
                lLO.addView(answerEvaluation);
                lLO.addView(correction);
            }
            // add the holderview to the big view in the layout
            listOfAnswers.addView(lLO);
        }
    }
}