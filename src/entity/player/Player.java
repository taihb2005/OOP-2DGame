package entity.player;

import entity.Entity;
import entity.Projectile;
import graphics.Sprite;
import main.GamePanel;
import main.GameState;
import main.KeyHandler;
import map.GameMap;
import graphics.Animation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player extends Entity {

    GameMap mp;
    final int IDLE = 0;
    final int RUN = 1;
    final int TALK = 2;
    final int SHOOT = 3;
    final int RELOAD = 4;
    final int DEATH = 5;

    final int RIGHT = 0;
    final int LEFT = 1;

    private int PREVIOUS_ACTION;
    private int CURRENT_ACTION;
    private int CURRENT_DIRECTION;

    private boolean isRunning;
    private boolean isShooting;
    private boolean isReloading;
    private boolean isDying;

    private boolean attackCanceled;

    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;
    private boolean shoot;
    private boolean die;
    
    public final int screenX, screenY;

    private BufferedImage[][][] player_gun = new BufferedImage[8][][];;
    private BufferedImage[][][] player_nogun = new BufferedImage[8][][];
    private BufferedImage bullet;

    private ArrayList<Projectile> projectiles = new ArrayList<Projectile>(); // chứa các viên đạn

    private int CURRENT_FRAME;


    final protected Animation animator = new Animation();

    public Player(GameMap mp) {
        super();
        this.mp = mp;
        width = 64;
        height = 64;

        solidArea1 = new Rectangle(27 , 53 , 13 , 6);
        solidAreaDefaultX1 = 27;
        solidAreaDefaultY1 = 53;

        screenX = GamePanel.windowWidth/2 - 32;
        screenY = GamePanel.windowHeight/2 - 32;
        bullet = new Sprite("/entity/player/projectile_id1.png", width, height).getSpriteSheet();

        getPlayerImages();
        setDefaultValue();
    }

    private void setDefaultValue()
    {
        worldX = 1400;
        worldY = 1700;
        newWorldX = worldX;
        newWorldY = worldY;
        speed = 3;

        attackCanceled = false;
        up = down = left = right = false;
        direction = "right";
        CURRENT_DIRECTION = RIGHT;
        PREVIOUS_ACTION = IDLE;
        CURRENT_ACTION = IDLE;
        CURRENT_FRAME = 0;
        animator.setAnimationState(player_gun[IDLE][RIGHT] , 5);
    }

    private void getPlayerImages()
    {
        player_gun[IDLE]   = new Sprite("/entity/player/idle_gun_blue.png"   , width , height).getSpriteArray();
        player_gun[TALK]   = new Sprite("/entity/player/talk_gun_blue.png"   , width , height).getSpriteArray();
        player_gun[RUN]    = new Sprite("/entity/player/run_gun_blue.png"    , width , height).getSpriteArray();
        player_gun[SHOOT]  = new Sprite("/entity/player/shoot_gun_blue.png"  , width , height).getSpriteArray();
        player_gun[RELOAD] = new Sprite("/entity/player/reload_gun_blue.png" , width , height).getSpriteArray();
        player_gun[DEATH]  = new Sprite("/entity/player/death_blue.png"      , width , height).getSpriteArray();
    }

    @Override
    public void update()
    {
        keyInput();
        handlePosition();
        changeDirection();
        //handleCollision();
        handleAnimationState();
        animator.update();
        CURRENT_FRAME = animator.getCurrentFrames();

        projectiles.removeIf(b -> !b.active);
            for (Projectile single_projectile : projectiles) {
                single_projectile.update();

    }
    }


    @Override
    public void render(Graphics2D g2)
    {
        g2.drawImage(player_gun[CURRENT_ACTION][CURRENT_DIRECTION][CURRENT_FRAME] ,
                     worldX - GamePanel.camera.getX() , worldY - GamePanel.camera.getY(),
                     width, height, null);

        for (Projectile single_projectile : projectiles) {
            single_projectile.render(g2);
        }
    }

    private void keyInput()
    {
        up    = KeyHandler.upPressed;
        down  = KeyHandler.downPressed;
        left  = KeyHandler.leftPressed;
        right = KeyHandler.rightPressed;

        //RUN
        isRunning = up | down | left | right;

        if(GamePanel.gameState == GameState.PLAY_STATE) attackCanceled = false; else
            if(GamePanel.gameState == GameState.DIALOGUE_STATE) attackCanceled = true;
        //SHOOT
        if (KeyHandler.enterPressed && !attackCanceled) {
                if (!isRunning) {
                    isShooting = true;
                    animator.playOnce();
                    shootProjectile();
                }
        }
        //isShooting = shoot;
    }

    public void shootProjectile() {
        if (animator.isPlaying()) { // đang bắn
            int startX = worldX;
            int startY = worldY;
            if (CURRENT_DIRECTION == RIGHT) {
                startX += width;
            }
            else {
                startX -= width;
            }
            int speed = 5;
            int direction = (CURRENT_DIRECTION == RIGHT) ? 1 : 0;
            BufferedImage bulletImage = bullet;
            Projectile newProjectile = new Projectile(startX, startY, speed, direction, bulletImage);
            projectiles.add(newProjectile);

            animator.setAnimationState(player_gun[SHOOT][CURRENT_DIRECTION], 6);
            animator.playOnce();
        }

    }

    private void handleAnimationState()
    {
        if(isShooting && !isRunning && animator.isPlaying() && !attackCanceled) {
            CURRENT_ACTION = SHOOT;

        }else if(isRunning && !animator.isPlaying())
        {
            CURRENT_ACTION = RUN;
            if(left)
            {
                direction = "left";
            } else if(right)
            {
                direction = "right";
            }
        } else
        {
            animator.setAnimationState(player_gun[IDLE][CURRENT_DIRECTION] , 5);
            CURRENT_ACTION = IDLE;
            PREVIOUS_ACTION = IDLE;
        }

        if(PREVIOUS_ACTION != CURRENT_ACTION)
        {
            PREVIOUS_ACTION = CURRENT_ACTION;
            if(isRunning)
            {
                animator.setAnimationState(player_gun[CURRENT_ACTION][CURRENT_DIRECTION] , 10);
            }
            if(isShooting && !isRunning)
            {
                animator.setAnimationState(player_gun[SHOOT][CURRENT_DIRECTION] , 6);
                animator.playOnce();
            }
        }

        if (!animator.isPlaying()) {
            isShooting = false;
        }

        //System.out.println(frameCounts);

    }

    private void changeDirection()
    {
        switch(direction)
        {
            case "left" : CURRENT_DIRECTION = LEFT; break;
            case "right": CURRENT_DIRECTION = RIGHT; break;
        }
    }

//    private void handleCollision(){
//        collisionOn = false;
//        onlyChangeDirection = false;
//        mp.cChecker.checkObjectCollsion(this);
//    }

    private void handlePosition()
    {
        int index = mp.cChecker.checkInteractWithNpc(this , true);
        interactNpc(index);
        collisionOn = false;
        if (up && isRunning && !isShooting) {
            if(right){newWorldX += 1; newWorldY -= 1;} else
            if(left){newWorldX -= 1 ; newWorldY -= 1;} else
                if(!down) newWorldY -= speed;
        }
        if (down && isRunning && !isShooting) {
            if(right){newWorldX += 1; newWorldY += 1;} else
            if(left){newWorldX -= 1 ; newWorldY += 1;} else
            if(!up) newWorldY += speed;
        }
        if (left && isRunning && !isShooting) {
            if(up){newWorldX -= 1; newWorldY -= 1;} else
            if(down){newWorldX -= 1 ; newWorldY +=1;} else
                if(!right) newWorldX -= speed;
        }
        if (right && isRunning && !isShooting) {
            if(up){newWorldX += 1; newWorldY -= 1;} else
            if(down){newWorldX += 1 ; newWorldY += 1;} else
            if(!left) newWorldX += speed;
        }

        mp.cChecker.checkCollisionWithInactiveObject(this);
        mp.cChecker.checkCollisionWithNpc(this , true);

        if(!collisionOn)
        {
            worldX = newWorldX;
            worldY = newWorldY;
        }
        newWorldX = worldX;
        newWorldY = worldY;

        GamePanel.camera.centerOn(worldX , worldY);
    }

    private void interactNpc(int index)
    {
        if(index != -1)
        {
            attackCanceled = true;
            if(GamePanel.gameState == GameState.PLAY_STATE && KeyHandler.enterPressed) {
                KeyHandler.enterPressed = false;
                GamePanel.gameState = GameState.DIALOGUE_STATE;
                mp.npc.get(index).talk();
            }
        }
    }

}