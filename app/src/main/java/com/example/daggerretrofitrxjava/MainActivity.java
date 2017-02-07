package com.example.daggerretrofitrxjava;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.daggerretrofitrxjava.http.TwitchAPI;
import com.example.daggerretrofitrxjava.http.apimodel.Top;
import com.example.daggerretrofitrxjava.http.apimodel.Twitch;
import com.example.daggerretrofitrxjava.root.App;

import javax.inject.Inject;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Inject
    TwitchAPI twitchAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((App)getApplication()).getComponent().inject(this);

        /**
         * Get top games from remote Twitch API
         */
        Call<Twitch> call = twitchAPI.getTopGames(getResources().getString(R.string.client_id));
        call.enqueue(new Callback<Twitch>() {
            @Override
            public void onResponse(Call<Twitch> call, Response<Twitch> response) {
                List<Top> gameList = response.body().getTop();

                for (Top top : gameList){
                    System.out.println(top.getGame().getName());
                }
            }

            @Override
            public void onFailure(Call<Twitch> call, Throwable t) {
                     t.printStackTrace();
            }
        });


        /**
         * Get top games names from remote Twitch API using Rx Java
         */
        /**
         * we are requesting from the TwitchAPI which is returning an observable of Twitch objects
         */
        twitchAPI.getTopGamesObservable(getResources().getString(R.string.client_id))
                /**
                 * The flatMap operator maps the Twitch object. It extracts the Top object by invoking
                 * the getTop method of each emitted Twitch object.
                 */
                .flatMap(new Func1<Twitch, Observable<Top>>() {
            @Override
            public Observable<Top> call(Twitch twitch) {
                /**
                 * Observable.from returns an Observable that emits a sequence of whatever the
                 * argument is. Now we have an Obsrvable of Top
                 */
                return Observable.from(twitch.getTop());
            }
            /**
            * We then apply another flatMap on this Observable of Top objects. And we want to return
             * an observable of strings.
            */
        }).flatMap(new Func1<Top, Observable<String>>() {
            @Override
            public Observable<String> call(Top top) {
                /**
                 * Observable.just returns observable that emits one list
                 *
                 * We just extracting the name of the game and then emitting an Observable that emits
                 * a sequence of names of games as string objects. At this point we have an
                 * Observable of strings.
                 */
                return Observable.just(top.getGame().getName());
            }
            /**
             * With RxJava We can subscribe and observe on different threads
             * Because we are doing network activity, we need to make the remote API call happen
             * on the background thread (Schedulers.io).
             *
             * We also want to manipulate, or display the returned data on the UI. So we are
             * observing the Observable by calling ObserveOn method with the AndroidSchedulers main
             * thread.
             *
             * Since we have an Observable of strings which emits the names of the top games. We need
             * to subscribe to it
             */
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                // The list has finished loading
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            /**
             * onNext will be called whenever there is a new string that gets emitted from the
             * Observable
             * @param s
             */
            @Override
            public void onNext(String s) {
                Log.d(LOG_TAG, "From rx java: " + s);
            }
        });


       twitchAPI.getTopGamesObservable(getResources().getString(R.string.client_id)).flatMap(new Func1<Twitch, Observable<Top>>() {
            @Override
            public Observable<Top> call(Twitch twitch) {

                return Observable.from(twitch.getTop());
            }
        }).flatMap(new Func1<Top, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(Top top) {

                return Observable.just(top.getGame().getPopularity());

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
           @Override
           public void onCompleted() {

           }

           @Override
           public void onError(Throwable e) {
               e.printStackTrace();

           }

           @Override
           public void onNext(Integer integer) {
               System.out.println("From rx java: Popularity is " + integer.toString());

           }
       });

        twitchAPI.getTopGamesObservable(getResources().getString(R.string.client_id)).flatMap(new Func1<Twitch, Observable<Top>>() {
            @Override
            public Observable<Top> call(Twitch twitch) {

                return Observable.from(twitch.getTop());
            }
        }).flatMap(new Func1<Top, Observable<String>>() {
            @Override
            public Observable<String> call(Top top) {

                return Observable.just(top.getGame().getName());

            }
        }).filter(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                return s.startsWith("H");
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                System.out.println("From rx java with filter: " + s);

            }
        });

    }
}
