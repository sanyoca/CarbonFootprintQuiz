package com.example.android.carbonfootprintquiz;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;

import static com.example.android.carbonfootprintquiz.R.raw.questions;
import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

public class Evaluate extends AppCompatActivity {
    final int COLORBLACK = 0xff000000;
    final int COLORWHITE = 0xffffffff;
    final int GOODANSWERGREEN = 0xff4beb60;
    final int BADANSWERRED = 0xfff2a0a0;
    int intCorrect, intGivenAnswers[], intShouldBeCorrect[];
    String stringName, goodAnswer, badAnswer;
    TextView questionNoToDisplay, questionToDisplay, answerEvaluation, correction;
    XmlPullParser parser;
    XmlPullParserFactory pullParserFactory;
    int intEventType;
    QuestionParser qna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evaluate);

        Intent previousIntent = getIntent();

        // get the number of correct answers
        intCorrect = previousIntent.getIntExtra("correctAnswers", 0);
        // get the user's answers
        intGivenAnswers = previousIntent.getIntArrayExtra("userAnswers");
        // get the should be correct answers
        intShouldBeCorrect = previousIntent.getIntArrayExtra("shouldbecorrect");
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

        LinearLayout lLO;
        int i;
        String questionNo;

        for (i = 0; i < intHowManyQuestions; i++) {
            try {
                // parse the i+1th questions datas into qna
                qna = ParseThis(i + 1);
            } catch (IOException e) {
                Log.i("MainActivity", e.getMessage());
            } catch (XmlPullParserException e) {
                Log.i("MainActivity", e.getMessage());
            }

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
            questionNoToDisplay.setTextColor(COLORBLACK);

            // add 'Question X' to the view
            lLO.addView(questionNoToDisplay);

            // The question
            questionToDisplay = new TextView(this);
            questionToDisplay.setText(qna.qpQuestion);
            questionToDisplay.setTextColor(COLORBLACK);
            questionToDisplay.setLayoutParams(lp);

            // add question to the view
            lLO.addView(questionToDisplay);

            goodAnswer = qna.qpAnswers[intShouldBeCorrect[i + 1]];
            badAnswer = qna.qpAnswers[intGivenAnswers[i + 1]];

            // the given answer equals to the correct answer?
            if (intGivenAnswers[i + 1] == intShouldBeCorrect[i + 1]) {
                // yes, it does. display it with "style"
                // the answer
                answerEvaluation = new TextView(this);
                answerEvaluation.setText(getString(R.string.wascorrect) + ": " + goodAnswer + ".");
                answerEvaluation.setBackgroundColor(GOODANSWERGREEN);
                // add it to the view
                lLO.addView(answerEvaluation);
            } else {
                // nope, but style is still needed
                // the user's answer
                answerEvaluation = new TextView(this);
                answerEvaluation.setText(getString(R.string.wasincorrect) + ": " + badAnswer + ".");
                answerEvaluation.setBackgroundColor(BADANSWERRED);
                answerEvaluation.setTextColor(COLORWHITE);
                // the correct answer
                correction = new TextView(this);
                correction.setText(getString(R.string.thecorrectis) + ": " + goodAnswer + ".");
                correction.setBackgroundColor(GOODANSWERGREEN);
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

    // define a class for containing the datas of the question
    class QuestionParser {
        public String qpQuestion;
        public String[] qpAnswers;
        public int qpCorrectAnswer;
    }
}