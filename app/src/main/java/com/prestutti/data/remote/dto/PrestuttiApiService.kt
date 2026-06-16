package com.prestutti.data.remote.dto

import com.prestutti.data.remote.dto.ContactDto
import retrofit2.http.GET

interface PrestuttiApiService {

    // Le indicamos a Retrofit que haga una petición de tipo GET.
    // "users" es el punto final (endpoint) de la URL que traerá la lista de contactos.
    @GET("users")
    suspend fun getSuggestedContacts(): List<ContactDto>

}