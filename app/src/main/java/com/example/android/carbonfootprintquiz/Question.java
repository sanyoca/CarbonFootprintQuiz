package com.example.android.carbonfootprintquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;

import static com.example.android.carbonfootprintquiz.R.raw.questions;
import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

public class Question extends AppCompatActivity implements View.OnClickListener {
    XmlPullParser parser;
    XmlPullParserFactory pullParserFactory;
    int intEventType;
    QuestionParser qna;
    private int intQuestionNumber, intCorrectAnswer, intCorrect, intAnswer = 0, intHowManyQuestions;
    private int[] intGivenAnswers, intShouldBeCorrect;
    private String stringName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int i;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.question);

        // get the number of questions from strings.xml
        intHowManyQuestions = Integer.valueOf(getString(R.string.howmanyquestions));
        intGivenAnswers = new int[intHowManyQuestions];
        intShouldBeCorrect = new int[intHowManyQuestions];

        Intent previousIntent = getIntent();
        // get the number of the question from the intent, started this one
        intQuestionNumber = previousIntent.getIntExtra("questionNumber", 0);
        // get the number of correct answers
        intCorrect = previousIntent.getIntExtra("correctAnswers", 0);
        // get the user's answer for the questions till now
        intGivenAnswers = previousIntent.getIntArrayExtra("userAnswers");
        // get the correct answers for the questions till now
        intShouldBeCorrect = previousIntent.getIntArrayExtra("shouldbecorrect");
        // get the user's name
        stringName = previousIntent.getStringExtra("name");

        // display the "Question x:" text
        TextView qNo = (TextView) findViewById(R.id.textview_questionnumber);
        qNo.setText(getString(R.string.question) + " " + String.valueOf(intQuestionNumber) + ":");

        // parse the intQuestionNumber-th question's datas
        try {
            qna = ParseThis(intQuestionNumber);
        } catch (XmlPullParserException e) {
            Log.i("MainActivity", e.getMessage());
        } catch (IOException e) {
            Log.i("MainActivity", e.getMessage());
        }

        // display the question
        TextView q = (TextView) findViewById(R.id.textview_question);
        q.setText(qna.qpQuestion);

        // get the radiogroup, so new radiobuttons can be inserted
        RadioGroup rGroup = (RadioGroup) findViewById(R.id.radiogroup_answers);

        // displaying the answers with radiobuttons
        for (i = 1; i < qna.qpAnswers.length; i++) {
            if (!qna.qpAnswers[i].equals("*")) {
                RadioButton rOpts;
                rOpts = new RadioButton(this);
                rOpts.setText(qna.qpAnswers[i]);
                rOpts.setTag(String.valueOf(i));
                // setup the clicklistener for the radiobutton
                rOpts.setOnClickListener(this);
                // add the radiobutton to the radiogroup
                rGroup.addView(rOpts);
            }
        }

        // store the correct answer number
        intCorrectAnswer = qna.qpCorrectAnswer;

        // setup the next question button listener
        Button nextQuestionButton = (Button) findViewById(R.id.button_next);
        nextQuestionButton.setOnClickListener(this);
    }

    public void onClick(View v) {

        int intWhichButton = Integer.valueOf(v.getTag().toString());
        switch (intWhichButton) {
            case 100: {
                // this is for the next question button
                // if the user selected an answer
                if (intAnswer > 0) {
                    // store the given and the correct answer for the question
                    intGivenAnswers[intQuestionNumber] = intAnswer;
                    intShouldBeCorrect[intQuestionNumber] = intCorrectAnswer;
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
                        evaluateIntent.putExtra("shouldbecorrect", intShouldBeCorrect);
                        evaluateIntent.putExtra("name", stringName);
                        startActivity(evaluateIntent);
                        // else go on with the questions
                    } else {
                        Intent questionsIntent = new Intent(Question.this, Question.class);
                        // and pass the datas to the intent
                        questionsIntent.putExtra("questionNumber", intQuestionNumber);
                        questionsIntent.putExtra("correctAnswers", intCorrect);
                        questionsIntent.putExtra("userAnswers", intGivenAnswers);
                        questionsIntent.putExtra("shouldbecorrect", intShouldBeCorrect);
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
            default: { // a radiobutton was checked, store its tag as the user's answer
                intAnswer = intWhichButton;
            }
        }
    }
    /*
    parsing the xml, containing the question datas
     */
    public QuestionParser ParseThis(int intQuestionNumber) throws IOException, XmlPullParserException {
        QuestionParser returnStuff = new QuestionParser();
        pullParserFactory = XmlPullParserFactory.newInstance();
        pullParserFactory.setNamespaceAware(false);
        parser = pullParserFactory.newPullParser();
        parser.setInput(getResources().openRawResource(questions), null);
        intEventType = parser.getEventType();

        // looking for the correct question number
        while (intEventType != END_DOCUMENT) {
            if ((intEventType == START_TAG) && parser.getName().equals("questionnumber")) {
                // found the questionnumber tag, read the number
                intEventType = parser.next();
                if (Integer.valueOf(parser.getText()) == intQuestionNumber) {
                    // is the number equals with the question number we seek? if yes, read it
                    returnStuff.qpQuestion = parseQuestion();
                    returnStuff.qpAnswers = parseAnswers();
                    returnStuff.qpCorrectAnswer = parseCorrectAnswer();
                    // then leave the while, no need to read the rest of the file
                    break;
                }
            } else {
                intEventType = parser.next();
            }
        }
        // return with the datas
        return returnStuff;
    }

    /*
    reads the actual question text from the XML
     */
    public String parseQuestion() throws IOException, XmlPullParserException {
        int intParserEvent;
        String returnQuestion;
        // we don't need the </questionnumber>, <questiontext> tags, and the \n stuff, so skip them
        intParserEvent = parser.next();
        intParserEvent = parser.next();
        intParserEvent = parser.next();
        // we need this, this contains the question
        intParserEvent = parser.next();
        returnQuestion = parser.getText();
        // and we skip the </questiontext> tag and the \n after it
        intParserEvent = parser.next();
        intParserEvent = parser.next();
        return returnQuestion;
    }

    /*
    reads the actual question's answers from the XML
     */
    public String[] parseAnswers() throws IOException, XmlPullParserException {
        String[] stringAnswers = {"*", "*", "*", "*", "*", "*"};
        int intParserEvent = parser.next(); // this will be <answers>
        int answerCounter = 1;

        while (intParserEvent != END_DOCUMENT) {
            if (intParserEvent == START_TAG && parser.getName().equals("answertext")) {
                intParserEvent = parser.next();
                stringAnswers[answerCounter] = parser.getText();
                answerCounter++;
            }

            if ((intParserEvent == END_TAG) && (parser.getName().equals("answers"))) {
                break;
            }

            intParserEvent = parser.next();
        }
        return stringAnswers;
    }

    /*
    reads the correct answer's number for the actual question
     */
    public int parseCorrectAnswer() throws IOException, XmlPullParserException {
        int intCorrectAnswer = 1;
        int intParserEvent = parser.next();

        while (intParserEvent != END_DOCUMENT) {
            if (intParserEvent == START_TAG && parser.getName().equals("correct")) {
                intParserEvent = parser.next();
                intCorrectAnswer = Integer.valueOf(parser.getText());
            }

            if ((intParserEvent == END_TAG) && (parser.getName().equals("correct"))) {
                break;
            }
            intParserEvent = parser.next();
        }
        return intCorrectAnswer;
    }

    /*
    define a class for containing the datas of the question
     */
    class QuestionParser {
        public String qpQuestion;
        public String[] qpAnswers;
        public int qpCorrectAnswer;
    }
}
