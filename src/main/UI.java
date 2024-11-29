package main;

import entity.Entity;
import entity.player.Player;
import main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;


import static main.GamePanel.*;

public class UI {
    private final GamePanel gp;
    public Player player;
    Graphics2D g2;
    public Font joystix;
    String currentDialogue = "";  // Dòng hội thoại hiện tại đầy đủ
    String displayedText = "";    // Dòng hội thoại đang được hiển thị dần
    int textIndex = 0;            // Chỉ số của ký tự đang được hiển thị
    double textSpeed = 0.1;       // Tốc độ hiển thị từng ký tự (càng nhỏ càng nhanh)
    int frameCounter = 0;         // Đếm số frame để điều khiển tốc độ hiển thị
    int subState = 0;
    int dialogueCount;
    int cursor = 0;

    private boolean slotselected = true;
    int selectedSlot = 0;

    public int commandNum = 0;

    public Entity target;

    private BufferedImage hpFrame, manaFrame;

    private BufferedImage titleBackground;

    private BufferedImage titleImage;

    private BufferedImage Key1Image;

    public Entity npc;
    public UI(GamePanel gp) {
        this.gp = gp;
        this.player = gp.currentMap.player;
        try {
            InputStream is = getClass().getResourceAsStream("/font/joystix monospace.otf");
            joystix = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        try {
            hpFrame = ImageIO.read(getClass().getResourceAsStream("/ui/hpFrame.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            manaFrame = ImageIO.read(getClass().getResourceAsStream("/ui/manaFrame.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            titleBackground = ImageIO.read(getClass().getResourceAsStream("/ui/Background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            titleImage  = ImageIO.read(getClass().getResourceAsStream("/ui/1.2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Key1Image  = ImageIO.read(getClass().getResourceAsStream("/ui/Screenshot 2024-11-29 233940.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Bắt đầu đoạn hội thoại ngay khi khởi tạo UI
        // DEMO
        //startDialogue("Tôi là siêu anh hùng đến từ HUST.\nCon hồ ly tinh này đáng sợ quá");
    }
    public void startDialogue(String dialogue) {
        currentDialogue = dialogue;
        displayedText = "";
        textIndex = 0;
        frameCounter = 0;
    }

    public void drawDialogueScreen() {
        g2.setFont(joystix.deriveFont(Font.PLAIN, 19));
        int x = tileSize * 2;
        int y = tileSize / 2;
        int width = gp.getWidth() - tileSize * 4;
        int height = tileSize * 4;

        if(target.dialogues[target.dialogueIndex] != null) {
            currentDialogue = target.dialogues[target.dialogueIndex];
            drawSubWindow(x, y, width, height);

            frameCounter++;
            if (frameCounter > textSpeed) {
                frameCounter = 0;
                if (textIndex < currentDialogue.length()) {
                    // Cập nhật displayedText theo từng ký tự
                    gp.playSE(1);
                    displayedText += currentDialogue.charAt(textIndex);
                    textIndex++;
                }
            }
            if(KeyHandler.enterPressed)
            {
                textIndex = 0;
                displayedText = "";
                if(gameState == GameState.DIALOGUE_STATE )
                {
                    target.dialogueIndex++;
                    KeyHandler.enterPressed = false;
                }
            }
        }else
        {
            target.dialogueIndex--;
            if(gameState == GameState.DIALOGUE_STATE)
            {
                gameState = GameState.PLAY_STATE;
            }
        }

        // Vẽ đoạn hội thoại từng dòng
        x += tileSize;
        y += tileSize;
        for (String line : displayedText.split("\n")) { // splits dialogue until "\n" as a line
            g2.drawString(line, x, y);
            y += 40;
        }

    }

    public void drawSubWindow(int x, int y, int width, int height) {
        Color c = new Color(0,0,0, 100); //BLACK
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        c = new Color(255,255,255); // WHITE
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x, y, width, height, 25, 25);
    }
    // Hàm vẽ thử

    public void drawTitleScreen(){
        g2.drawImage(titleBackground,0, 0, windowWidth, windowHeight, null);

        //Title name
        g2.setFont(joystix.deriveFont(Font.PLAIN, 80f));
        String text = "HUST Core";
        int x = getXforCenteredText(text);
        int y = windowHeight / 4;

        //Shadow
        g2.setColor(Color.black);
        g2.drawString(text,x+5,y+5);

        //Colour
        g2.setColor(Color.white);
        g2.drawString(text , x , y );

        //Draw Picture

        //Menu
        g2.setFont(joystix.deriveFont(Font.PLAIN, 30f));

        text = "PLAY";
        x = getXforCenteredText(text);
        y = windowHeight*3/5;
        g2.drawString(text, x, y);
        if(commandNum == 0){
            g2.drawString(">",x-gp.tileSize, y);
        }

        text = "OPTIONS";
        x = getXforCenteredText(text);
        y = windowHeight*7/10;
        g2.drawString(text, x, y);
        if(commandNum == 1){
            g2.drawString(">",x-gp.tileSize, y);
        }

        text = "QUIT";
        x = getXforCenteredText(text);
        y = windowHeight*4/5;
        g2.drawString(text, x, y);
        if(commandNum == 2){
            g2.drawString(">",x-gp.tileSize, y);
        }
    }

    private void drawPausedScreen()
    {
        g2.setColor(new Color(0 , 0 , 0 , 100));
        g2.fillRoundRect(0, 0, windowWidth, windowHeight , 0 , 0);
        g2.setFont(joystix.deriveFont(Font.PLAIN, 80f));
        g2.setColor(Color.WHITE);
        String text = "";
        int x = getXforCenteredText(text);
        int y = windowHeight / 2;
        g2.drawString(text , x , y );
    }

    public int getXforCenteredText(String text)
    {
        int length = (int)g2.getFontMetrics().getStringBounds(text , g2).getWidth();
        int x = windowWidth / 2 - length / 2;
        return x;
    }

    public void render(Graphics2D g2) {
        this.g2 = g2; // Gán đối tượng g2 vào để sử dụng
        if(gameState == GameState.PLAY_STATE)
        {
            drawHPBar();
            drawManaBar();
            drawInventory();
        }
        else if(gameState == GameState.MENU_STATE)
        {
            drawTitleScreen();
        }
        else if(gameState == GameState.DIALOGUE_STATE)
        {
            drawDialogueScreen();
            drawHPBar();
            drawManaBar();
            drawInventory();
        }
        if(gameState == GameState.LEVELUP_STATE){
            drawDialogueScreen();
        } else
        if(gameState == GameState.PAUSE_STATE)
        {
            drawHPBar();
            drawManaBar();
            drawPausedScreen();
            drawOptionsScreen();
            drawInventory();
        }
        if(gameState == GameState.LOSE_STATE){
            gp.currentMap.dispose();
            drawGameOverScreen();
        }
        if(gameState == GameState.SETTING_STATE){

            drawSettingScreen();

        }
    }

    public Font getFont(){return joystix;};

    public void drawHPBar() {
        int fullHPWidth = 175;  // Chiều dài tối đa của thanh HP
        int hpBarHeight = 12;   // Chiều cao của thanh HP
        int x = 70;
        int y = windowHeight - 80;
        int currentHPWidth = (int)((double)player.currentHP / player.maxHP * fullHPWidth);
        // Vẽ nền (màu xám) cho thanh HP
        g2.drawImage(hpFrame, x-52, y-8, 251, 28, null);

        // Vẽ thanh HP hiện tại (màu đỏ)
        g2.setColor(Color.RED);
        g2.fillRect(x, y , currentHPWidth, hpBarHeight);
    }

    public void drawManaBar() {
        int fullManaWidth = 63;  // Chiều dài tối đa của thanh HP
        int ManaBarHeight = 12;   // Chiều cao của thanh HP
        int x = 70;
        int y = windowHeight - 45;
        int currentHPWidth = (int)((double)player.currentMana / player.maxMana * fullManaWidth);
        // Vẽ nền (màu xám) cho thanh Mana
        g2.drawImage(manaFrame, x-52, y-8, 139, 28, null);

        // Vẽ thanh HP hiện tại (màu xanh)
        g2.setColor(Color.BLUE);
        g2.fillRect(x, y, currentHPWidth, ManaBarHeight);
    }

    public void drawRewardWindow(){
        g2.setFont(joystix.deriveFont(Font.PLAIN, 19));
        int x = tileSize * 2;
        int y = tileSize / 2;
        int width = gp.getWidth() - tileSize * 4;
        int height = tileSize * 4;
        drawSubWindow(x , y , width , height);
    }

    public void drawLevelUpWindow(){
        g2.setFont(joystix.deriveFont(Font.PLAIN, 19));
        int x = tileSize * 2;
        int y = tileSize / 2;
        int width = gp.getWidth() - tileSize * 4;
        int height = tileSize * 4;
        drawSubWindow(x , y , width , height);
    }

    public void drawGameOverScreen()
    {
        g2.drawImage(titleBackground,0, 0, windowWidth, windowHeight, null);
        g2.setFont(joystix.deriveFont(Font.PLAIN, 80f));
        String text = "You sucked!";
        int x = getXforCenteredText(text);
        int y = windowHeight *2/5;
        g2.drawString(text , x , y);

        g2.setColor(Color.black);
        g2.drawString(text,x+5,y+5);

        //Colour
        g2.setColor(Color.white);
        g2.drawString(text , x , y );

        //RETRY
        g2.setFont(joystix.deriveFont(Font.PLAIN, 30f));
        text = "RETRY";
        x = getXforCenteredText(text);
        y = windowHeight*3/5;
        g2.drawString(text, x, y);
        if(commandNum == 0){
            g2.drawString(">",x-gp.tileSize, y);
        }

        text = "QUIT";
        x = getXforCenteredText(text);
        y = windowHeight*7/10;
        g2.drawString(text, x, y);
        if(commandNum == 1){
            g2.drawString(">",x-gp.tileSize, y);
        }
    }

    public void drawOptionsScreen() {
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(32F));

        // SUB WINDOW

        int frameX = gp.tileSize * 4;
        int frameY = gp.tileSize;
        int frameWidth = gp.tileSize * 8;
        int frameHeight = gp.tileSize * 10;

        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        switch(subState) {
            case 0: options_top(frameX, frameY); break;
        }

    }
    public void options_top(int frameX, int frameY) {
        int textX, textY;
        // Thử với Consolas
        g2.setFont(joystix.deriveFont(Font.PLAIN, 19));
        // TITLE
        String text = "Options";
        textX = getXforCenteredText(text);
        textY = frameY + tileSize;
        g2.drawString(text, textX, textY);

        // MUSIC
        textX = frameX + tileSize;
        textY += tileSize*2;
        g2.drawString("Music", textX, textY);
        drawSubWindow(textX+185, textY-25 ,tileSize*3/2, tileSize);
        g2.drawString(String.valueOf(music.volumePercentage), textX + 202, textY);
        g2.drawString("-",textX +150, textY);
        g2.drawString("+",textX+280, textY);
        if (commandNum == 0) {
            g2.drawString(">", textX-25, textY);
        }

        // SE
        textX = frameX + tileSize;
        textY += tileSize*2;
        g2.drawString("SE", textX, textY);
        drawSubWindow(textX+185, textY-25 ,tileSize*3/2, tileSize);
        g2.drawString(String.valueOf(se.volumePercentage), textX + 202, textY);
        g2.drawString("-",textX +150, textY);
        g2.drawString("+",textX+280, textY);
        if (commandNum == 1) {
            g2.drawString(">", textX-25, textY);
        }

        // RETRY
        textX = frameX + tileSize;
        textY += tileSize*2;
        g2.drawString("Restart", textX, textY);
        if (commandNum == 2) {
            g2.drawString(">", textX-25, textY);
        }

        // EXIT
        textX = frameX + tileSize;
        textY += tileSize*2;
        g2.drawString("Exit", textX, textY);
        if (commandNum == 3) {
            g2.drawString(">", textX-25, textY);
        }
    }
    public void drawInventory() {

        int frameX = tileSize / 4;
        int frameY = tileSize / 2 + 80;
        int frameWidth = tileSize * 2 - 18;
        int frameHeight = tileSize * 6 + 32;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        int slotX = frameX + 15;
        int slotY = frameY + 12;
        int slotSize = tileSize / 4;

        if (KeyHandler.key1pressed) {
            selectedSlot = 0;
            slotselected = true;
            KeyHandler.key1pressed = false;
        } else if (KeyHandler.key2pressed) {
            selectedSlot = 1;
            slotselected = true;
            KeyHandler.key2pressed = false;
        } else if (KeyHandler.key3pressed) {
            selectedSlot = 2;
            slotselected = true;
            KeyHandler.key3pressed = false;
        } else if (KeyHandler.key4pressed) {
            selectedSlot = 3;
            slotselected = true;
            KeyHandler.key4pressed = false;
        } else if (KeyHandler.key5pressed) {
            selectedSlot = 4;
            slotselected = true;
            KeyHandler.key5pressed = false;
        }

        g2.setFont(joystix.deriveFont(Font.PLAIN, 15));

        for (int i = 0; i < 5; i++) {
            int currentslotY = slotY + i * (slotSize + 50);
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillRoundRect(slotX, currentslotY, 50, 50, 10, 10);

            g2.setColor(new Color(255, 255, 255));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(slotX, currentslotY, 50, 50, 10, 10);

            String numSlot = Integer.toString(i + 1);
            g2.drawString(numSlot , slotX + 33 , currentslotY + 44);

            if (slotselected && i == selectedSlot)
            {
                g2.setStroke(new BasicStroke(5));
                g2.drawRoundRect(slotX, currentslotY, 50, 50, 10, 10);
            }
        }
    }
    public void drawSettingScreen(){
        g2.drawImage(titleBackground,0, 0, windowWidth, windowHeight, null);
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(32F));

        // SUB WINDOW

        int frameX = gp.tileSize * 4;
        int frameY = gp.tileSize;
        int frameWidth = gp.tileSize * 8;
        int frameHeight = gp.tileSize * 10;

        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        switch(subState) {
            case 0: setting_top(frameX, frameY); break;
            case 1: control(frameX, frameY); break;
        }
    }
    public void setting_top(int frameX, int frameY) {
        int textX, textY;
        // Thử với Consolas
        g2.setFont(joystix.deriveFont(Font.PLAIN, 19));
        // TITLE
        String text = "Options";
        textX = getXforCenteredText(text);
        textY = frameY + tileSize;
        g2.drawString(text, textX, textY);
        // MUSIC
        textX = frameX + tileSize;
        textY += tileSize*2;
        g2.drawString("Music", textX, textY);
        drawSubWindow(textX+185, textY-25 ,tileSize*3/2, tileSize);
        g2.drawString(String.valueOf(music.volumePercentage), textX + 202, textY);
        g2.drawString("-",textX +150, textY);
        g2.drawString("+",textX+280, textY);
        if (commandNum == 0) {
            g2.drawString(">", textX-25, textY);
        }

        // SE
        textX = frameX + tileSize;
        textY += tileSize*2;
        g2.drawString("SE", textX, textY);
        drawSubWindow(textX+185, textY-25 ,tileSize*3/2, tileSize);
        g2.drawString(String.valueOf(se.volumePercentage), textX + 202, textY);
        g2.drawString("-",textX +150, textY);
        g2.drawString("+",textX+280, textY);
        if (commandNum == 1) {
            g2.drawString(">", textX-25, textY);
        }

        //CONTROL
        textX = frameX + tileSize;
        textY += tileSize*2;
        g2.drawString("CONTROL", textX, textY);
        if (commandNum == 2) {
            g2.drawString(">", textX-25, textY);
        }

        // EXIT
        textX = frameX + tileSize;
        textY += tileSize*2;
        g2.drawString("Exit", textX, textY);
        if (commandNum == 3) {
            g2.drawString(">", textX-25, textY);
        }
    }
    public void control(int frameX, int frameY){
        int textX, textY;
        g2.setFont(joystix.deriveFont(Font.PLAIN, 19));
        //TITLE
        String text = "CONTROL";
        textX = getXforCenteredText(text);
        textY = frameY + tileSize;
        g2.drawString(text, textX, textY);

        //BANG HO TRO
        textX = frameX + tileSize/2;
        textY += tileSize;

        g2.drawString("MOVE", textX, textY);
        if(cursor == 0) g2.drawString(">", textX-25, textY);
        textY+=tileSize;

        g2.drawString("ATTACK", textX, textY);
        if(cursor ==1) g2.drawString(">", textX-25, textY);
        textY+=tileSize;

        g2.drawString("BUY/USE ITEMS", textX, textY);
        if(cursor ==2) g2.drawString(">", textX-25, textY);
        textY+=tileSize;

        g2.drawString("PAUSE/ESCAPE", textX, textY);
        if(cursor == 3) g2.drawString(">", textX-25, textY);
        textY+=tileSize;

        g2.drawString("COMMUNICATE", textX, textY);
        if(cursor == 4) g2.drawString(">", textX-25, textY);

        //THONG TIN
        textX = frameX + tileSize*11/2;
        textY = frameY + tileSize*2;

        g2.drawString("W A S D", textX, textY);
        textY+=tileSize;

        g2.drawString("ENTER", textX, textY);
        textY+=tileSize;

        g2.drawString("1 2 3 4", textX, textY);
        textY+=tileSize;

        g2.drawString("ESC", textX, textY);
        textY+=tileSize;

        g2.drawString("ENTER", textX, textY);


    }
}