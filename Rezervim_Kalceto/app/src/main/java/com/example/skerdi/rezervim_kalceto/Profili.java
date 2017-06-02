package com.example.skerdi.rezervim_kalceto;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Profili extends AppCompatActivity {

    private TextView emer;
    private TextView mbiemer;
    private TextView email;
    private TextView gjinia;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String userid;
    private ImageView foto_profili;


    private Ruajtes_Informacioni ruajtes_informacioni;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profili);

        mAuth = FirebaseAuth.getInstance();

        emer= (TextView) findViewById(R.id.emritxt);
        mbiemer = (TextView) findViewById(R.id.mbiemritxt);
        email = (TextView) findViewById(R.id.emailtxt);
        gjinia = (TextView) findViewById(R.id.datelindjatxt);
        foto_profili = (ImageView) findViewById(R.id.imageView3);

        ruajtes_informacioni= Ruajtes_Informacioni.getInstance(this);

        userid = ruajtes_informacioni.merrTeDhena("id_perdorues_login");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        DatabaseReference PerdoruesRef=myRef.child("Perdorues");
        DatabaseReference idRef=PerdoruesRef.child(userid);
        DatabaseReference emerRef=idRef.child("emri");
        DatabaseReference mbiemerRef=idRef.child("mbiemri");
        DatabaseReference emailRef=idRef.child("email");
        DatabaseReference gjiniaRef=idRef.child("gjinia");
        DatabaseReference fotoURL=idRef.child("foto");

        fotoURL.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String fotourl = dataSnapshot.getValue(String.class);

                Picasso.with(Profili.this)
                        .load(fotourl)
                        .resize(250,300).into(foto_profili);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        emerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);

                emer.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mbiemerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value2 = dataSnapshot.getValue(String.class);
                mbiemer.setText(value2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        emailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value2 = dataSnapshot.getValue(String.class);
                email.setText(value2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        gjiniaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value2 = dataSnapshot.getValue(String.class);
                gjinia.setText(value2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
