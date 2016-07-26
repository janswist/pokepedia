package pl.janswist.pokepedia.models;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.janswist.pokepedia.R;

public class PokemonDetailsAdapter extends RecyclerView.Adapter<PokemonDetailsAdapter.ViewHolder> {

    private List<String> abilities = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.tvAbility)TextView tvAbility;
        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public PokemonDetailsAdapter(List<String> abilities) {
        this.abilities = abilities;
    }

    @Override
    public PokemonDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pokemon_ability_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvAbility.setText(abilities.get(position));
    }

    @Override
    public int getItemCount() {
        return abilities.size();
    }
}
