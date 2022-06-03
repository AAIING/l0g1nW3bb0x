package com.opencode.webboxdespacho.config;

import com.opencode.webboxdespacho.models.Login;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CallInterface {

    @GET("api/login/loguser")
    Call<Login> getLogin(@Query("usr") String usr,
                         @Query("contrasena") String contrasena,
                         @Query("idsesion") int idsesion);

}
