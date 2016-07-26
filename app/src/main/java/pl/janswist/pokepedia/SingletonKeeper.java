package pl.janswist.pokepedia;

import java.util.ArrayList;
import java.util.List;

import pl.janswist.pokepedia.models.PokemonService;
import pl.janswist.pokepedia.pokemodels.PokemonApi;
import pl.janswist.pokepedia.pokemodels.PokemonCatalog;
import pl.janswist.pokepedia.pokemodels.Result;
import retrofit2.Retrofit;
import rx.Observable;

public class SingletonKeeper {

    private static SingletonKeeper ourInstance = new SingletonKeeper();
    private List<Result> pokedex = new ArrayList<>();

    private Retrofit p = PokemonService.retrofit;
    private PokemonService pokemonService = p.create(PokemonService.class);
    private Observable<PokemonCatalog> pokemonCatalogRequest = getPokemonService().getFullPokedex().cache();
    private Observable<PokemonApi> pokemonAbility;
    private int pokemonNumber = 0;

    public static SingletonKeeper getInstance() {
        return ourInstance;
    }

    private SingletonKeeper() {
    }

    public List<Result> getPokedex() {
        return pokedex;
    }

    public void setPokedex(List<Result> pokedex) {
        this.pokedex = pokedex;
    }

    public PokemonService getPokemonService() {
        return pokemonService;
    }

    public Observable<PokemonCatalog> getPokemonCatalogRequest() {
        return pokemonCatalogRequest;
    }

    public Observable<PokemonApi> getPokemonAbility() {
        return pokemonAbility;
    }

    public void setPokemonAbility() {
        this.pokemonAbility = getPokemonService().getPokemonAbility(String.valueOf(pokemonNumber)).cache();
    }

    public int getPokemonNumber() {
        return pokemonNumber;
    }

    public void setPokemonNumber(int pokemonNumber) {
        this.pokemonNumber = pokemonNumber;
    }


}
