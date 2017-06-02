package com.example.skerdi.rezervim_kalceto;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static android.R.attr.width;
import static com.example.skerdi.rezervim_kalceto.R.attr.height;

public class Panel_Perdorues extends AppCompatActivity {

    private static final String TAG ="prove" ;
    private TextView tekst_fillim;
    private Button shiko_Profilin;
    private Button shiko_Rezervimet;
    private Button Kerko;
    private Button Dil;
    private FirebaseAuth autentikimi;
    private String userid, foto;
    private String Menyra_Login;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ImageView foto_profili;

    private Ruajtes_Informacioni ruajtes_informacioni;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panel_perdorues);

        tekst_fillim = (TextView) findViewById(R.id.textView);
        shiko_Profilin = (Button) findViewById(R.id.Profil);
        shiko_Rezervimet = (Button) findViewById(R.id.Rezervime);
        Kerko = (Button) findViewById(R.id.Kerko);
        Dil = (Button) findViewById(R.id.Dil);
        foto_profili= (ImageView) findViewById(R.id.imageView2);
        autentikimi=FirebaseAuth.getInstance();

        ruajtes_informacioni= Ruajtes_Informacioni.getInstance(this);

        //ndertojme referncat ne databaze

        FirebaseDatabase  database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        DatabaseReference PerdoruesRef=myRef.child("Perdorues");
        userid = ruajtes_informacioni.merrTeDhena("id_perdorues_login");


        //e bejme poshte referencen e user pasi marrim userid
        DatabaseReference idRef=PerdoruesRef.child(userid);
        DatabaseReference emerRef=idRef.child("emri");


        DatabaseReference fotoURL=idRef.child("foto");
        fotoURL.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String foto = dataSnapshot.getValue(String.class);
                Picasso.with(Panel_Perdorues.this)
                        .load(foto)
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
                StringBuilder mesazh = new StringBuilder();
                mesazh.append("Miresevini ").append(value).append(" ! . Zgjidhni nga menuja e meposhtme! ");
                tekst_fillim.setText(mesazh);
                Log.d(TAG, "Value is: " + value);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        shiko_Profilin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(Panel_Perdorues.this, Profili.class);
                startActivity(intent4);
            }
        });

        Kerko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Panel_Perdorues.this, Kerko.class);
                startActivity(intent);
            }
        });

        Dil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectFromFacebook();
                autentikimi.signOut();
            //    LoginManager.getInstance().logOut();
                //ruajtes_informacioni.fshiPlotesisht();
                Intent intent3= new Intent(Panel_Perdorues.this, Login.class);
                startActivity(intent3);
                finish();
            }
        });
    }


    public void disconnectFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            Log.d("dalja","dalja FAIL");
            return;
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();
                Log.d("dalja","dalja sukses FB");
            }
        }).executeAsync();
    }


    @Override
    public void onBackPressed() {
        return;
    }
}
