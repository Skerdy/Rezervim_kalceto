package com.example.skerdi.rezervim_kalceto;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import android.widget.ViewSwitcher.ViewFactory;

import com.google.firebase.auth.FirebaseAuth;

public class Aktiviteti1 extends AppCompatActivity {
    private Button LogIn;
    private Button Register;
    private TextSwitcher tekstsw;
    private int count_tekst=0;
    private String Tekst[] = {
            "Rezervo kalceto..", "Skerdy..", "blablabla"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aktiviteti1);


        LogIn = (Button) findViewById(R.id.buton_hyr);
        Register = (Button) findViewById(R.id.register);
        tekstsw = (TextSwitcher) findViewById(R.id.textswitcher);
        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login_Method();
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register_Method();
            }
        });


        setFactory();
        tekstsw.setCurrentText(Tekst[count_tekst]);

        tekstsw.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                count_tekst++;
                if(count_tekst==Tekst.length)
                    count_tekst=0;
                tekstsw.setCurrentText(Tekst[count_tekst]);
            }
        });
        loadAnimations();
    }


    private void Login_Method() {
        Intent intent = new Intent(Aktiviteti1.this, Login.class);
        startActivity(intent);
    }

    private void Register_Method() {
        Intent intent = new Intent(Aktiviteti1.this, Register.class);
        startActivity(intent);
    }

    //pjesa e Switchtekstit


    private void loadAnimations() {
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        tekstsw.setInAnimation(in);
        tekstsw.setOutAnimation(out);
    }

    private void setFactory() {
        tekstsw.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView myText = new TextView(Aktiviteti1.this);
                myText.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                myText.setTextSize(20);
                myText.setTextColor(Color.BLUE);
                return myText;
            }
        });
    }
}
