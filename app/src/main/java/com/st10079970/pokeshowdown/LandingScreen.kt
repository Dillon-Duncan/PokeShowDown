package com.st10079970.pokeshowdown

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class LandingScreen : AppCompatActivity() {
    private lateinit var tvTitle: TextView
    private val titleText = "PokeShowDown"
    private var currentIndex = 0
    private val handler = Handler(Looper.getMainLooper())
    private val pokemonList = mutableListOf<Pokemon>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_landing_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvTitle = findViewById(R.id.txtViewLanding)
        fetchRandomPokemon()
        displayTextLetterByLetter()
    }

    private fun fetchRandomPokemon() {
        CoroutineScope(Dispatchers.IO).launch {
            val randomIds = (1..1300).shuffled().take(100)
            for (id in randomIds) {
                val response = NetworkModule.apiService.getPokemon(id).awaitResponse()
                if (response.isSuccessful) {
                    response.body()?.let { pokemonResponse ->
                        val name = pokemonResponse.name
                        val type = pokemonResponse.types.first().type.name
                        val attack = pokemonResponse.stats.first { it.stat.name == "attack" }.base_stat
                        val defense = pokemonResponse.stats.first { it.stat.name == "defense" }.base_stat
                        val imageUrl = pokemonResponse.sprites.front_default
                        val pokemon = Pokemon(name, type, attack, defense, imageUrl)
                        pokemonList.add(pokemon)
                    }
                }
            }
            withContext(Dispatchers.Main) {
                // Do nothing
            }
        }
    }

    private fun displayTextLetterByLetter() {
        if (currentIndex < titleText.length) {
            tvTitle.append(titleText[currentIndex].toString())
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.custom_red))
            tvTitle.textSize = 50f
            tvTitle.setTypeface(null, android.graphics.Typeface.BOLD)
            currentIndex++
            handler.postDelayed({ displayTextLetterByLetter() }, 150)
        } else {
            handler.postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                intent.putParcelableArrayListExtra("pokemonList", ArrayList(pokemonList))
                startActivity(intent)
                finish()
            }, 800)
        }
    }
}