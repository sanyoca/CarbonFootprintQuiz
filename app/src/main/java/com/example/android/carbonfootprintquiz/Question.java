package com.example.android.carbonfootprintquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    String stringAnswer = "*";
    String[] stringShouldBeCorrect;
    String stringCorrectAnswer;
    private int intQuestionNumber, intCorrect, intHowManyQuestions;
    private String[] stringGivenAnswers;
    private String stringName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int i;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.question);

        // get the number of questions from strings.xml
        intHowManyQuestions = Integer.valueOf(getString(R.string.howmanyquestions));
        stringGivenAnswers = new String[intHowManyQuestions];
        stringShouldBeCorrect = new String[intHowManyQuestions];

        Intent previousIntent = getIntent();
        // get the number of the question from the intent, started this one
        intQuestionNumber = previousIntent.getIntExtra("questionNumber", 0);
        // get the number of correct answers
        intCorrect = previousIntent.getIntExtra("correctAnswers", 0);
        // get the user's answer for the questions till now
        stringGivenAnswers = previousIntent.getStringArrayExtra("userAnswers");
        // get the correct answers for the questions till now
        stringShouldBeCorrect = previousIntent.getStringArrayExtra("shouldbecorrect");
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

        // if it is a radiobutton question, use this
        if (qna.qpQuestionType.equals("radio")) {
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
        }

        // if it is a question, that requires a text input from the user, use this
        if (qna.qpQuestionType.equals("edit")) {
            EditText editAnswer = (EditText) findViewById(R.id.edit_answer);
            editAnswer.setVisibility(View.VISIBLE);
        }

        // if there is multiple answers can be selected, use this
        if (qna.qpQuestionType.equals("check")) {
            // get the view, that contains the checkboxes
            LinearLayout checkboxHolder = (LinearLayout) findViewById(R.id.checkbox_answers);

            // displaying the answers with checkboxes
            for (i = 1; i < qna.qpAnswers.length; i++) {
                if (!qna.qpAnswers[i].equals("*")) {
                    CheckBox cOpts;
                    cOpts = new CheckBox(this);
                    cOpts.setText(qna.qpAnswers[i]);
                    cOpts.setTag(String.valueOf(i));
                    // setup the clicklistener for the radiobutton
                    cOpts.setOnClickListener(this);
                    // add the radiobutton to the radiogroup
                    checkboxHolder.addView(cOpts);
                }
            }
        }

        // store the correct answer number
        stringCorrectAnswer = qna.qpCorrectAnswer;

        // setup the next question and hint button listener
        Button nextQuestionButton = (Button) findViewById(R.id.button_next);
        nextQuestionButton.setOnClickListener(this);
        Button hintButton = (Button) findViewById(R.id.button_hint);
        hintButton.setOnClickListener(this);
        if(intQuestionNumber == intHowManyQuestions)
            nextQuestionButton.setText(getString(R.string.evaluateme));
    }

    /**
     * onClicklistener for the next question button, the hint button and the radiobuttons
     *
     * @param v the view that was clicked on
     */
    public void onClick(View v) {
        String stringWhichButton = v.getTag().toString();
        EditText editAnswer = (EditText) findViewById(R.id.edit_answer);
        switch (stringWhichButton) {
            case "100": { // this is for the next question button
                // if this question was a radiobutton type question
                if (qna.qpQuestionType.equals("radio")) {
                    // if the user selected an answer
                    if (!stringAnswer.equals("") && !stringAnswer.equals("*")) {
                        // store the given and the correct answer for the question
                        stringGivenAnswers[intQuestionNumber] = stringAnswer;
                        stringShouldBeCorrect[intQuestionNumber] = stringCorrectAnswer;
                        // increase the number of question by 1
                        intQuestionNumber++;
                        // if the user gave the correct answer, increase the counter
                        if (stringAnswer.equals(stringCorrectAnswer)) {
                            intCorrect++;
                        }

                        if (intQuestionNumber > intHowManyQuestions) {
                            // if the end of the quiz reached, start the evaluation
                            Intent evaluateIntent = new Intent(Question.this, Evaluate.class);
                            // passing the number of correct answers and the given answers
                            evaluateIntent.putExtra("correctAnswers", intCorrect);
                            evaluateIntent.putExtra("userAnswers", stringGivenAnswers);
                            evaluateIntent.putExtra("shouldbecorrect", stringShouldBeCorrect);
                            evaluateIntent.putExtra("name", stringName);
                            startActivity(evaluateIntent);
                            // else go on with the questions
                        } else {
                            Intent questionsIntent = new Intent(Question.this, Question.class);
                            // and pass the datas to the intent
                            questionsIntent.putExtra("questionNumber", intQuestionNumber);
                            questionsIntent.putExtra("correctAnswers", intCorrect);
                            questionsIntent.putExtra("userAnswers", stringGivenAnswers);
                            questionsIntent.putExtra("shouldbecorrect", stringShouldBeCorrect);
                            questionsIntent.putExtra("name", stringName);
                            // start the intent - itself again
                            startActivity(questionsIntent);
                        }
                    } else {
                        // if the user didn't select an answer, display a warning toast
                        Toast.makeText(Question.this, getString(R.string.forgotanswer), Toast.LENGTH_SHORT).show();
                    }
                }

                // if the answer has to be typed
                if (qna.qpQuestionType.equals("edit")) {
                    stringAnswer = editAnswer.getText().toString();
                    if (!stringAnswer.equals("")) {
                        // store the given and the correct answer for the question
                        stringGivenAnswers[intQuestionNumber] = stringAnswer;
                        stringShouldBeCorrect[intQuestionNumber] = stringCorrectAnswer;
                        // increase the number of question by 1
                        intQuestionNumber++;
                        // if the user gave the correct answer, increase the counter
                        if (stringAnswer.equals(stringCorrectAnswer)) {
                            intCorrect++;
                        }

                        if (intQuestionNumber > intHowManyQuestions) {
                            // if the end of the quiz reached, start the evaluation
                            Intent evaluateIntent = new Intent(Question.this, Evaluate.class);
                            // passing the number of correct answers and the given answers
                            evaluateIntent.putExtra("correctAnswers", intCorrect);
                            evaluateIntent.putExtra("userAnswers", stringGivenAnswers);
                            evaluateIntent.putExtra("shouldbecorrect", stringShouldBeCorrect);
                            evaluateIntent.putExtra("name", stringName);
                            startActivity(evaluateIntent);
                            // else go on with the questions
                        } else {
                            Intent questionsIntent = new Intent(Question.this, Question.class);
                            // and pass the datas to the intent
                            questionsIntent.putExtra("questionNumber", intQuestionNumber);
                            questionsIntent.putExtra("correctAnswers", intCorrect);
                            questionsIntent.putExtra("userAnswers", stringGivenAnswers);
                            questionsIntent.putExtra("shouldbecorrect", stringShouldBeCorrect);
                            questionsIntent.putExtra("name", stringName);
                            // start the intent - itself again
                            startActivity(questionsIntent);
                        }
                    } else {
                        // if the user didn't answer anything
                        Toast.makeText(Question.this, getString(R.string.forgotanswer), Toast.LENGTH_SHORT).show();
                    }
                }

                // if there are more acceptable options for a question -> checkboxes
                if (qna.qpQuestionType.equals("check")) {
                    LinearLayout checkboxHolder = (LinearLayout) findViewById(R.id.checkbox_answers);
                    CheckBox checkAnswer;
                    String stringAnswer = "";

                    for (int i = 0; i < checkboxHolder.getChildCount(); i++) {
                        checkAnswer = (CheckBox) checkboxHolder.getChildAt(i);
                        if (checkAnswer.isChecked()) {
                            stringAnswer = stringAnswer + "1";
                        } else {
                            stringAnswer = stringAnswer + "0";
                        }
                    }

                    if (stringAnswer.contains("1")) {
                        // store the given and the correct answer for the question
                        stringGivenAnswers[intQuestionNumber] = stringAnswer;
                        stringShouldBeCorrect[intQuestionNumber] = stringCorrectAnswer;
                        // increase the number of question by 1
                        intQuestionNumber++;

                        // if the user gave the correct answer, increase the counter
                        if (stringAnswer.equals(qna.qpCorrectAnswer)) {
                            intCorrect++;
                        }

                        if (intQuestionNumber > intHowManyQuestions) {
                            // if the end of the quiz reached, start the evaluation
                            Intent evaluateIntent = new Intent(Question.this, Evaluate.class);
                            // passing the number of correct answers and the given answers
                            evaluateIntent.putExtra("correctAnswers", intCorrect);
                            evaluateIntent.putExtra("userAnswers", stringGivenAnswers);
                            evaluateIntent.putExtra("shouldbecorrect", stringShouldBeCorrect);
                            evaluateIntent.putExtra("name", stringName);
                            startActivity(evaluateIntent);
                            // else go on with the questions
                        } else {
                            Intent questionsIntent = new Intent(Question.this, Question.class);
                            // and pass the datas to the intent
                            questionsIntent.putExtra("questionNumber", intQuestionNumber);
                            questionsIntent.putExtra("correctAnswers", intCorrect);
                            questionsIntent.putExtra("userAnswers", stringGivenAnswers);
                            questionsIntent.putExtra("shouldbecorrect", stringShouldBeCorrect);
                            questionsIntent.putExtra("name", stringName);
                            // start the intent - itself again
                            startActivity(questionsIntent);
                        }
                    } else {
                        // if the user didn't select any answer, display a warning toast
                        Toast.makeText(Question.this, getString(R.string.forgotanswer), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            // need a hint?  This is for the hint button
            case "200": {
                Toast.makeText(Question.this, qna.qpHint, Toast.LENGTH_SHORT).show();
            }
            // a radiobutton was checked, store its tag as the user's answer
            default: {
                stringAnswer = stringWhichButton;
            }
        }
    }

    /**
     * parsing the xml, containing the question datas
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
                    returnStuff.qpQuestionType = parseQuestionType();
                    returnStuff.qpAnswers = parseAnswers();
                    returnStuff.qpHint = parseHint();
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

    /**
     * reads the actual question text from the XML
     */
    public String parseQuestion() throws IOException, XmlPullParserException {

        String returnQuestion;
        // we don't need the </questionnumber>, <questiontext> tags, and the \n stuff, so skip them
        parser.next();
        parser.next();
        parser.next();
        // we need this, this contains the question
        parser.next();
        returnQuestion = parser.getText();
        // and we skip the </questiontext> tag and the \n after it
        parser.next();
        parser.next();
        return returnQuestion;
    }

    /**
     * reads the question type
     */
    public String parseQuestionType() throws IOException, XmlPullParserException {

        String stringQuestionType;
        // we don't need the opening tag
        parser.next();
        // we just need this: the question type: radio, check, edit
        parser.next();
        stringQuestionType = parser.getText();
        // skip the close tag and the \n
        parser.next();
        parser.next();
        return stringQuestionType;
    }

    /**
     * reads the actual question's answers from the XML
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

    /**
     * Get a little hint for the user
     */
    public String parseHint() throws IOException, XmlPullParserException {
        parser.next(); // this will be <hint>
        parser.next();
        parser.next();
        String stringHint = parser.getText();
        parser.next();
        parser.next();
        return stringHint;
    }

    /**
     * reads the correct answer's number for the actual question
     */
    public String parseCorrectAnswer() throws IOException, XmlPullParserException {
        String stringCorrectAnswer = "1";
        int intParserEvent = parser.next();

        while (intParserEvent != END_DOCUMENT) {
            if (intParserEvent == START_TAG && parser.getName().equals("correct")) {
                intParserEvent = parser.next();
                stringCorrectAnswer = parser.getText();
            }

            if ((intParserEvent == END_TAG) && (parser.getName().equals("correct"))) {
                break;
            }
            intParserEvent = parser.next();
        }
        return stringCorrectAnswer;
    }

    /**
     * define a class for containing the datas of the question
     */
    private class QuestionParser {
        private String qpHint;
        private String qpQuestion;
        private String qpQuestionType;
        private String[] qpAnswers;
        private String qpCorrectAnswer;
    }
}
