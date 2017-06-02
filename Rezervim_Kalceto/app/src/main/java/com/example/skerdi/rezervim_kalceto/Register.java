package com.example.skerdi.rezervim_kalceto;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Register extends AppCompatActivity {


    private static final String TAG = "rregjistrimi";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText emri;
    private EditText mbiemri;
    private EditText email;
    private EditText fjalekalimi;
    private EditText mosha;
    private Button rregjistrohu;
    private String Gjinia;

    private RadioGroup gjinia_zgjedhje;
    private RadioButton gjinia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();


        emri = (EditText) findViewById(R.id.editText2);
        mbiemri = (EditText) findViewById(R.id.editText3);
        email = (EditText) findViewById(R.id.editText6);
        fjalekalimi = (EditText) findViewById(R.id.editText7);
        mosha = (EditText) findViewById(R.id.editText8);


        gjinia_zgjedhje = (RadioGroup) findViewById(R.id.zgjedhje_gjini);
        rregjistrohu = (Button) findViewById(R.id.button3);




        rregjistrohu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(format_I_Sakte()){
                    int gjinia_zgjedhur =gjinia_zgjedhje.getCheckedRadioButtonId();
                    gjinia = (RadioButton ) findViewById(gjinia_zgjedhur);
                    Gjinia = gjinia.getText().toString();
                    Rregjistrohu(email.getText().toString(), fjalekalimi.getText().toString());
                }
            }
        });
    }




    private void Rregjistrohu(String email, String fjalekalim) {
        mAuth.createUserWithEmailAndPassword(email, fjalekalim)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Toast.makeText(Register.this, "Nuk mund te rregjistroheni!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                              Toast.makeText(Register.this, "Rregjistrimi i krye me sukses",
                                    Toast.LENGTH_SHORT).show();
                            mAuth=FirebaseAuth.getInstance();
                              FirebaseUser currentFirebaseUser = mAuth.getCurrentUser() ;
                              rregjistro_TeDhenat(currentFirebaseUser.getUid());
                              Intent intent= new Intent(Register.this, Aktiviteti1.class);
                              startActivity(intent);
                        }
                    }
                });
    }


    private boolean format_I_Sakte() {
        boolean sakte = true;
        String Email = email.getText().toString();
        if (TextUtils.isEmpty(Email)) {
            email.setError("Kerkohet");
            sakte = false;
        } else {
            email.setError(null);
        }

        String Fjalekalim = fjalekalimi.getText().toString();
        if (TextUtils.isEmpty(Fjalekalim)) {
            fjalekalimi.setError("Kerkohet.");
            sakte = false;
        } else {
            fjalekalimi.setError(null);
        }
        return sakte;

    }

    private void rregjistro_TeDhenat (String userid){
        String Emer,Mbiemer, Mosha, Email;

        Emer = emri.getText().toString();
        Mbiemer = mbiemri.getText().toString();
        Mosha = mosha.getText().toString();
        Email = email.getText().toString();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        DatabaseReference PerdoruesRef=myRef.child("Perdorues");
        DatabaseReference idRef=PerdoruesRef.child(userid);
        DatabaseReference emerRef=idRef.child("emri");
        DatabaseReference mbiemerRef=idRef.child("mbiemri");
        DatabaseReference moshaRef=idRef.child("mosha");
        DatabaseReference emailRef=idRef.child("email");
        DatabaseReference gjiniaRef=idRef.child("gjinia");

        emerRef.setValue(Emer);
        mbiemerRef.setValue(Mbiemer);
        moshaRef.setValue(Mosha);
        emailRef.setValue(Email);
        if(Gjinia.equals("Mashkull")) {
            gjiniaRef.setValue("male");
        }
        else
            gjiniaRef.setValue("female");
    }

}
