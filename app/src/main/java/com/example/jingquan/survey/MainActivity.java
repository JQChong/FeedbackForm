/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.jingquan.survey;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            QuestionFragment qf = new QuestionFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.animator.enter_right, R.animator.exit_left);
            ft.replace(R.id.main, qf);
            ft.commit();
        }
        try {
            Manager manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
            if (manager.getExistingDatabase("questions_lists8") == null) {
                Database db = manager.getDatabase("questions_lists8");
                Map<String, Object> questionList = new HashMap<>();
                InputStream is = getAssets().open("LikertScale.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                int i = 1;
                while ((line = br.readLine()) != null) {
                    questionList.put("LSQ-" + i, new Question(i, Question.QUESTION_TYPE.LSQ, line));
                    i++;
                }
                InputStream is1 = getAssets().open("FreeResponse.txt");
                BufferedReader br1 = new BufferedReader(new InputStreamReader(is1));
                String line1;
                int j = 1;
                while ((line1 = br1.readLine()) != null) {
                    questionList.put("FRQ-" + j, new Question((i+j-1), Question.QUESTION_TYPE.FRQ, line1));
                    j++;
                }
                Document doc = db.getDocument("1234567890");
                doc.putProperties(questionList);
            }
            if (manager.getExistingDatabase("survey_responses6") == null) {
                manager.getDatabase("survey_responses6");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
