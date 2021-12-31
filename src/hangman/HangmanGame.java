package hangman;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HangmanGame {
    // 파일 입출력에 필요한 변수
    private File textFile;

    private List<String> words;
    private List<HangmanRound> rounds;
    private int currentRoundNumber = 1;

    private int winCount = 0;
    private int loseCount = 0;

    private RoundStartListener roundStartListener;
    private long roundStartDelayMillis;
    private RoundProgressListener roundProgressListener;
    private RoundEndListener roundEndListener;

    private GameEndListener gameEndListener;

    private ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();


    public HangmanGame() {
        textFile = new File("words.txt");
        words = new WordSource(textFile, 25143).getWords(9 + 2 + 1);
        System.out.println(words);
        rounds = words.stream().map(word -> new HangmanRound(word)).collect(Collectors.toList());
    }

    private HangmanRound getCurrentRoundNumber() {
        return rounds.get(currentRoundNumber - 1);
    }

    private void delayExecute(Runnable task, long delayMillis) {
        if (delayMillis <= 0)
            task.run();
        else
            es.schedule(task, delayMillis, TimeUnit.MILLISECONDS);
    }

    public void start() {
        roundStartListener.onRoundStart(getCurrentHiddenWord());
    }


    public void setRoundStartListener(RoundStartListener roundStartListener,long beforeDelayMillis) {
        this.roundStartListener = roundStartListener;
        this.roundStartDelayMillis = beforeDelayMillis;
    }

    public void setRoundProgressListener(RoundProgressListener roundProgressListener) {
        this.roundProgressListener = roundProgressListener;
    }

    public void setRoundEndListener(RoundEndListener roundEndListener) {
        this.roundEndListener = roundEndListener;
    }

    public void setGameEndListener(GameEndListener gameEndListener) {
        this.gameEndListener = gameEndListener;
    }


    public void match(char c) {
        HangmanRound currentRound = getCurrentRoundNumber();
        boolean isMatch = currentRound.replaceHiddenWord(c);


        roundProgressListener.onRoundProgress(isMatch, currentRound.getFailCount(), currentRound.getHiddenWord());

        if (currentRound.isRoundFinished()) {
            boolean isRoundWin = currentRound.isRoundWin();
            if (isRoundWin)
                winCount++;
            else
                loseCount++;

            roundEndListener.onRoundEnd(isRoundWin, winCount, loseCount);
            this.currentRoundNumber++;
            if (winCount >= 10)
                gameEndListener.onGameEnd(true);
            else if (loseCount >= 3)
                gameEndListener.onGameEnd(false);
            else
                delayExecute(() -> roundStartListener.onRoundStart(getCurrentHiddenWord())
                        , roundStartDelayMillis);
        }

    }

    public String getCurrentHiddenWord() {
        return getCurrentRoundNumber().getHiddenWord();
    }



    @FunctionalInterface
    interface GameEndListener {
        // 게임이 정상 종료되었을 때 호출
        void onGameEnd(Boolean isGameWin);
    }

    @FunctionalInterface
    interface RoundStartListener {
        // 각 round가 시작될 때 호출
        void onRoundStart(String newHiddenWord);
    }

    @FunctionalInterface
    interface RoundProgressListener {
        // 각 round가 진행될 때 호출; 사용자가 submit 버튼을 누를 때
        void onRoundProgress(Boolean isMatch, Integer roundFailCount, String latestHiddenWord);
    }

    @FunctionalInterface
    interface RoundEndListener {
        // 각 round가 끝날 때 호출
        void onRoundEnd(Boolean isRoundWin, Integer winCount, Integer loseCount);
    }

    private static class HangmanRound {
        private final String word;
        private String hiddenWord;
        private int failCount = 0;

        private boolean isRoundFinished = false;
        private boolean isRoundWin = false;

        public HangmanRound(String word) {
            this.word = word;
            this.hiddenWord = toHiddenWord(word);
        }

        private String toHiddenWord(String word) {
            Random random = new Random();
            int maxHiddenLength = (int) (word.length() * 0.3);
            if (maxHiddenLength <= 0)
                maxHiddenLength = 1;
            int hiddenLength = random.nextInt(maxHiddenLength) + 1;
            char[] wordArr = word.toCharArray();

            random.ints(0, word.length())
                    .distinct()
                    .limit(hiddenLength)
                    .forEach(i -> wordArr[i] = '_');

            return String.valueOf(wordArr);
        }

        public int getFailCount() {
            return failCount;
        }

        public boolean isRoundFinished() {
            return isRoundFinished;
        }

        public boolean isRoundWin() {
            if (!isRoundFinished())
                throw new RuntimeException("Round is not finised");
            return isRoundWin;
        }

        public String getHiddenWord() {
            return hiddenWord;
        }

        private boolean contain(char c) {
            for (int i = 0; i < hiddenWord.length(); i++) {
                if (hiddenWord.charAt(i) == '_' && (Character.toLowerCase(word.charAt(i)) == Character.toLowerCase(c)))
                    return true;
            }
            return false;
        }

        public boolean replaceHiddenWord(char c) {
            if (!contain(c)) {
                failCount++;
                if (failCount >= 5) {
                    isRoundFinished = true;
                    isRoundWin = false;
                }
                return false;
            }

            char[] hiddenWordArr = hiddenWord.toCharArray();
            for (int i = 0; i < hiddenWordArr.length; i++) {
                if (hiddenWordArr[i] == '_' && (Character.toLowerCase(word.charAt(i)) == Character.toLowerCase(c)))
                    hiddenWordArr[i] = c;
            }
            hiddenWord = String.valueOf(hiddenWordArr);
            if (hiddenWord.equalsIgnoreCase(word)) {
                isRoundFinished = true;
                isRoundWin = true;
            }

            return true;
        }

        @FunctionalInterface
        interface RoundEndListener {
            void onRoundEnd(Boolean isRoundSuccess);
        }

    }

    private class WordSource {
        private final int MAX_LINE;
        private final File file;

        public WordSource(File file, int maxLine) {
            if (!file.exists())
                throw new RuntimeException("No Such File : " + file.getName());
            this.file = file;
            this.MAX_LINE = maxLine;
        }

        private String getWordOfLine(int line) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String s = null;
                for (int i = 1; i <= MAX_LINE; i++) {
                    s = reader.readLine();
                    if (line == i)
                        return s;
                }
                throw new IllegalArgumentException(line + " is too long");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // 중복되지 않는 n개의 무작위 단어를 파일에서 읽음
        public List<String> getWords(int n) {
            Random random = new Random();
            Set<Integer> usedLines = new HashSet<>();
            List<String> words = new ArrayList<>();

            while (words.size() < n) {
                int randomLine = random.nextInt(MAX_LINE) + 1;
                if (usedLines.contains(randomLine))
                    continue;
                String word = getWordOfLine(randomLine);
                if (word.length() < 3)
                    continue;
                else {
                    words.add(word);
                    usedLines.add(randomLine);
                }
            }
            return words;
        }

    }


}
