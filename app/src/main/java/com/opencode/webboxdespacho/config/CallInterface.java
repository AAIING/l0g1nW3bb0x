package com.opencode.webboxdespacho.config;

import com.opencode.webboxdespacho.models.Despachosd;
import com.opencode.webboxdespacho.models.Login;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CallInterface {

    @GET("api/login/loguser")
    Call<Login> getLogin(@Query("usr") String usr,
                         @Query("contrasena") String contrasena,
                         @Query("idsesion") int idsesion);

    @GET("api/despachos/pedidosviaje")
    Call<List<Despachosd>> getViajes(@Query("numviaje") int numviaje);

}
