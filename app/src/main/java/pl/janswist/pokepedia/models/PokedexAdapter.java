package pl.janswist.pokepedia.models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.janswist.pokepedia.Constants;
import pl.janswist.pokepedia.R;
import pl.janswist.pokepedia.Utils;
import pl.janswist.pokepedia.pokemodels.Result;

public class PokedexAdapter extends RecyclerView.Adapter<PokedexAdapter.ViewHolder> {

    private Context c;
    private List<Result> pokemonList = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.tvName)TextView tvName;
        @Bind(R.id.ivPokemon)ImageView ivPokemon;
        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public PokedexAdapter(Context c, List<Result> pokemonList) {
        this.c = c;
        this.pokemonList = pokemonList;
    }

    @Override
    public PokedexAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pokemon_list_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String pokemonName = pokemonList.get(position).getName();
        pokemonName = pokemonName.substring(0,1).toUpperCase() + pokemonName.substring(1);
        holder.tvName.setText(pokemonName);

        Glide.with(c)
                .load(String.format(Constants.IMAGE_URL,
                        Utils.getPokemonNumberFromUrl(pokemonList.get(position))))
                .into(holder.ivPokemon);
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }
}
