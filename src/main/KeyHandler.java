package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{

    GamePanel gp;
    public static boolean upPressed;
    public static boolean downPressed;
    public static boolean rightPressed;
    public static boolean leftPressed;
    public static boolean showDebugMenu = false;
    public static boolean showHitbox = false;

    public KeyHandler(GamePanel gp)
    {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e){

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if(keyCode == KeyEvent.VK_S)
        {
            downPressed = true;
        }

        if(keyCode == KeyEvent.VK_W)
        {
            upPressed = true;
        }

        if(keyCode == KeyEvent.VK_D)
        {
            rightPressed = true;
        }

        if(keyCode == KeyEvent.VK_A)
        {
            leftPressed = true;
        }

        if(keyCode == KeyEvent.VK_ESCAPE)
        {
            if(gp.gameState == GameState.PLAY_STATE)
            {
                gp.gameState = GameState.PAUSE_STATE;
            } else if(gp.gameState == GameState.PAUSE_STATE)
            {
                gp.gameState = GameState.PLAY_STATE;
            }
        }
        if(keyCode == KeyEvent.VK_F3)
        {
            showDebugMenu = !showDebugMenu;
        }

        if(keyCode == KeyEvent.VK_F4)
        {
            showHitbox = !showHitbox;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        switch (keyCode)
        {
            case KeyEvent.VK_S: downPressed = false; break;
            case KeyEvent.VK_A: leftPressed = false; break;
            case KeyEvent.VK_D: rightPressed = false; break;
            case KeyEvent.VK_W: upPressed = false; break;
        }
    }
}