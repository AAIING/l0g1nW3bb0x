package com.opencode.webboxdespacho.sqlite.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.opencode.webboxdespacho.models.Itemsid;
import com.opencode.webboxdespacho.models.Viajes;
import com.opencode.webboxdespacho.models.Viajesd;
import com.opencode.webboxdespacho.models.Pedidos;
import com.opencode.webboxdespacho.sqlite.DbHelperSql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViajesData {

    private SQLiteDatabase db;
    private DbHelperSql dbHelper;

    public ViajesData(Context context) {
        dbHelper = new DbHelperSql(context);
    }

    public void updateItemEscaneado(String codigo, String opt) throws Exception{
        db = dbHelper.getWritableDatabase();
        try {
            String query = "";
            if(opt.equals("1")) {
                 query = "UPDATE ITEMSID SET ESCANEADO=1 WHERE CODIGO=?";
            } else if(opt.equals("2")){
                query = "UPDATE ITEMSID SET ESCANEADOENTREGADO=1 WHERE CODIGO=?";
            }
            db.execSQL(query, new Object[] {
                    codigo
            });
            dbHelper.close();
        }catch (Exception e){
            dbHelper.close();
            throw new Exception(e.getMessage());
        }
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
            if(opt.equals("1")){
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

    public Itemsid getItem(String codigo) throws Exception{
        Itemsid itemid = new Itemsid();
        db = dbHelper.getWritableDatabase();
        try{
            String query = "SELECT * FROM ITEMSID WHERE CODIGO=?";
            String[] selectionsArgs = new String[]{codigo};
            Cursor cursor = db.rawQuery(query, selectionsArgs);
            if(cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    itemid.setTipoitem(cursor.getString(cursor.getColumnIndexOrThrow("TIPOITEM")));
                    itemid.setPedidosregistro(cursor.getInt(cursor.getColumnIndexOrThrow("PEDIDOSREGISTRO")));
                    itemid.setEscaneado(cursor.getInt(cursor.getColumnIndexOrThrow("ESCANEADO")));
                    itemid.setEscaneadoEntregado(cursor.getInt(cursor.getColumnIndexOrThrow("ESCANEADOENTREGADO")));
                    cursor.moveToNext();
                }
            }
            cursor.close();
            dbHelper.close();
        } catch (Exception e){
            dbHelper.close();
            throw new Exception(e.getMessage());
        }

        return itemid;
    }

    public Viajesd getDespachod(String npedido) throws Exception{
        //
        Viajesd despd = new Viajesd();
        Pedidos pedidos = new Pedidos();
        db = dbHelper.getWritableDatabase();
        try{
            String query = "SELECT D.*, P.* FROM VIAJESD D LEFT JOIN PEDIDOS P ON D.PEDIDO = P.REGISTRO WHERE PEDIDO=?";
            String[] selectionsArgs = new String[]{npedido};
            Cursor cursor = db.rawQuery(query, selectionsArgs);
            if(cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    despd.setCajascargadas(cursor.getInt(cursor.getColumnIndexOrThrow("CAJASCARGADAS")));
                    despd.setBolsascargadas(cursor.getInt(cursor.getColumnIndexOrThrow("BOLSASCARGADAS")));
                    despd.setCajasentregadas(cursor.getInt(cursor.getColumnIndexOrThrow("CAJASENTREGADAS")));
                    despd.setBolsasentregadas(cursor.getInt(cursor.getColumnIndexOrThrow("BOLSASENTREGADAS")));
                    //
                    pedidos.setCajas((short) cursor.getInt(cursor.getColumnIndexOrThrow("CAJAS")));
                    pedidos.setBolsas((short) cursor.getInt(cursor.getColumnIndexOrThrow("BOLSAS")));
                    despd.setPedidos(pedidos);
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
        List<Viajesd> listd = new ArrayList<Viajesd>();
        List<Itemsid> listitems = null;

        Map<Integer, String> lookmap = new HashMap<>();
        Map<Integer, String> lookmap2 = new HashMap<>();

        try{
            String query = "SELECT V.*, D.*, P.*, I.* FROM VIAJES V ";
            query += "LEFT JOIN VIAJESD D ON V.NROVIAJE = D.NROVIAJE ";
            query += "LEFT JOIN PEDIDOS P ON D.PEDIDO = P.REGISTRO ";
            query += "LEFT JOIN ITEMSID I ON D.PEDIDO = I.PEDIDOSREGISTRO; ";

            Cursor cursor = db.rawQuery(query, null);
            if(cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Viajes viajes = new Viajes();

                    if(!lookmap.containsKey(cursor.getInt(cursor.getColumnIndexOrThrow("NROVIAJE"))))
                    {
                        lookmap.put(cursor.getInt(cursor.getColumnIndexOrThrow("NROVIAJE")), "");
                        viajes.setIdViaje(cursor.getInt(cursor.getColumnIndexOrThrow("ID_DESPACHOSD")));
                        viajes.setFecha(cursor.getString(cursor.getColumnIndexOrThrow("FECHA")));
                        viajes.setPatente(cursor.getString(cursor.getColumnIndexOrThrow("PATENTE")));
                        viajes.setChofer(cursor.getString(cursor.getColumnIndexOrThrow("CHOFER")));
                        viajes.setNroviaje(cursor.getInt(cursor.getColumnIndexOrThrow("NROVIAJE")));
                        viajes.setEstado((short) cursor.getInt(cursor.getColumnIndexOrThrow("ESTADO")));
                        list.add(viajes);
                    }

                    if(!lookmap2.containsKey(cursor.getInt(cursor.getColumnIndexOrThrow("PEDIDO"))))
                    {
                        Viajesd item_viajesd = new Viajesd();
                        Pedidos pedidos = new Pedidos();
                        lookmap2.put(cursor.getInt(cursor.getColumnIndexOrThrow("PEDIDO")),"");

                        item_viajesd.setPedido(cursor.getInt(cursor.getColumnIndexOrThrow("PEDIDO")));
                        item_viajesd.setCajascargadas(cursor.getInt(cursor.getColumnIndexOrThrow("CAJASCARGADAS")));
                        item_viajesd.setBolsascargadas(cursor.getInt(cursor.getColumnIndexOrThrow("BOLSASCARGADAS")));
                        item_viajesd.setCajasentregadas(cursor.getInt(cursor.getColumnIndexOrThrow("CAJASENTREGADAS")));
                        item_viajesd.setBolsasentregadas(cursor.getInt(cursor.getColumnIndexOrThrow("BOLSASENTREGADAS")));

                        pedidos.setRegistro(cursor.getInt(cursor.getColumnIndexOrThrow("PEDIDO")));
                        pedidos.setCliente(cursor.getString(cursor.getColumnIndexOrThrow("CLIENTE")));
                        pedidos.setDireccionenvio(cursor.getString(cursor.getColumnIndexOrThrow("DIRECCIONENVIO")));
                        pedidos.setComunaenvio(cursor.getString(cursor.getColumnIndexOrThrow("COMUNA")));
                        pedidos.setCondominioenvio(cursor.getString(cursor.getColumnIndexOrThrow("CONDOMINIO")));
                        pedidos.setCajas((short) cursor.getInt(cursor.getColumnIndexOrThrow("CAJAS")));
                        pedidos.setBolsas((short) cursor.getInt(cursor.getColumnIndexOrThrow("BOLSAS")));

                        listitems = new ArrayList<Itemsid>();
                        pedidos.setItemsids(listitems);
                        item_viajesd.setPedidos(pedidos);
                        listd.add(item_viajesd);
                        viajes.setViajesd(listd);
                    }

                    Itemsid itemsid = new Itemsid();
                    itemsid.setCodigo(cursor.getString(cursor.getColumnIndexOrThrow("CODIGO")));
                    itemsid.setTipoitem(cursor.getString(cursor.getColumnIndexOrThrow("TIPOITEM")));
                    itemsid.setPedidosregistro(cursor.getInt(cursor.getColumnIndexOrThrow("PEDIDOSREGISTRO")));
                    itemsid.setEscaneado(cursor.getInt(cursor.getColumnIndexOrThrow("ESCANEADO")));

                    listitems.add(itemsid);
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
            String query3 ="DELETE FROM VIAJES";
            db.execSQL(query3,  new Object[]{});
            String query ="DELETE FROM VIAJESD";
            db.execSQL(query,  new Object[]{});
            String query2 ="DELETE FROM PEDIDOS";
            db.execSQL(query2,  new Object[]{});
            String query4 ="DELETE FROM ITEMSID";
            db.execSQL(query4,  new Object[]{});
            dbHelper.close();
        }catch(Exception e){
            dbHelper.close();
            throw new Exception(e.getMessage());
        }
    }

    public void insertPedidos(List<Viajes> viajes) throws Exception{
        try {
            db = dbHelper.getWritableDatabase();
            for(Viajes item_viaje: viajes)
            {
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
                /***/
                for(Viajesd item: item_viaje.getViajesd())
                {
                    String query = "INSERT INTO VIAJESD ( " +
                            "NROVIAJE, PEDIDO, CAJASCARGADAS, BOLSASCARGADAS, CAJASENTREGADAS, BOLSASENTREGADAS) " +
                            "VALUES (?, ?, ?, ?, ?, ?);";
                    db.execSQL(
                            query,
                            new Object[]{
                                    item.Nroviaje,
                                    item.Pedido,
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
                                    pedidos.getRegistro(),
                                    pedidos.getCliente(),
                                    pedidos.getDireccionenvio(),
                                    pedidos.getComunaenvio(),
                                    pedidos.getCondominioenvio(),
                                    pedidos.getCajas(),
                                    pedidos.getBolsas()
                            });
                    /***/
                    for(Itemsid itemsid: pedidos.getItemsids()){
                            String query4 = "INSERT INTO ITEMSID ( " +
                                    "CODIGO, TIPOITEM, PEDIDOSREGISTRO, ESCANEADO, ESCANEADOENTREGADO) " +
                                    "VALUES (?, ?, ?, ?, ?);";
                            db.execSQL(
                                    query4,
                                    new Object[]{
                                            itemsid.getCodigo(),
                                            itemsid.getTipoitem(),
                                            itemsid.getPedidosregistro(),
                                            0,
                                            0
                                    });
                    }
                }
            }
            dbHelper.close();
        }catch (Exception e){
            dbHelper.close();
            throw new Exception(e.getMessage());
        }
    }
}
