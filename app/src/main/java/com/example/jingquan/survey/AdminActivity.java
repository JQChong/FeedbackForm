package com.example.jingquan.survey;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch(item.getItemId()){
                    case R.id.lsq:
                        selectedFragment = LSQViewFragment.newInstance();
                        break;
                    case R.id.frq:
                        selectedFragment = FRQViewFragment.newInstance();
                        break;
                    case R.id.res:
                        selectedFragment = ResponseFragment.newInstance();
                        break;
                }
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.content, selectedFragment);
                ft.setCustomAnimations(R.animator.enter_right,R.animator.exit_left);
                ft.commit();
                return true;
            }
        });
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content, LSQViewFragment.newInstance());
        ft.setCustomAnimations(R.animator.enter_right,R.animator.exit_left);
        ft.commit();
    }

}
