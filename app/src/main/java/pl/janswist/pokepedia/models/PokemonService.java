package pl.janswist.pokepedia.models;

import pl.janswist.pokepedia.Constants;
import pl.janswist.pokepedia.pokemodels.Form;
import pl.janswist.pokepedia.pokemodels.PokemonApi;
import pl.janswist.pokepedia.pokemodels.PokemonCatalog;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface PokemonService {

    @GET("pokemon/?limit=1000")
    Observable<PokemonCatalog> getFullPokedex();

    @GET("pokemon/{idOrName}")
    Observable<Form> getPokemonData(@Path("idOrName") String pokemon);

    @GET("pokemon/{idOrName}")
    Observable<PokemonApi> getPokemonAbility(@Path("idOrName") String pokemon);

    Retrofit retrofit = new Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constants.API_URL)
            .build();
}
