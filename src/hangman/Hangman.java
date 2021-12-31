package hangman;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Hangman {

    private JButton startButton;
    private JFrame jFrame;
    private Container contentPane;

    private Font introTitleFont;
    private Font introButtonFont;
    private Font titleFont;
    private Font scoreFont;
    private Font wordFont;
    private Font keyFont;
    private JLabel winLabel;
    private JLabel loseLabel;
    private JLabel wordLabel;
    private JButton[] alphaButtons;
    private JButton[] numberButtons;
    private JButton submitButton;
    private JButton quoteButton;
    private JPanel hangmanPanel;
    private Image image;
    private JPanel jPanel;

    public Hangman() {
        initFont();
        initFrame();
        initIntro();
    }

    private void initFrame() {
        jFrame = new JFrame("2조 Hangman");
        jFrame.setSize(600, 600);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);
        contentPane = jFrame.getContentPane();
        contentPane.setLayout(new GridBagLayout());
    }

    private void initFont() {
        Font font;
        Font boardFont;

        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("font/Font.TTF"));
            introTitleFont = font.deriveFont(100f);
            introButtonFont = font.deriveFont(30f);
            titleFont = font.deriveFont(30f);
            keyFont = font.deriveFont(20f);
            boardFont = Font.createFont(Font.TRUETYPE_FONT, new File("font/BoardFont.TTF"));
            scoreFont = boardFont.deriveFont(30f);
            wordFont = boardFont.deriveFont(30f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    // gridbaglayout 내 위치 선정 함수
    private GridBagConstraints setGridBagConstraint(int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = gridx;
        gridBagConstraints.gridy = gridy;
        gridBagConstraints.gridwidth = gridwidth;
        gridBagConstraints.gridheight = gridheight;
        gridBagConstraints.weightx = weightx;
        gridBagConstraints.weighty = weighty;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        return gridBagConstraints;
    }


    private void initIntro() {
        /*
            Color 관련 변수
         */

        Color borderColor = new Color(0x755139);
        LineBorder lineBorder = new LineBorder(borderColor, 20);
        Color backgroundColor = new Color(1, 80, 0);
        Color foregroundColor = new Color(255, 255, 255);

        /*
            jPanel: title이 위치할 panel
            titleLabel: title
        */
        jPanel = new JPanel(new GridBagLayout());
        jPanel.setBackground(backgroundColor);
        jPanel.setBorder(lineBorder);

        JLabel titleLabel = new JLabel("HANGMAN");
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        titleLabel.setFont(introTitleFont);
        titleLabel.setForeground(foregroundColor);
        titleLabel.setVerticalAlignment(JLabel.BOTTOM);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        startButton = new JButton("Press to Start");
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        startButton.setFont(introButtonFont);
        startButton.setForeground(foregroundColor);
        startButton.setBackground(backgroundColor);
        startButton.setOpaque(true);
        startButton.setVerticalAlignment(JButton.TOP);

        GridBagConstraints titleConstraint = setGridBagConstraint(0, 0, 1, 1, 1, 1);
        GridBagConstraints buttonConstraint = setGridBagConstraint(0, 1, 1, 1, 1, 1);
        jPanel.add(titleLabel, titleConstraint);
        jPanel.add(startButton, buttonConstraint);

        GridBagConstraints jPanelConstraint = setGridBagConstraint(0, 0, 1, 1, 1, 1);
        contentPane.add(jPanel, jPanelConstraint);
    }

    public void initUI() {
        jFrame.remove(jPanel);

        /*
            Color
        */
        Color borderColor = new Color(0x755139);
        LineBorder boardBorder = new LineBorder(borderColor, 20);
        Color boardBackground = new Color(1, 80, 0);
        Color boardForeground = new Color(255, 255, 255);
        Color titleBackground = new Color(0xf2edd7);
        Color keyboardBackground = new Color(0xf2edd7);

        /*
            titleLabel: title
        */
        JLabel titleLabel = new JLabel("HANGMAN");
        titleLabel.setFont(titleFont);
        titleLabel.setBackground(titleBackground);
        titleLabel.setOpaque(true);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        /*
            boardPanel: 칠판이 위치할 panel
            hangmanPanel: hangman 그림
            wordLabel: 맞출 단어
            hitLabel: 맞춘 횟수
            missLabel: 틀린 횟수
         */
        JPanel boardPanel = new JPanel(new GridBagLayout());
        boardPanel.setBorder(boardBorder);
        boardPanel.setBackground(boardBackground);

        /*
            hangmanPanel: hangman이 위치할 panel
         */
        image = new ImageIcon("image/0.png").getImage();
        hangmanPanel = new JPanel() {
            public void paint(Graphics g) {
                g.drawImage(image, 0, 0, null);
            }
        };

        wordLabel = new JLabel("SAMPLE");
        wordLabel.setFont(wordFont);
        wordLabel.setForeground(boardForeground);
        wordLabel.setBackground(boardBackground);
        wordLabel.setOpaque(true);
        wordLabel.setHorizontalAlignment(JLabel.CENTER);
        wordLabel.setVerticalAlignment(JLabel.TOP);

        ImageIcon borderImage = new ImageIcon("image/borderImage.jpg");
        MatteBorder matteBorder = new MatteBorder(3, 3, 3, 3, borderImage);

        winLabel = new JLabel("0/10");
        winLabel.setForeground(boardForeground);
        winLabel.setBackground(boardBackground);
        TitledBorder hitTitledBorder = new TitledBorder(matteBorder, "WIN", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, scoreFont, boardForeground);
        winLabel.setBorder(hitTitledBorder);
        winLabel.setOpaque(true);
        winLabel.setFont(scoreFont);
        winLabel.setHorizontalAlignment(JLabel.CENTER);

        loseLabel = new JLabel("0/3");
        loseLabel.setForeground(boardForeground);
        loseLabel.setBackground(boardBackground);
        TitledBorder missTitledBorder = new TitledBorder(matteBorder, "LOSE", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, scoreFont, boardForeground);
        loseLabel.setBorder(missTitledBorder);
        loseLabel.setOpaque(true);
        loseLabel.setFont(scoreFont);
        loseLabel.setHorizontalAlignment(JLabel.CENTER);

        /*
            keyboardPanel: keyboard가 위치할 Panel
            key: key
         */
        JPanel keyboardPanel = new JPanel();
        keyboardPanel.setBackground(keyboardBackground);
        keyboardPanel.setLayout(new GridBagLayout());
        keyboardPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        numberButtons = new RoundedButton[10];
        alphaButtons = new RoundedButton[26];
        quoteButton = new RoundedButton(String.valueOf((char) ('\'')));

        // number key
        for (int i = 0; i < numberButtons.length; i++) {
            numberButtons[i] = new RoundedButton(String.valueOf(i));
            numberButtons[i].setFont(keyFont);
            GridBagConstraints numberConstraint = setGridBagConstraint(i, 0, 1, 1, 1, 1);
            keyboardPanel.add(numberButtons[i], numberConstraint);
        }

        // alphabet key
        int x = 0;
        int y = 1;
        for (int i = 0; i < alphaButtons.length; i++) {
            alphaButtons[i] = new RoundedButton(String.valueOf((char) ('a' + i)));
            alphaButtons[i].setFont(keyFont);
            x = i - 10 * (y - 1);
            GridBagConstraints alphaConstraint = setGridBagConstraint(x, y, 1, 1, 1, 1);
            keyboardPanel.add(alphaButtons[i], alphaConstraint);
            if (x == 9) y++;
        }

        // quote key
        quoteButton.setFont(keyFont);
        GridBagConstraints quoteConstraint = setGridBagConstraint(6, 3, 1, 1, 1, 1);
        keyboardPanel.add(quoteButton, quoteConstraint);

        // enter key
        submitButton = new RoundedButton("Enter");
        submitButton.setFont(keyFont);
        GridBagConstraints submitConstraint = setGridBagConstraint(7, 3, 3, 1, 1, 1);
        keyboardPanel.add(submitButton, submitConstraint);

        // contentPane 위치 지정
        JLabel emptyLabel = new JLabel();
        GridBagConstraints emptyConstraint = setGridBagConstraint(0, 0, 1, 1, 1, 1);
        GridBagConstraints hangmanConstraint = setGridBagConstraint(0, 0, 1, 3, 5, 5);
        GridBagConstraints wordConstraint = setGridBagConstraint(0, 3, 1, 1, 5, 1);
        GridBagConstraints hitConstraints = setGridBagConstraint(1, 1, 1, 1, 1, 1);
        GridBagConstraints missConstraints = setGridBagConstraint(1, 2, 1, 1, 1, 1);
        hitConstraints.insets = new Insets(0, 0, 5, 20);
        missConstraints.insets = new Insets(5, 0, 0, 20);

        // boardPanel 위치 지정
        GridBagConstraints titleConstraint = setGridBagConstraint(0, 0, 1, 1, 1, 0.5);
        GridBagConstraints boardConstraint = setGridBagConstraint(0, 1, 1, 1, 1, 10);
        GridBagConstraints keyboardConstraint = setGridBagConstraint(0, 2, 1, 1, 1, 1);

        boardPanel.add(hangmanPanel, hangmanConstraint);
        boardPanel.add(wordLabel, wordConstraint);
        boardPanel.add(emptyLabel, emptyConstraint);
        boardPanel.add(winLabel, hitConstraints);
        boardPanel.add(loseLabel, missConstraints);
        contentPane.add(titleLabel, titleConstraint);
        contentPane.add(boardPanel, boardConstraint);
        contentPane.add(keyboardPanel, keyboardConstraint);

        initKeyboardListener();
    }

    public void changeImage(int wrongCount) {
        int width = hangmanPanel.getWidth();
        int height = hangmanPanel.getHeight();

        File file = new File("image/" + wrongCount + ".png");
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        image = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        hangmanPanel.repaint();
    }

    public void changeWinImage() {
        int width = hangmanPanel.getWidth();
        int height = hangmanPanel.getHeight();

        File file = new File("image/win.png");
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        image = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        hangmanPanel.repaint();
    }

    public void show() {
        jFrame.setVisible(true);
    }

    private void initKeyboardListener(){
        // 키보드를 눌렀을 때 이에 대응되는 버튼이 눌리도록 키보드 리스너 등록
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            char key = e.getKeyChar();
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if ('a' <= key && key <= 'z') {
                    alphaButtons[key - 'a'].doClick();
                    return true;
                } else if ('0' <= key && key <= '9') {
                    numberButtons[key - '0'].doClick();
                    return true;
                } else if ('\'' == key) {
                    quoteButton.doClick();
                    return true;
                } else if (key == '\n') {
                    submitButton.doClick();
                    return true;
                }
            }
            return false;
        });
    }


    public void setWinCount(int winCount){
        winLabel.setText(winCount + "/10");
    }

    public void setLoseCount(int loseCount){
        loseLabel.setText(loseCount + "/3");
    }

    public void setHiddenWord(String hiddenWord){
        this.wordLabel.setText(String.join(" ",hiddenWord.split("")));
    }

    public void enableButtons(boolean enabled){
        for (JButton button: numberButtons)
            button.setEnabled(enabled);
        for (JButton button: alphaButtons)
            button.setEnabled(enabled);
        quoteButton.setEnabled(enabled);
        submitButton.setEnabled(enabled);
    }

    public void setKeyButtonActionListener(ActionListener actionListener){
        for (JButton button: numberButtons)
            button.addActionListener(actionListener);
        for (JButton button: alphaButtons)
            button.addActionListener(actionListener);
        quoteButton.addActionListener(actionListener);
    }

    public void setSubmitButtonActionListener(ActionListener actionListener){
        submitButton.addActionListener(actionListener);
    }


    public JButton getStartButton() {
        return startButton;
    }


    private class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            decorate();
            setBackground(new Color(255, 247, 242));
            setForeground(new Color(0, 0, 0));
        }

        protected void decorate() {
            setBorderPainted(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Color backgroundColor = getBackground(); //배경색 결정
            Color foregroundColor = getForeground(); //글자색 결정
            Color disabledColor = Color.lightGray;

            int width = getWidth() - 5;
            int height = getHeight() - 5;

            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isArmed()) {
                graphics.setColor(backgroundColor.darker());
            } else if (getModel().isRollover()) {
                graphics.setColor(backgroundColor.brighter());
            } else {
                graphics.setColor(backgroundColor);
            }

            graphics.fillRoundRect(0, 0, width, height, 10, 10);

            FontMetrics fontMetrics = graphics.getFontMetrics();
            Rectangle stringBounds = fontMetrics.getStringBounds(this.getText(), graphics).getBounds();

            int textX = (width - stringBounds.width) / 2;
            int textY = (height - stringBounds.height) / 2 + fontMetrics.getAscent();
            if (isEnabled())
                graphics.setColor(foregroundColor);
            else
                graphics.setColor(disabledColor);

            graphics.setFont(getFont());
            graphics.drawString(getText(), textX, textY);
            graphics.dispose();

            super.paintComponent(g);
        }
    }
}
