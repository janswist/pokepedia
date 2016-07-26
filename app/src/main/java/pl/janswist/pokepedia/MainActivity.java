package pl.janswist.pokepedia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.janswist.pokepedia.models.PokedexAdapter;
import pl.janswist.pokepedia.models.RecyclerClickListener;
import pl.janswist.pokepedia.pokemodels.Result;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private SingletonKeeper keeper = SingletonKeeper.getInstance();
    private Subscription pokemonCatalog;
    final List<Result> searchList = new ArrayList<>();

    public static final String POKEMON_NAME = "pokemon_name";
    public static final String POKEMON_NUMBER = "pokemon_number";

    @Bind(R.id.progressBar)ProgressBar progressBar;
    @Bind(R.id.rvPokelist)RecyclerView rvPokelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        initRecyclerViewManager();
        getFullPokeDex();
    }

    void initRecyclerViewManager(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvPokelist.setLayoutManager(linearLayoutManager);
        setRecyclerView(new ArrayList<>());
    }

    void setRecyclerView(List<Result> list){
        PokedexAdapter adapter = new PokedexAdapter(this, list);
        rvPokelist.setAdapter(adapter);
        setRecyclerOnItemClick();
    }

    void setRecyclerOnItemClick(){
        rvPokelist.addOnItemTouchListener(new RecyclerClickListener(this, rvPokelist,
                new RecyclerClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        onListItemClick(position);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                }));
    }

    void onListItemClick(int position){
        Result pokemon = searchList.get(position);
        String pokemonName = pokemon.getName().substring(0,1).toUpperCase() + pokemon.getName().substring(1);
        int pokemonNumber = Utils.getPokemonNumberFromUrl(pokemon);

        Intent i = new Intent(this, PokemonDetails.class);
        i.putExtra(POKEMON_NAME, pokemonName);
        i.putExtra(POKEMON_NUMBER, pokemonNumber);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    void getFullPokeDex(){
        progressBar.setVisibility(View.VISIBLE);

        pokemonCatalog = keeper.getPokemonCatalogRequest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(Constants.TIMEOUT, TimeUnit.SECONDS)
                .subscribe(
                        pokemon -> {
                                keeper.setPokedex(pokemon.getResults());
                                searchList.addAll(pokemon.getResults());
                                setRecyclerView(keeper.getPokedex());
                        },
                        e -> Log.i(Constants.ERROR_TAG, e.toString()),
                        () -> progressBar.setVisibility(View.GONE));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.search_pokemon));
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        searchSpecificPokemon(query);
        return true;
    }

    void searchSpecificPokemon(final String searchedPokemon){

        searchList.clear();

        Observable.from(keeper.getPokedex())
                .filter(result -> comparePokemonWithQuery(searchedPokemon, result))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(searchList::add,
                        e -> Log.e(Constants.ERROR_TAG, e.toString()),
                        () -> setRecyclerView(searchList));
    }

    boolean comparePokemonWithQuery(String pokemon, Result result){
        return result.getName().contains(pokemon.toLowerCase())
                || pokemon.equals(String.valueOf(Utils.getPokemonNumberFromUrl(result)));
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pokemonCatalog.unsubscribe();
        getFullPokeDex();
        Log.i("asd", "aaaabbbb");
    }
}