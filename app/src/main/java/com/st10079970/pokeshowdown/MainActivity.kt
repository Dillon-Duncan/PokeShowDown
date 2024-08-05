package com.st10079970.pokeshowdown

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var redPlayerPokemon: Pokemon
    private lateinit var bluePlayerPokemon: Pokemon
    private var redPlayerReady = false
    private var bluePlayerReady = false
    private var redPlayerScore = 0
    private var bluePlayerScore = 0
    private lateinit var pokemonList: MutableList<Pokemon>
    private var redPlayerPreviousPokemon: Pokemon? = null
    private var bluePlayerPreviousPokemon: Pokemon? = null
    private var clicks = 0

    private val typeAdvantages = mapOf(
        "fire" to listOf("grass", "ice", "bug", "steel"),
        "water" to listOf("fire", "ground", "rock"),
        "grass" to listOf("water", "ground", "rock"),
        "electric" to listOf("water", "flying"),
        "ice" to listOf("grass", "flying", "dragon"),
        "fighting" to listOf("normal", "rock", "ice", "dark", "steel"),
        "poison" to listOf("grass", "bug"),
        "ground" to listOf("electric", "poison", "rock", "steel", "fire"),
        "flying" to listOf("grass", "fighting", "bug"),
        "psychic" to listOf("fighting", "poison"),
        "bug" to listOf("grass", "psychic", "dark"),
        "rock" to listOf("flying", "bug", "fire", "ice"),
        "ghost" to listOf("ghost", "psychic"),
        "steel" to listOf("rock", "ice", "fairy"),
        "dragon" to listOf("dragon"),
        "dark" to listOf("psychic", "ghost"),
        "fairy" to listOf("dragon", "dark", "fighting")
    )

    private val typeDisadvantages = mapOf(
        "fire" to listOf("water", "rock", "fire"),
        "water" to listOf("water", "grass", "ice"),
        "grass" to listOf("fire", "grass", "poison", "flying", "bug", "steel"),
        "electric" to listOf("grass", "electric", "dragon"),
        "ice" to listOf("fire", "water", "ice", "steel"),
        "fighting" to listOf("flying", "psychic", "fairy"),
        "poison" to listOf("grass", "poison", "ground", "rock", "ghost"),
        "ground" to listOf("grass", "bug"),
        "flying" to listOf("electric", "rock", "steel"),
        "psychic" to listOf("dark", "steel"),
        "bug" to listOf("fire", "flying", "rock"),
        "rock" to listOf("ground", "steel"),
        "ghost" to listOf("dark", "ghost"),
        "steel" to listOf("fire", "water", "electric", "steel"),
        "dragon" to listOf("steel"),
        "dark" to listOf("dark", "fairy", "fighting"),
        "fairy" to listOf("fire", "poison", "steel")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pokemonList = intent.getParcelableArrayListExtra("pokemonList") ?: mutableListOf()

        assignPokemons()

        findViewById<Button>(R.id.btnRedConfirm).setOnClickListener {
            redPlayerReady = true
            findViewById<TextView>(R.id.txtViewRedScore).text = "Ready"
            disableButtonsRed()
            checkBattleReady()
        }

        findViewById<Button>(R.id.btnBlueConfirm).setOnClickListener {
            bluePlayerReady = true
            findViewById<TextView>(R.id.txtViewBlueScore).text = "Ready"
            disableButtonsBlue()
            checkBattleReady()
        }

        findViewById<Button>(R.id.btnRedNext).setOnClickListener {
            if (!redPlayerReady) {
                clicks++
                redPlayerPreviousPokemon = redPlayerPokemon
                assignRandomPokemon(isRedPlayer = true)
            }
        }

        findViewById<Button>(R.id.btnBlueNext).setOnClickListener {
            if (!bluePlayerReady) {
                clicks++
                bluePlayerPreviousPokemon = bluePlayerPokemon
                assignRandomPokemon(isRedPlayer = false)
            }
        }

        findViewById<Button>(R.id.btnRedBack).setOnClickListener {
            if (!redPlayerReady && redPlayerPreviousPokemon != null) {
                redPlayerPokemon = redPlayerPreviousPokemon!!
                updateUI()
            }
        }

        findViewById<Button>(R.id.btnBlueBack).setOnClickListener {
            if (!bluePlayerReady && bluePlayerPreviousPokemon != null) {
                bluePlayerPokemon = bluePlayerPreviousPokemon!!
                updateUI()
            }
        }
    }

    private fun assignPokemons() {
        if (pokemonList.size >= 2) {
            redPlayerPokemon = pokemonList.removeAt(0)
            bluePlayerPokemon = pokemonList.removeAt(0)
            updateUI()
        } else {
            fetchNewPokemonList()
        }
    }

    private fun assignRandomPokemon(isRedPlayer: Boolean) {
        if (pokemonList.isEmpty()) {
            fetchNewPokemonList()
        } else {
            val randomIndex = Random.nextInt(pokemonList.size)
            if (isRedPlayer) {
                redPlayerPokemon = pokemonList.removeAt(randomIndex)
            } else {
                bluePlayerPokemon = pokemonList.removeAt(randomIndex)
            }
            updateUI()
        }
    }

    private fun updateUI() {
        if (!redPlayerReady) {
            findViewById<ImageView>(R.id.imgRedPokemon).let {
                Glide.with(this).load(redPlayerPokemon.imageUrl).into(it)
            }
            findViewById<TextView>(R.id.txtViewRedAtt).text = "ATT: ${redPlayerPokemon.attack}"
        findViewById<TextView>(R.id.txtViewRedDef).text = "DEF: ${redPlayerPokemon.defense}"
        }

        if (!bluePlayerReady) {
            findViewById<ImageView>(R.id.imgBluePokemon).let {
                Glide.with(this).load(bluePlayerPokemon.imageUrl).into(it)
            }
            findViewById<TextView>(R.id.txtViewBlueAtt).text = "ATT: ${bluePlayerPokemon.attack}"
            findViewById<TextView>(R.id.txtViewBlueDef).text = "DEF: ${bluePlayerPokemon.defense}"
        }
    }

    private fun disableButtonsBlue() {
        findViewById<Button>(R.id.btnBlueNext).isEnabled = false
        findViewById<Button>(R.id.btnBlueBack).isEnabled = false
        findViewById<TextView>(R.id.txtViewBlueAtt).text = "?"
        findViewById<TextView>(R.id.txtViewBlueDef).text = "?"
    }

    private fun disableButtonsRed() {
        findViewById<Button>(R.id.btnRedNext).isEnabled = false
        findViewById<Button>(R.id.btnRedBack).isEnabled = false
        findViewById<TextView>(R.id.txtViewRedAtt).text = "?"
        findViewById<TextView>(R.id.txtViewRedDef).text = "?"
    }

    private fun enableButtonsBlue() {
        findViewById<Button>(R.id.btnBlueNext).isEnabled = true
        findViewById<Button>(R.id.btnBlueBack).isEnabled = true
    }

    private fun enableButtonsRed() {
        findViewById<Button>(R.id.btnRedNext).isEnabled = true
        findViewById<Button>(R.id.btnRedBack).isEnabled = true
    }

    private fun checkBattleReady() {
        if (redPlayerReady && bluePlayerReady) {
            determineWinner()
        }
    }

    private fun determineWinner() {
        val redPlayerScoreTextView = findViewById<TextView>(R.id.txtViewRedScore)
        val bluePlayerScoreTextView = findViewById<TextView>(R.id.txtViewBlueScore)

        val redPlayerTotal = redPlayerPokemon.attack + redPlayerPokemon.defense + getTypeAdvantage(redPlayerPokemon.type, bluePlayerPokemon.type)
        val bluePlayerTotal = bluePlayerPokemon.attack + bluePlayerPokemon.defense + getTypeAdvantage(bluePlayerPokemon.type, redPlayerPokemon.type)

        if (redPlayerTotal > bluePlayerTotal) {
            redPlayerScore++
            redPlayerScoreTextView.text = "WINNER"
            bluePlayerScoreTextView.text = "LOSER"
        } else if (bluePlayerTotal > redPlayerTotal) {
            bluePlayerScore++
            bluePlayerScoreTextView.text = "WINNER"
            redPlayerScoreTextView.text = "LOSER"
        } else {
            redPlayerScoreTextView.text = "DRAW"
            bluePlayerScoreTextView.text = "DRAW"
        }

        redPlayerReady = false
        bluePlayerReady = false

        enableButtonsBlue()
        enableButtonsRed()

        redPlayerScoreTextView.postDelayed({
            redPlayerScoreTextView.text = "$redPlayerScore"
            bluePlayerScoreTextView.text = "$bluePlayerScore"
            assignPokemons()
        }, 1500)
    }

    private fun getTypeAdvantage(attackerType: String, defenderType: String): Int {
        return when {
            typeAdvantages[attackerType]?.contains(defenderType) == true -> 50
            typeDisadvantages[attackerType]?.contains(defenderType) == true -> -50
            else -> 0
        }
    }

    private fun fetchNewPokemonList() {
        CoroutineScope(Dispatchers.IO).launch {
            val randomIds = (1..1300).shuffled().take(60)
            for (id in randomIds) {
                val response = NetworkModule.apiService.getPokemon(id).awaitResponse()
                if (response.isSuccessful) {
                    response.body()?.let { pokemonResponse ->
                        val name = pokemonResponse.name
                        val type = pokemonResponse.types.first().type.name
                        val attack =
                            pokemonResponse.stats.first { it.stat.name == "attack" }.base_stat
                        val defense =
                            pokemonResponse.stats.first { it.stat.name == "defense" }.base_stat
                        val imageUrl = pokemonResponse.sprites.front_default
                        val pokemon = Pokemon(name, type, attack, defense, imageUrl)
                        pokemonList.add(pokemon)
                    }
                }
            }
            withContext(Dispatchers.Main) {
                assignPokemons()
            }
        }
    }
}