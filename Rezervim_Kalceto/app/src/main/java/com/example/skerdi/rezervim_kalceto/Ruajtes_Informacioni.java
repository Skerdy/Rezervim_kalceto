package com.example.skerdi.rezervim_kalceto;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Skerdi on 6/1/2017.
 */

public class Ruajtes_Informacioni {

    private static Ruajtes_Informacioni ruajtes_informacioni;
    private SharedPreferences sharedPreferences;

    //konstruktori i ruajtes informacioni

    public static Ruajtes_Informacioni getInstance(Context context){
        if(ruajtes_informacioni==null){
            ruajtes_informacioni=new Ruajtes_Informacioni(context);
        }
        return ruajtes_informacioni;
    }

    private Ruajtes_Informacioni(Context context){
        sharedPreferences=context.getSharedPreferences("TeDhena", Context.MODE_PRIVATE);
    }

    public void ruajTeDhena(String key, String value){
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }

    public String merrTeDhena(String key) {
        String vlera;
        if (sharedPreferences!= null) {
             vlera = sharedPreferences.getString(key, "");
            return  vlera;
        }
        return "";
    }

    public void fshiPlotesisht(){
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.clear();
        prefsEditor.apply();

    }
}
