package com.opencode.webboxdespacho.config;

import com.opencode.webboxdespacho.models.Fotos;
import com.opencode.webboxdespacho.models.Viajes;
import com.opencode.webboxdespacho.models.Viajesd;
import com.opencode.webboxdespacho.models.Login;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface CallInterface {

    @GET("api/login/loguser")
    Call<Login> getLogin(@Query("usr") String usr,
                         @Query("contrasena") String contrasena,
                         @Query("idsesion") int idsesion);

    @GET("api/viajes/pedidosviaje")
    Call<List<Viajes>> getViajes(@Query("rutchofer") int rutchofer);

    @PUT("api/viajes/estadoviaje")
    Call<Viajes> putViajes(@Query("numviaje") int numviaje,
                           @Query("estado") int estado,
                           @Query("rutchofer") int rutchofer);

    @POST("api/pedidos/fotosdespacho")
    Call<Fotos> postFotosDespacho(@Body RequestBody body);

    @PUT("api/pedidos/pedidoentregado")
    Call<Fotos> updatePedidoEntregado(@Body RequestBody body);
}
