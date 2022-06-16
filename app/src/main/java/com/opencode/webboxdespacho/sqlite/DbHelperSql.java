package com.opencode.webboxdespacho.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelperSql extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DbPedidos.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelperSql(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAR_TABLA_VIAJES);
        db.execSQL(CREAR_TABLA_VIAJESD);
        db.execSQL(CREAR_TABLA_PEDIDOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public static final String CREAR_TABLA_PEDIDOS = " CREATE TABLE PEDIDOS( " +
            "ID_PEDIDO          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,  " +
            "REGISTRO           INTEGER NOT NULL,"+
            "CLIENTE            NVARCHAR(200) NOT NULL, " +
            "DIRECCIONENVIO     NVARCHAR(200) NOT NULL, " +
            "COMUNA             NVARCHAR(200) NOT NULL, " +
            "CONDOMINIO         NVARCHAR(200) NOT NULL, " +
            "CAJAS              INTEGER NOT NULL, " +
            "BOLSAS             INTEGER NOT NULL " +
            ");";

    public static final String CREAR_TABLA_VIAJESD = " CREATE TABLE VIAJESD( " +
            "ID_DESPACHOSD      INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,  " +
            "NROVIAJE           INTEGER NOT NULL, " +
            "PEDIDO             INTEGER NOT NULL, " +
            "CAJAS              INTEGER NOT NULL, " +
            "BOLSAS             INTEGER NOT NULL, " +
            "CAJASCARGADAS      INTEGER NOT NULL, " +
            "BOLSASCARGADAS     INTEGER NOT NULL, " +
            "CAJASENTREGADAS    INTEGER NOT NULL, " +
            "BOLSASENTREGADAS   INTEGER NOT NULL " +
            ");";

    public static final String CREAR_TABLA_VIAJES = " CREATE TABLE VIAJES( " +
            "ID_VIAJES   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,  " +
            "FECHA       VARCHAR(200) NOT NULL, " +
            "PATENTE     INTEGER NOT NULL, " +
            "CHOFER      INTEGER NOT NULL, " +
            "NROVIAJE    INTEGER NOT NULL, " +
            "ESTADO      INTEGER NOT NULL "+
            ");";
}
