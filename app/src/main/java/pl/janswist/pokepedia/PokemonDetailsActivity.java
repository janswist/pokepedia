package pl.janswist.pokepedia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.janswist.pokepedia.models.PokemonDetailsAdapter;
import pl.janswist.pokepedia.pokemodels.Ability;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PokemonDetailsActivity extends AppCompatActivity {

    private List<String> abilites = new ArrayList<>();
    private int pokemonNumber;
    private String pokemonName;
    private Subscription pokemonAbility;
    SingletonKeeper keeper = SingletonKeeper.getInstance();

    @Bind(R.id.ivPokemonPhoto)ImageView ivPokemonPhoto;
    @Bind(R.id.tvPokemonName)TextView tvPokemonName;
    @Bind(R.id.progressBar)ProgressBar progressBar;
    @Bind(R.id.rvAbilities)RecyclerView rvAbilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_details);

        ButterKnife.bind(this);
        Intent i = getIntent();
        pokemonName = i.getStringExtra(MainActivity.POKEMON_NAME);
        pokemonNumber = i.getIntExtra(MainActivity.POKEMON_NUMBER, 0);

        initRecyclerViewManager();
        setPokemonNameAndNumber();
        setPokemonImage();
        cacheData();
        getPokemonAbility();
    }

    void initRecyclerViewManager(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAbilities.setLayoutManager(linearLayoutManager);
        rvAbilities.setAdapter(new PokemonDetailsAdapter(new ArrayList<>()));
    }

    void setPokemonNameAndNumber(){
        setTitle(String.format(getResources().getString(R.string.pokemon_no), pokemonNumber));
        tvPokemonName.setText(pokemonName);
    }

    void setPokemonImage(){
        String url = String.format(Constants.IMAGE_URL, pokemonNumber);
        Glide.with(this).load(url).into(ivPokemonPhoto);
    }

    void cacheData(){
        if( isFirstPokemonInSession()|| hasUserSelectedAnotherPokemon()){
            keeper.setPokemonNumber(pokemonNumber);
            keeper.setPokemonAbility();
        }
    }

    boolean isFirstPokemonInSession(){
        return keeper.getPokemonNumber() == 0;
    }

    boolean hasUserSelectedAnotherPokemon(){
        return keeper.getPokemonNumber() != pokemonNumber;
    }

    void getPokemonAbility(){
        progressBar.setVisibility(View.VISIBLE);

        pokemonAbility = keeper.getPokemonAbility()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(pokemon -> Observable.from(pokemon.getAbilities()))
                .flatMap(this::getAbility)
                .flatMap(s -> Observable.just(s.replace("-", " ")))
                .subscribe(ability -> abilites.add(ability),
                        e -> Log.i(Constants.ERROR_TAG, e.toString()),
                        this::setPokemonAbilities);
    }

    Observable<String> getAbility(Ability ability){
        return Observable.just(ability.getAbility().getName());
    }

    void setPokemonAbilities(){
        progressBar.setVisibility(View.GONE);
        rvAbilities.setAdapter(new PokemonDetailsAdapter(abilites));
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pokemonAbility.unsubscribe();
        getPokemonAbility();
    }
}
