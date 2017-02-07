package com.example.daggerretrofitrxjava.root;

import com.example.daggerretrofitrxjava.MainActivity;
import com.example.daggerretrofitrxjava.http.ApiModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, ApiModule.class})
public interface ApplicationComponent {

    void inject(MainActivity target);

}
