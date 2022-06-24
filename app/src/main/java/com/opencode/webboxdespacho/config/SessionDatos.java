package com.opencode.webboxdespacho.config;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionDatos {

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;
    Context mContext;

    public SessionDatos(Context mContext) {
        this.mContext = mContext;
        preferences=mContext.getSharedPreferences(SessionKeys.data_app.name(), Context.MODE_PRIVATE);
        editor=preferences.edit();
    }

    public HashMap<SessionKeys, String> getRecord(){
        HashMap<SessionKeys, String> map = new HashMap<>();
        map.put(SessionKeys.idViaje, preferences.getString(SessionKeys.idViaje.name(), "0"));
        map.put(SessionKeys.estadoViaje, preferences.getString(SessionKeys.estadoViaje.name(), "0"));
        return  map;
    }

    public void setIdViaje(String id, String estado){
        editor.putString(SessionKeys.idViaje.name(), id);
        editor.putString(SessionKeys.estadoViaje.name(), estado);
        editor.commit();
    }

    public boolean CheckSession(){
        boolean isLog = false;
        if (!preferences.getBoolean(SessionKeys.isLoggedIn.name(),false)){
            //mContext.startActivity(new Intent(mContext, MenuActivity.class));
            isLog = false;
        }else {
            isLog = true;
            //mContext.startActivity(new Intent(mContext, MainActivity.class));
        }
        return isLog;
    }

    public void Logout(){
        editor.clear();
        editor.commit();
        //mContext.startActivity(new Intent(mContext, MainActivity.class));
    }

    public void setIsLoggedIn(){
        editor.putBoolean(SessionKeys.isLoggedIn.name(), true);
        editor.commit();
    }
}
