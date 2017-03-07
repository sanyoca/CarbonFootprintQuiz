package com.example.android.carbonfootprintquiz;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Question extends AppCompatActivity implements View.OnClickListener {
    private int intQuestionNumber, intCorrectAnswer, intCorrect, intAnswer = 0, intHowManyQuestions;
    private int[] intGivenAnswers;
    private String stringName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int i, j, o, numberOfOptions;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.question);

        // get the number of questions from strings.xml
        intHowManyQuestions = Integer.valueOf(getString(R.string.howmanyquestions));
        intGivenAnswers = new int[intHowManyQuestions];

        // get the number of the question from the intent, started this one
        // and the correct answer number for the current question
        Intent previousIntent = getIntent();
        intQuestionNumber = previousIntent.getIntExtra("questionNumber", 0);
        intCorrect = previousIntent.getIntExtra("correctAnswers", 0);
        intGivenAnswers = previousIntent.getIntArrayExtra("userAnswers");
        stringName = previousIntent.getStringExtra("name");

        // display the "Question x:" text
        TextView qNo = (TextView) findViewById(R.id.textview_questionnumber);
        qNo.setText(getString(R.string.question) + " " + String.valueOf(intQuestionNumber) + ":");

        // get the question string array from strings.xml
        Resources questionsRes = getResources();
        String[] questions = questionsRes.getStringArray(R.array.questions);

        // get the number of answers for each question from strings.xml
        Resources numberOfAnswersRes = getResources();
        String[] numbers = numberOfAnswersRes.getStringArray(R.array.numberofanswers);

        // get the answers for each questions from strings.xml
        Resources optionsRes = getResources();
        String[] options = optionsRes.getStringArray(R.array.options);

        // get the correct answers for each questions from strings.xml
        Resources correctRes = getResources();
        String[] correct =correctRes.getStringArray(R.array.correctanswers);
        intCorrectAnswer = Integer.valueOf(correct[intQuestionNumber-1]);

        // display the question
        TextView q = (TextView) findViewById(R.id.textview_question);
        q.setText(questions[intQuestionNumber-1]);

        // get the radiogroup, so new radiobuttons can be inserted
        RadioGroup rGroup = (RadioGroup) findViewById(R.id.radiogroup_answers);

        // sorting out the number of answers and their place in the array
        numberOfOptions = Integer.valueOf(numbers[intQuestionNumber-1]);
        o = 0;
        for(i=0; i < intQuestionNumber-1; i++)   {
            o = o + Integer.valueOf(numbers[i]);
        }
        // displaying the possible answers
        RadioButton rOpts;
        for(j=0; j <= numberOfOptions-1; j++)   {
            // create a new radiobutton, with the answer as the text
            rOpts = new RadioButton(this);
            rOpts.setText(options[o+j]);
            rOpts.setTag(String.valueOf(j+1));
            // setup the clicklistener for the radiobutton
            rOpts.setOnClickListener(this);
            // add the radiobutton to the radiogroup
            rGroup.addView(rOpts);
        }

        // setup the next question button listener
        Button nextQuestionButton = (Button) findViewById(R.id.button_next);
        nextQuestionButton.setOnClickListener(this);
    }

    public void onClick(View v) {

        int intWhichButton = Integer.valueOf(v.getTag().toString());
        switch (intWhichButton) {
            case 100: {
                // this is for the next question button
                if (intAnswer > 0) {
                    // store the given answer for the question
                    intGivenAnswers[intQuestionNumber] = intAnswer;
                    // increase the number of question by 1
                    intQuestionNumber++;
                    // if the user gave the correct answer, increase the counter
                    if (intAnswer == intCorrectAnswer) {
                        intCorrect++;
                    }

                    if (intQuestionNumber > intHowManyQuestions) {
                        // if the end of the quiz reached, start the evaluation
                        Intent evaluateIntent = new Intent(Question.this, Evaluate.class);
                        // passing the number of correct answers and the given answers
                        evaluateIntent.putExtra("correctAnswers", intCorrect);
                        evaluateIntent.putExtra("userAnswers", intGivenAnswers);
                        evaluateIntent.putExtra("name", stringName);
                        startActivity(evaluateIntent);
                        // else go on with the questions
                    } else {
                        Intent questionsIntent = new Intent(Question.this, Question.class);
                        // and pass the datas to the intent
                        questionsIntent.putExtra("questionNumber", intQuestionNumber);
                        questionsIntent.putExtra("correctAnswers", intCorrect);
                        questionsIntent.putExtra("userAnswers", intGivenAnswers);
                        questionsIntent.putExtra("name", stringName);
                        // start the intent - itself again
                        startActivity(questionsIntent);
                    }
                } else {
                    // if the user didn't select an answer, display a warning toast
                    Toast.makeText(Question.this, getString(R.string.forgotanswer), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default: {
                intAnswer = intWhichButton;
            }
        }
    }
}
