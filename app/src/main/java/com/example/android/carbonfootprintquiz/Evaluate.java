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

import static android.R.attr.left;
import static android.R.attr.right;

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
        outOf.setText(getString(R.string.outof) + " " + String.valueOf(intHowManyQuestions) + " " +getString(R.string.answered));
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

        int i, j, o;
        for(i=0; i<intHowManyQuestions; i++)   {
            // display the question number
            questionNoToDisplay = new TextView(this);
            questionNoToDisplay.setText(getString(R.string.question) + " " + String.valueOf(i+1));
            questionNoToDisplay.setGravity(Gravity.CENTER_HORIZONTAL);
            questionNoToDisplay.setTypeface(null, Typeface.BOLD);
            questionNoToDisplay.setTextColor(0xff000000);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 20, 0, 0);
            questionNoToDisplay.setLayoutParams(lp);
            listOfAnswers.addView(questionNoToDisplay);
            //display the question
            questionToDisplay = new TextView(this);
            questionToDisplay.setText(questions[i]);
            listOfAnswers.addView(questionToDisplay);

            // where the answers of the current question start?
            // o will contain the index for the "options" string-array, pointing to the first option of the current question
            o = 0;
            j = 0;
            while(j<i)  {
                o = o + Integer.valueOf(numbers[j]);
                j++;
            }

            // get the good and bad answers
            goodAnswer = options[o+Integer.valueOf(correctAnswers[i])-1];
            badAnswer = options[o+intGivenAnswers[i+1]-1];

            // the given answer equals to the correct answer?
            if(intGivenAnswers[i+1] == Integer.valueOf(correctAnswers[i]))  {
                // yes, it does. display it with "style"
                answerEvaluation = new TextView(this);
                answerEvaluation.setText(getString(R.string.wascorrect) + ": " + goodAnswer + ".");
                answerEvaluation.setBackgroundColor(0xff00ff00);
                listOfAnswers.addView(answerEvaluation);
            }   else    {
                // nope, but style is still needed
                answerEvaluation = new TextView(this);
                answerEvaluation.setText(getString(R.string.wasincorrect) + ": " + badAnswer + ".");
                answerEvaluation.setBackgroundColor(0xffff0000);
                answerEvaluation.setTextColor(0xffffffff);
                correction = new TextView(this);
                correction.setText(getString(R.string.thecorrectis) + ": " + goodAnswer + ".");
                correction.setBackgroundColor(0xff00ff00);
                lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 10, 0, 0);
                correction.setLayoutParams(lp);
                listOfAnswers.addView(answerEvaluation);
                listOfAnswers.addView(correction);
            }

            line = new View(this);
            line.setBackgroundColor(0xff888888);
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
            lp.setMargins(0, 25, 0, 5);
            line.setLayoutParams(lp);
            listOfAnswers.addView(line);
        }
    }
}