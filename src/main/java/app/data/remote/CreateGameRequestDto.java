package app.data.remote;

public class CreateGameRequestDto {
    private boolean computerOpponent;

    public CreateGameRequestDto(boolean computerOpponent) {
        this.computerOpponent = computerOpponent;
    }

    public boolean isComputerOpponent() {
        return computerOpponent;
    }
}
