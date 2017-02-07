package com.example.daggerretrofitrxjava.http;

import com.example.daggerretrofitrxjava.http.apimodel.Twitch;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import rx.Observable;

public interface TwitchAPI {

    @GET("games/top")
    Call<Twitch> getTopGames(@Header("Client-Id") String clientId);

    /**
     * This point to the same end point as the method above. But this time it will
     * return an Observable of Twitch rather than a call object as it did in the original method.
     * @param clientId
     * @return
     */
    @GET("games/top")
    Observable <Twitch> getTopGamesObservable(@Header("Client-Id") String clientId);
}
