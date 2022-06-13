package com.opencode.webboxdespacho.sqlite.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.opencode.webboxdespacho.models.Despachosd;
import com.opencode.webboxdespacho.models.Pedidos;
import com.opencode.webboxdespacho.sqlite.DbHelperSql;

import java.util.ArrayList;
import java.util.List;

public class DespachosdData {

    private SQLiteDatabase db;
    private DbHelperSql dbHelper;

    public DespachosdData(Context context) {
        dbHelper = new DbHelperSql(context);
    }

    public List<Despachosd> getDespachos() throws Exception{
        db = dbHelper.getWritableDatabase();
        List<Despachosd> list = new ArrayList<Despachosd>();
        try{
            String query = "SELECT D.*, P.* FROM DESPACHOSD D LEFT JOIN PEDIDOS P ON D.PEDIDO = P.REGISTRO";
            Cursor cursor = db.rawQuery(query, null);
            if(cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Despachosd item = new Despachosd();
                    Pedidos pedidos = new Pedidos();
                    item.setIddespachod(cursor.getInt(cursor.getColumnIndexOrThrow("ID_DESPACHOSD")));
                    item.setNroviaje(cursor.getInt(cursor.getColumnIndexOrThrow("NROVIAJE")));
                    item.setPedido(cursor.getInt(cursor.getColumnIndexOrThrow("PEDIDO")));
                        pedidos.setCliente(cursor.getString(cursor.getColumnIndexOrThrow("CLIENTE")));
                        pedidos.setDireccionenvio(cursor.getString(cursor.getColumnIndexOrThrow("DIRECCIONENVIO")));
                        pedidos.setComunaenvio(cursor.getString(cursor.getColumnIndexOrThrow("COMUNA")));
                        pedidos.setCondominioenvio(cursor.getString(cursor.getColumnIndexOrThrow("CONDOMINIO")));
                        pedidos.setCajas((short) cursor.getInt(cursor.getColumnIndexOrThrow("CAJAS")));
                        pedidos.setBolsas((short) cursor.getInt(cursor.getColumnIndexOrThrow("BOLSAS")));
                        item.setPedidos(pedidos);
                    item.setCajas(cursor.getInt(cursor.getColumnIndexOrThrow("CAJAS")));
                    item.setBolsas(cursor.getInt(cursor.getColumnIndexOrThrow("BOLSAS")));
                    item.setCajascargadas(cursor.getInt(cursor.getColumnIndexOrThrow("CAJASCARGADAS")));
                    item.setBolsascargadas(cursor.getInt(cursor.getColumnIndexOrThrow("BOLSASCARGADAS")));
                    item.setCajasentregadas(cursor.getInt(cursor.getColumnIndexOrThrow("CAJASENTREGADAS")));
                    item.setBolsasentregadas(cursor.getInt(cursor.getColumnIndexOrThrow("BOLSASENTREGADAS")));
                    list.add(item);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            dbHelper.close();
        } catch (Exception e){
            dbHelper.close();
            throw new Exception(e.getMessage());
        }
        return list;
    }

    public void borrarPedidos() throws Exception{
        db = dbHelper.getWritableDatabase();
        try{
            String query = "DELETE FROM DESPACHOSD";
            db.execSQL(query,  new Object[]{});

            String query2 = "DELETE FROM PEDIDOS";
            db.execSQL(query2,  new Object[]{});

            dbHelper.close();
        }catch(Exception e){
            dbHelper.close();
            throw new Exception(e.getMessage());
        }
    }

    public void insertPedidos(List<Despachosd> list) throws Exception{
        try {
            db = dbHelper.getWritableDatabase();
            for(Despachosd item: list) {
                String query = "INSERT INTO DESPACHOSD ( " +
                        "NROVIAJE, PEDIDO, CAJAS, BOLSAS, CAJASCARGADAS, BOLSASCARGADAS, CAJASENTREGADAS, BOLSASENTREGADAS) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
                db.execSQL(
                        query,
                        new Object[]{
                                item.Nroviaje,
                                item.Pedido,
                                item.Cajas,
                                item.Bolsas,
                                item.Cajascargadas,
                                item.Bolsascargadas,
                                item.Cajasentregadas,
                                item.Bolsasentregadas
                        });
                /***/
                Pedidos pedidos = item.getPedidos();
                String query2 = "INSERT INTO PEDIDOS ( " +
                        "REGISTRO, CLIENTE, DIRECCIONENVIO, COMUNA, CONDOMINIO, CAJAS, BOLSAS) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?);";
                db.execSQL(
                        query2,
                        new Object[]{
                                item.Pedido,
                                pedidos.getCliente(),
                                pedidos.getDireccionenvio(),
                                pedidos.getComunaenvio(),
                                pedidos.getCondominioenvio(),
                                pedidos.getCajas(),
                                pedidos.getBolsas()
                        });
            }
            dbHelper.close();
        }catch (Exception e){
            dbHelper.close();
            throw new Exception(e.getMessage());
        }
    }
}
