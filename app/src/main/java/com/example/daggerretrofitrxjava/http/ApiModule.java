package com.example.daggerretrofitrxjava.http;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ApiModule {

    public final String BASE_URL = "https://api.twitch.tv/kraken/";

    @Provides
    public OkHttpClient provideClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        /**
         * The HEADERS log level will log the request and response lines and their respective
         * headers without the actual BODY
         */
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        return new OkHttpClient.Builder().addInterceptor(interceptor).build();

    }


    @Provides
    public Retrofit provideRetrofit(String baseURL, OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(baseURL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                /**
                 * we added this line to wire up RxJava extensions.
                 * This is needed for Retrofit2 to know what our observable is.
                 */
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Provides
    public TwitchAPI provideApiService() {
        return provideRetrofit(BASE_URL, provideClient()).create(TwitchAPI.class);
    }

}
