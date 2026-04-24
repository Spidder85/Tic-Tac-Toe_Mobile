package app.di;

import android.content.Context;

import androidx.room.Room;

import com.google.gson.Gson;

import javax.inject.Singleton;

import app.data.local.AppDatabase;
import app.data.local.GameDao;
import app.data.local.UserDao;
import app.data.remote.ApiConfig;
import app.data.remote.AuthHeaderFactory;
import app.data.remote.TicTacToeApi;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class DataModule {
    @Provides
    @Singleton
    Gson provideGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    AppDatabase provideDatabase(Context context) {
        return Room.databaseBuilder(
                context,
                AppDatabase.class,
                "tic_tac_toe.db"
        ).build();
    }

    @Provides
    @Singleton
    UserDao provideUserDao(AppDatabase database) {
        return database.userDao();
    }

    @Provides
    @Singleton
    GameDao provideGameDao(AppDatabase database) {
        return database.gameDao();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient okHttpClient, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    TicTacToeApi provideApi(Retrofit retrofit) {
        return retrofit.create(TicTacToeApi.class);
    }

    @Provides
    @Singleton
    AuthHeaderFactory provideAuthHeaderFactory() {
        return new AuthHeaderFactory();
    }
}
