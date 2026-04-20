package app.data.remote;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TicTacToeApi {
    @POST("auth/signup")
    Call<Boolean> signUp(@Body SignUpRequestDto request);

    @POST("auth/signin")
    Call<String> signIn(@Header("Authorization") String authorization);

    @POST("game")
    Call<CurrentGameDto> createGame(
            @Header("Authorization") String authorization,
            @Body CreateGameRequestDto request
    );

    @GET("game/available")
    Call<List<CurrentGameDto>> getAvailableGames(
            @Header("Authorization") String authorization
    );

    @POST("game/{gameId}/join")
    Call<CurrentGameDto> joinGame(
            @Header("Authorization") String authorization,
            @Path("gameId") String gameId
    );

    @GET("game/{gameId}")
    Call<CurrentGameDto> getGame(
            @Header("Authorization") String authorization,
            @Path("gameId") String gameId
    );

    @POST("game/{gameId}")
    Call<CurrentGameDto> makeMove(
            @Header("Authorization") String authorization,
            @Path("gameId") String gameId,
            @Body CurrentGameDto game
    );

    @GET("user/{userId}")
    Call<UserDto> getUser(
            @Header("Authorization") String authorization,
            @Path("userId") String userId
    );
}
