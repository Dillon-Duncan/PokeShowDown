package com.st10079970.pokeshowdown

data class PokemonResponse(
    val name: String,
    val sprites: Sprites,
    val stats: List<Stat>,
    val types: List<Type>
)

data class Sprites(val front_default: String)

data class Stat(val base_stat: Int, val stat: StatDetail)

data class StatDetail(val name: String)

data class Type(val type: TypeDetail)

data class TypeDetail(val name: String)