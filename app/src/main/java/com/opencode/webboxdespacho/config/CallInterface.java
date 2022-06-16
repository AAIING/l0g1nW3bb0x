package com.opencode.webboxdespacho.config;

import com.opencode.webboxdespacho.models.Viajes;
import com.opencode.webboxdespacho.models.Viajesd;
import com.opencode.webboxdespacho.models.Login;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface CallInterface {

    @GET("api/login/loguser")
    Call<Login> getLogin(@Query("usr") String usr,
                         @Query("contrasena") String contrasena,
                         @Query("idsesion") int idsesion);

    @GET("api/viajes/pedidosviaje")
    Call<List<Viajes>> getViajes(@Query("numviaje") int numviaje);

    @PUT("api/viajes/estadoviaje")
    Call<Viajes> putViajes(@Query("numviaje") int numviaje,
                           @Query("estado") int estado);
}
