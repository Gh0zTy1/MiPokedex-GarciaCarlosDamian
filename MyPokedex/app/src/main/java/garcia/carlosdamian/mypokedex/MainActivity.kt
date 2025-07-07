package garcia.carlosdamian.mypokedex

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.GridView
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class MainActivity : AppCompatActivity() {

    private lateinit var pokemonList: MutableList<Pokemon>
    private lateinit var adapter: PokemonListAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Initialize the list and adapter
        pokemonList = mutableListOf()
        adapter = PokemonListAdapter(this, pokemonList)

        // 2. Vincular el GridView y configurar su adaptador
        val gvPokemons = findViewById<GridView>(R.id.pokemonsGridView)
        gvPokemons.adapter = adapter

        // 3. Obtener los datos de Firebase en tiempo real
        fetchPokemonsFromFirestore()

        // 4. Configurar el ImageButton para ir a la pantalla de registro
        val btnAddPokemon = findViewById<ImageButton>(R.id.addPokemonBtn)
        btnAddPokemon.setOnClickListener {
            val intent = Intent(this, PokemonRegistryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchPokemonsFromFirestore() {
        db.collection("pokemons")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("MainActivity", "Listen failed.", e)
                    return@addSnapshotListener
                }

                // Limpiar la lista actual para evitar duplicados
                pokemonList.clear()

                // Recorrer los documentos y convertirlos a objetos Pokemon
                for (doc in snapshots!!) {
                    val pokemon = doc.toObject<Pokemon>()
                    pokemonList.add(pokemon)
                }

                // Notificar al adaptador que los datos han cambiado
                adapter.notifyDataSetChanged()
            }
    }
}