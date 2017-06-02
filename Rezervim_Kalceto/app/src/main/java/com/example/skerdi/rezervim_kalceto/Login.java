package com.example.skerdi.rezervim_kalceto;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.INTERNET;

public class Login extends AppCompatActivity {
    private static final String TAG ="Prove_login" ;
    private EditText Email;
    private EditText Fjalekalim;
    private Button Hyr;
    private FirebaseAuth autentikimi;
    private FirebaseAuth fbautentikimi;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private Bundle bFacebookData;
    private String menyra_E_Login="kot";
    private Ruajtes_Informacioni ruajtes_informacioni;
    private String id_perdorues;
    private Boolean autentikim_korrekt_facebook=false;
    private FirebaseUser perdorues;
    private FirebaseAuth.AuthStateListener perdoruesListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        //Lidhim elementet grafike
        Email= (EditText) findViewById(R.id.email);
        Fjalekalim = (EditText) findViewById(R.id.password);
        Hyr = (Button) findViewById(R.id.button2);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");

        //inicializojme autentikimin
        autentikimi=FirebaseAuth.getInstance();
        callbackManager=CallbackManager.Factory.create();
        perdoruesListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d("listener", "gjendja ndryshoi");
                FirebaseUser perdorues = firebaseAuth.getCurrentUser();
                if(perdorues!=null){
                    id_perdorues=perdorues.getUid();
                    Log.d("listener", "user exists:" + perdorues.getUid());

                    if(bFacebookData!=null){
                            Log.d("listener", "current id:" + perdorues.getUid());
                            Log.d("listener", "Emri" + bFacebookData.getString("first_name"));
                            //rregjistrojme te dhenat ne databaze manualisht
                            rregjistro_TeDhenat(perdorues.getUid(), bFacebookData);
                            //e ruajme ne shared preferences kodin
                            ruajtes_informacioni.ruajTeDhena("id_perdorues_login", perdorues.getUid());
                        }
                        else {
                            Log.d("userid", id_perdorues+"mehhhhh");
                          //  rregjistro_TeDhenat(id_perdorues, bFacebookData);
                            //e ruajme ne shared preferences kodin
                            ruajtes_informacioni.ruajTeDhena("id_perdorues_login", id_perdorues);
                        }
                        Intent intent = new Intent(Login.this, Panel_Perdorues.class);
                        startActivity(intent);
                    }
                else {
                    callbackManager=CallbackManager.Factory.create();
                    Log.d("listener", "user bosh");
                }
            }
        };
        //fbautentikimi = FirebaseAuth.getInstance();







        //krijojme instancen e klases ruajtes informacioni
        ruajtes_informacioni=Ruajtes_Informacioni.getInstance(this);

        //User zgjedh te logohet me anen e email dhe password (jepet dhe userid perkates)
        Hyr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!format_I_Sakte()) {
                }
                else {
                    Hyr(Email.getText().toString(), Fjalekalim.getText().toString());
                }
            }
        });

        //User zgjedh te logohet me facebook ;( Jepet dhe userid perkates)
       loginButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               hyrFacebook();

           }
       });


        //kodi i gjenerimit te HashKey (nderlidhja Facebook app- Android APP) i pavlere ne thelb :P
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.skerdi.rezervim_kalceto",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }



    private void Hyr (String email, String fjalekalim){
            autentikimi.signInWithEmailAndPassword(email, fjalekalim).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(Login.this, "Login Sukses... ", Toast.LENGTH_SHORT).show();
                        perdorues = autentikimi.getCurrentUser();
                        id_perdorues = perdorues.getUid();
                        ruajtes_informacioni.ruajTeDhena("id_perdorues_login", id_perdorues);
                        Intent intent = new Intent(Login.this, Panel_Perdorues.class);
                        startActivity(intent);
                    }
                    else {
                        // kjo do te thote qe login deshtoi prandaj shfaqim nje mesazh tek perdoruesi
                        Toast.makeText(Login.this, "Procesi i Login Deshtoi! ", Toast.LENGTH_SHORT).show();
                    }
                }
            });


    }

    private boolean format_I_Sakte() {
        boolean sakte = true;
        String email = Email.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Email.setError("Required.");
            sakte = false;
        } else {
            Email.setError(null);
        }

        String fjalekalim = Fjalekalim.getText().toString();
        if (TextUtils.isEmpty(fjalekalim)) {
            Fjalekalim.setError("Required.");
            sakte = false;
        } else {
            Fjalekalim.setError(null);
        }
        return sakte;

    }


    public void hyrFacebook(){
        Log.d("listener", "fillon metoda HyrFacebook");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("listener", "fillon userAuthTOKEN");
                handleFacebookAccessToken(loginResult.getAccessToken());
                Log.d("listener", "mbaroi user auth, fillon graph req");
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),new GraphRequest.GraphJSONObjectCallback(){

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("listener", "mbaron GraphReq");
                        bFacebookData = getFacebookData(object);
                    }
                });
                Log.d("listener", "Jepen parametrat e bundle");
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
                request.setParameters(parameters);
                Log.d("listener", "fillonAsyncTask");
                request.executeAsync();
                Log.d("listener", "mbaronAsyncTask");
                            }

            @Override
            public void onCancel() {
                Log.d("listener", "GraphReq Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("listener", "GraphReq Error");
                Toast.makeText(Login.this, "Procesi i marrjes se te dhenave deshtoi! ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bundle getFacebookData(JSONObject object) {
        Bundle bundle = new Bundle();
        try{
            String id = object.getString("id");
            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=250&height=300");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));
        }
        catch (JSONException e){
            Log.d(TAG,"Error parsing JSON");
        }
        return bundle;
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        autentikimi.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            id_perdorues=autentikimi.getCurrentUser().getUid();
                           Log.d("listener", "login fb sakte dhe idperdorues ka vlere: "+id_perdorues);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            autentikim_korrekt_facebook=false;
                        }
                    }
                });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.d("listener", "Callback aktivizohet");
    }

    private void rregjistro_TeDhenat (String userid, Bundle teDhenaFB){
        String Emer, Mbiemer, Mosha, Location, Email, Gjinia, FotoURL;

        Emer = teDhenaFB.getString("first_name");
        Mbiemer = teDhenaFB.getString("last_name");
        Mosha = teDhenaFB.getString("birthday");
        Location = teDhenaFB.getString("location");
        Email = teDhenaFB.getString("email");
        Gjinia = teDhenaFB.getString("gender");
        FotoURL = teDhenaFB.getString("profile_pic");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        DatabaseReference PerdoruesRef=myRef.child("Perdorues");
        DatabaseReference idRef=PerdoruesRef.child(userid);
        DatabaseReference emerRef=idRef.child("emri");
        DatabaseReference mbiemerRef=idRef.child("mbiemri");
        DatabaseReference moshaRef=idRef.child("ditelindja");
        DatabaseReference locationRef=idRef.child("location");
        DatabaseReference emailRef=idRef.child("email");
        DatabaseReference gjiniaRef=idRef.child("gjinia");
        DatabaseReference fotoUrlRef=idRef.child("foto");

        emerRef.setValue(Emer);
        mbiemerRef.setValue(Mbiemer);
        moshaRef.setValue(Mosha);
        locationRef.setValue(Location);
        emailRef.setValue(Email);
        gjiniaRef.setValue(Gjinia);
        fotoUrlRef.setValue(FotoURL);
    }


    @Override
    protected void onStart() {
        super.onStart();
        autentikimi.addAuthStateListener(perdoruesListener);
        /*
        perdorues=autentikimi.getCurrentUser();
        if((perdorues!=null)){
            ruajtes_informacioni.ruajTeDhena("id_perdorues_login", perdorues.getUid());
            Intent intent =new Intent(Login.this, Panel_Perdorues.class);
            startActivity(intent);
        }
        */
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (perdoruesListener != null) {
            autentikimi.removeAuthStateListener(perdoruesListener);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(Login.this, Aktiviteti1.class);
        startActivity(intent);
    }
}


