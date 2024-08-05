package com.st10079970.pokeshowdown

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApiService {
    @GET("pokemon/{id}")
    fun getPokemon(@Path("id") id: Int): Call<PokemonResponse>
}