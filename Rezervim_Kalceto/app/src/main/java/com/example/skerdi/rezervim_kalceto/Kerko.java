package com.example.skerdi.rezervim_kalceto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Kerko extends AppCompatActivity {
    private Spinner qytet;
    private Spinner kompleks;
    private Spinner fusha;
    private List <String> Qytet_emer;
    private Button vazhdo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kerko);

        qytet = (Spinner) findViewById(R.id.spinner_qytet);
        kompleks = (Spinner) findViewById(R.id.spinner_kompleks);
        fusha = (Spinner) findViewById(R.id.spinner_fusha);
        vazhdo = (Button) findViewById(R.id.button6);

         Qytet_emer = new ArrayList<String>();
        //Marrim referencen tek databaza

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference Reference = database.getReference();
        DatabaseReference qytetReference= Reference.child("Qytet");
        final DatabaseReference idQytet = qytetReference.child("id_qytet");

        idQytet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               long numri_qyteteve = dataSnapshot.getChildrenCount();

                Log.d("listener", "Children Count = "+ dataSnapshot.getChildrenCount());

               for(long i =1; i<numri_qyteteve+1; i++){
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("\"");
                    String key = Long.toString(i);
                    stringBuilder.append(key).append("\"");
                    key = stringBuilder.toString();
                    Log.d("listener", "Vlera e key : "+key);
                    DatabaseReference idqytetAktual= idQytet.child(key);

                    DatabaseReference idEmerQytet=idqytetAktual.child("emri");
                    idEmerQytet.addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {
                           String emerQytet = dataSnapshot.getValue(String.class);
                           Qytet_emer.add(emerQytet);
                       }


                       @Override
                       public void onCancelled(DatabaseError databaseError) {

                       }
                   });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        shtoDataAdapterNeSpiner(qytet, Qytet_emer);
        shtoListenerNeSpiner(qytet);

        vazhdo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Kerko.this,
                        "Zgjedhja : " +
                                "\nQyteti : "+ String.valueOf(qytet.getSelectedItem()), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void shtoDataAdapterNeSpiner(Spinner etalon, List<String> emerListe){
        ArrayAdapter <String> dataAdapter = new ArrayAdapter <String> (Kerko.this, android.R.layout.simple_spinner_item, emerListe);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etalon.setAdapter(dataAdapter);
    }
    public void shtoListenerNeSpiner (Spinner etalon){
        etalon.setOnItemSelectedListener(new Listener_Per_Spinner());
    }
}
