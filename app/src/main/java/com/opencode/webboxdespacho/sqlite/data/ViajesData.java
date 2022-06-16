package com.opencode.webboxdespacho.sqlite.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.opencode.webboxdespacho.models.Viajes;
import com.opencode.webboxdespacho.models.Viajesd;
import com.opencode.webboxdespacho.models.Pedidos;
import com.opencode.webboxdespacho.sqlite.DbHelperSql;

import java.util.ArrayList;
import java.util.List;

public class ViajesData {

    private SQLiteDatabase db;
    private DbHelperSql dbHelper;

    public ViajesData(Context context) {
        dbHelper = new DbHelperSql(context);
    }

    public void updateEstadoViaje(String numviaje, String estado) throws Exception{
        db = dbHelper.getWritableDatabase();
        try {
            String query ="UPDATE VIAJES SET ESTADO=? WHERE NROVIAJE=?";
            db.execSQL(query, new Object[] {
                            estado,
                            numviaje
                    });
            dbHelper.close();
        }catch (Exception e){
            dbHelper.close();
            throw new Exception(e.getMessage());
        }
    }

    public void updateCajaBolsaCount(String opt, String numpedido, String bolsa, String caja) throws Exception{
        db = dbHelper.getWritableDatabase();
        try{
            //
            String query = "";
            if(opt.equals("1")) {
                query = "UPDATE VIAJESD SET " +
                        "CAJASCARGADAS = ?, " +
                        "BOLSASCARGADAS = ? " +
                        "WHERE PEDIDO = ?;";
            }else if(opt.equals("2")){
                query = "UPDATE VIAJESD SET " +
                        "CAJASENTREGADAS = ?, " +
                        "BOLSASENTREGADAS = ? " +
                        "WHERE PEDIDO = ?;";
            }
            db.execSQL(
                    query,
                    new Object[] {
                            caja,
                            bolsa,
                            numpedido
                    });
            dbHelper.close();
        }catch (Exception e){
            dbHelper.close();
            throw new Exception(e.getMessage());
        }
    }


    public Viajesd getDespachod(String npedido) throws Exception{
        //Pedidos pedidos = new Pedidos();
        Viajesd despd = new Viajesd();
        db = dbHelper.getWritableDatabase();
        try{
            String query = "SELECT * FROM VIAJESD WHERE PEDIDO=?";
            String[] selectionsArgs = new String[]{npedido};
            Cursor cursor = db.rawQuery(query, selectionsArgs);
            if(cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    despd.setCajas((short) cursor.getInt(cursor.getColumnIndexOrThrow("CAJAS")));
                    despd.setBolsas((short) cursor.getInt(cursor.getColumnIndexOrThrow("BOLSAS")));
                    despd.setCajascargadas(cursor.getInt(cursor.getColumnIndexOrThrow("CAJASCARGADAS")));
                    despd.setBolsascargadas(cursor.getInt(cursor.getColumnIndexOrThrow("BOLSASCARGADAS")));
                    despd.setCajasentregadas(cursor.getInt(cursor.getColumnIndexOrThrow("CAJASENTREGADAS")));
                    despd.setBolsasentregadas(cursor.getInt(cursor.getColumnIndexOrThrow("BOLSASENTREGADAS")));
                    cursor.moveToNext();
                }
            }
            cursor.close();
            dbHelper.close();
        } catch (Exception e){
            dbHelper.close();
            throw new Exception(e.getMessage());
        }

        return despd;
    }

    public List<Viajes> getDespachos() throws Exception{
        db = dbHelper.getWritableDatabase();
        List<Viajes> list = new ArrayList<Viajes>();
        try{
            String query = "SELECT V.*, D.*, P.* FROM VIAJES V LEFT JOIN VIAJESD D ON V.NROVIAJE = D.NROVIAJE LEFT JOIN PEDIDOS P ON D.PEDIDO = P.REGISTRO";
            Cursor cursor = db.rawQuery(query, null);
            if(cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Viajes viajes = new Viajes();
                    Viajesd item = new Viajesd();
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

                        viajes.setIdViaje(cursor.getInt(cursor.getColumnIndexOrThrow("ID_DESPACHOSD")));
                        viajes.setFecha(cursor.getString(cursor.getColumnIndexOrThrow("FECHA")));
                        viajes.setPatente(cursor.getString(cursor.getColumnIndexOrThrow("PATENTE")));
                        viajes.setChofer(cursor.getString(cursor.getColumnIndexOrThrow("CHOFER")));
                        viajes.setNroviaje(cursor.getInt(cursor.getColumnIndexOrThrow("NROVIAJE")));
                        viajes.setEstado((short) cursor.getInt(cursor.getColumnIndexOrThrow("ESTADO")));
                        viajes.setViajesd(item);

                    list.add(viajes);

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
            String query3 = "DELETE FROM VIAJES";
            db.execSQL(query3,  new Object[]{});
            String query = "DELETE FROM VIAJESD";
            db.execSQL(query,  new Object[]{});
            String query2 = "DELETE FROM PEDIDOS";
            db.execSQL(query2,  new Object[]{});
            dbHelper.close();
        }catch(Exception e){
            dbHelper.close();
            throw new Exception(e.getMessage());
        }
    }

    public void insertPedidos(List<Viajes> list) throws Exception{
        try {
            db = dbHelper.getWritableDatabase();
            int count = 0;
            for(Viajes item_viaje: list) {
                //
                if(count == 0) {
                    String query3 = "INSERT INTO VIAJES ( " +
                            "FECHA, PATENTE, CHOFER, NROVIAJE, ESTADO)" +
                            "VALUES (?, ?, ?, ?, ?);";
                    db.execSQL(
                            query3,
                            new Object[]{
                                    item_viaje.Fecha,
                                    item_viaje.Patente,
                                    item_viaje.Chofer,
                                    item_viaje.Nroviaje,
                                    item_viaje.Estado
                            });
                    count++;
                }
                /***/
                Viajesd item = item_viaje.getViajesd();
                String query = "INSERT INTO VIAJESD ( " +
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
