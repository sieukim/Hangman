package hangman;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class HangmanIntegration {

    private Hangman hangman;
    private HangmanGame game;

    private Character selectedChar;
    private JButton selectedButton;


    private void onKeyButtonClicked(ActionEvent e) {
        JButton eventSource = ((JButton) e.getSource());
        selectedChar = eventSource.getText().charAt(0);
        selectedButton = eventSource;
    }

    private void onSubmitButtonClicked(ActionEvent e) {
        if (selectedChar != null && selectedButton != null) {
            // 한번 눌린 버튼은 다시 눌리지 못하도록 설정
            selectedButton.setEnabled(false);
            game.match(selectedChar);
            selectedChar = null;
            selectedButton = null;
        }
    }

    private void onRoundStart(String newHiddenWord) {
        hangman.enableButtons(true);
        hangman.changeImage(0);
        hangman.setHiddenWord(newHiddenWord);
    }

    private void onRoundProgress(Boolean isMatch, Integer roundFailCount, String latestHiddenWord) {
        if (isMatch)
            hangman.setHiddenWord(latestHiddenWord);
        else
            hangman.changeImage(roundFailCount);
    }

    private void onRoundEnd(Boolean isRoundWin, Integer winCount, Integer loseCount) {
        if (isRoundWin)
            hangman.changeWinImage();
        else
            hangman.changeImage(5);
        hangman.setWinCount(winCount);
        hangman.setLoseCount(loseCount);
        hangman.enableButtons(false);
    }

    private void onGameEnd(Boolean isGameWin) {
        hangman.enableButtons(false);
        hangman.setHiddenWord(isGameWin ? "Win" : "Lose");
        Executors.newSingleThreadScheduledExecutor().schedule(() -> System.exit(0), 3, TimeUnit.SECONDS);
    }


    public HangmanIntegration() {

    }


    public void start() {
        hangman = new Hangman();

        game = new HangmanGame();

        hangman.getStartButton().addActionListener(e -> {
            hangman.initUI();
            hangman.show();

            // 이벤트 리스너 등록
            game.setRoundStartListener(this::onRoundStart, 1500);
            game.setRoundProgressListener(this::onRoundProgress);
            game.setRoundEndListener(this::onRoundEnd);

            game.setGameEndListener(this::onGameEnd);


            hangman.setKeyButtonActionListener(this::onKeyButtonClicked);
            hangman.setSubmitButtonActionListener(this::onSubmitButtonClicked);

            game.start();
        });

        hangman.show();

    }

    public static void main(String[] args) {
        new HangmanIntegration().start();
    }


}