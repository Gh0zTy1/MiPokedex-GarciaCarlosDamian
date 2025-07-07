package garcia.carlosdamian.mypokedex

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class PokemonListAdapter : BaseAdapter {

    private lateinit var pokemons: MutableList<Pokemon>
    private lateinit var context: Context

    constructor(context: Context, pokemons: MutableList<Pokemon>) {
        this.pokemons = pokemons
        this.context = context
    }

    override fun getCount(): Int {
        return pokemons.size
    }

    override fun getItem(position: Int): Pokemon {
        return this.pokemons.get(position)
    }

    override fun getItemId(position: Int): Long {
        return this.pokemons.get(position).hashCode().toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: View.inflate(context, R.layout.item_pokemon_card, null)

        val imageView = view.findViewById<ImageView>(R.id.ivPokemonImage)
        val textId = view.findViewById<TextView>(R.id.tvPokemonId)
        val textName = view.findViewById<TextView>(R.id.tvPokemonName)

        val pokemon = getItem(position)

        textId.text = "#${pokemon.id}"
        textName.text = pokemon.name

        Glide.with(context)
            .load(pokemon.imageUrl)
            .placeholder(R.drawable.android_icon)
            .into(imageView)

        return view
    }
}