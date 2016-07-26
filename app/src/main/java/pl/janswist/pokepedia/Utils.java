package pl.janswist.pokepedia;

import pl.janswist.pokepedia.pokemodels.Result;

public class Utils{

    public static int getPokemonNumberFromUrl(Result pokemon){
        return Integer.valueOf(pokemon.getUrl().substring(pokemon.getUrl().indexOf("n") + 2, pokemon.getUrl().length()-1));
    }
}
