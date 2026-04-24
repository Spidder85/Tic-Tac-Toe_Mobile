package app;

import android.app.Application;

import app.di.AppComponent;
import app.di.AppModule;
import app.di.DaggerAppComponent;

public class TicTacToeApplication extends Application {
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
