package com.example.android.carbonfootprintquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import static java.lang.Boolean.FALSE;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // insert the intro text into WebView
        WebView webview = (WebView) findViewById(R.id.webview_intro);
        String intro = "<p align='justify'>" + getString(R.string.intro) + "</p>";
        webview.setVerticalScrollBarEnabled(FALSE);
        webview.loadData(intro, "text/html", null);

        // setup the start quiz button listener
        findViewById(R.id.button_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the user's name
                EditText nameEditText = (EditText) findViewById(R.id.edittext_name);
                String name = nameEditText.getText().toString();
                // if it's not empty, start the quiz, else pop up a toast
                if (!name.equals("")) {
                    Intent questionsIntent = new Intent(MainActivity.this, Question.class);
                    // pass the number of the actual question (1), the number of correct answers till now (0)
                    // and the answers given by the users array, then start the Question activity
                    int intHowManyQuestions = Integer.valueOf(getString(R.string.howmanyquestions));
                    String[] ua;
                    ua = new String[intHowManyQuestions + 1];

                    questionsIntent.putExtra("questionNumber", 1);
                    questionsIntent.putExtra("correctAnswers", 0);
                    questionsIntent.putExtra("userAnswers", ua);
                    questionsIntent.putExtra("shouldbecorrect", ua);
                    questionsIntent.putExtra("name", name);

                    startActivity(questionsIntent);
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.forgotname), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

