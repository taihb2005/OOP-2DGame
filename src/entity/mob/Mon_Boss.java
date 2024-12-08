package entity.mob;

import entity.Actable;
import entity.effect.type.EffectNone;
import entity.projectile.Proj_ExplosivePlasma;
import entity.projectile.Proj_Flame;
import entity.projectile.Proj_TrackingPlasma;
import entity.projectile.Projectile;
import graphics.Animation;
import graphics.Sprite;
import map.GameMap;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import static main.GamePanel.*;
import static map.GameMap.childNodeSize;

public class Mon_Boss extends Monster implements Actable {
    public final static int IDLE = 0;
    public final static int TALK = 1;
    public final static int RUN = 2;
    public final static int SHOOT_1 = 3;
    public final static int SHOOT_2 = 4;
    public final static int SHOOT_3 = 5;
    public final static int DIE = 6;

    public final static int RIGHT = 0;
    public final static int LEFT = 1;

    private int CURRENT_SKILL = 1;
    private final BufferedImage[][][] mon_boss = new BufferedImage[7][][];
    private final Animation mon_animator_boss = new Animation();
    private int actionLockCounter = 550;
    private final int changeDirCounter = 240;

    private int shootTimer = 0;
    private int shootInterval = 10;

    private int spawnPointX;
    private int spawnPointY;
    private int posX;
    private int posY;
    private int rangeRadius;

    private int detectionCounter = 0;
    private final int detectionToSetAggro = 180;
    private Projectile projectile1, projectile2, projectile3;
    ArrayList<Projectile> proj;
    private int currentColumn = 1;
    private boolean isShooting1, isShooting2, isShooting3;
    private boolean shoot3;

//    private int flameColumnDelayCounter = 0; // Bộ đếm delay giữa các cột
//    private int flameCurrentColumn = 1;     // Cột hiện tại đang được bắn
//    private int maxFlameColumns = 5;        // Tổng số cột flame
    private boolean shooting = false;      // Trạng thái đang bắn flame


    private int testTime = 0;
    public Mon_Boss(GameMap mp){
        super(mp);
        name = "Boss";
        width = 64;
        height = 64;
        speed = 2;

        getImage();
        setDefault();
    }

    public Mon_Boss(GameMap mp , int x , int y){
        super(mp , x , y);
        name = "Boss";
        width = 128;
        height = 128;
        speed = 1;

        getImage();
        setDefault();
    }

    private void getImage(){
        mon_boss[IDLE]  = new Sprite("/entity/mob/boss/boss_idle.png"  , width , height).getSpriteArray();
        mon_boss[TALK]  = new Sprite("/entity/mob/boss/boss_talk.png"  , width , height).getSpriteArray();
        mon_boss[RUN]   = new Sprite("/entity/mob/boss/boss_run.png"   , width , height).getSpriteArray();
        mon_boss[SHOOT_1] = new Sprite("/entity/mob/boss/boss_shoot1.png" , width , height).getSpriteArray();
        mon_boss[SHOOT_2] = new Sprite("/entity/mob/boss/boss_shoot2.png" , width , height).getSpriteArray();
        mon_boss[SHOOT_3] = new Sprite("/entity/mob/boss/boss_shoot3.png" , width , height).getSpriteArray();
        mon_boss[DIE]   = new Sprite("/entity/mob/boss/boss_die.png"   , width , height).getSpriteArray();
    }

    private void setDefault(){
        hitbox = new Rectangle(20 , 40 , 80 , 80);
        solidArea1 = new Rectangle(20 , 110 , 90 , 18);
        solidArea2 = new Rectangle(0 , 0 , 0 , 0);
        interactionDetectionArea = new Rectangle(-50 , -50 , width + 100 , height + 100);
        setDefaultSolidArea();

        invincibleDuration = 30;
        maxHP = 400;
        currentHP = maxHP;
        strength = 10;
        level = 1;
        defense = 10;
        rangeRadius = 200;
        projectile1 = new Proj_TrackingPlasma(mp);
        projectile2 = new Proj_ExplosivePlasma(mp);
        projectile3 = new Proj_Flame(mp);
        proj = new ArrayList<>();
        effectDealOnTouch = new EffectNone(mp.player);
        effectDealByProjectile = new EffectNone(mp.player);

        SHOOT_INTERVAL = 45;
        expDrop = 100;

        direction = "right";
        CURRENT_DIRECTION = RIGHT;

        CURRENT_ACTION = IDLE;
        PREVIOUS_ACTION = IDLE;
        mon_animator_boss.setAnimationState(mon_boss[IDLE][CURRENT_DIRECTION] , 10);
    }

    private void changeAnimationDirection(){
        switch(direction)
        {
            case "left" : CURRENT_DIRECTION = LEFT; break;
            case "right": CURRENT_DIRECTION = RIGHT; break;
        }
    }

    private void setAction() {
        switch (CURRENT_SKILL) {
            case 1: actionWhileShoot1();
            actionLockCounter--;
            if (actionLockCounter == 0) {
                CURRENT_SKILL = 2;
                actionLockCounter = 100;
            }
            break;
            case 2: actionWhileShoot2();
            actionLockCounter--;
            if (actionLockCounter == 0) CURRENT_SKILL = 3;
            break;
            case 3: actionWhileShoot3();
            if (currentColumn >= 10) {
                CURRENT_SKILL = 1;
                actionLockCounter = 100;
                currentColumn = 1;
            }
            break;
        }
    }

    @Override
    public void update() {
        setAction();
        handleAnimationState();
        handleStatus();
        if(!proj.isEmpty())proj.removeIf(pr -> !pr.active);
        changeAnimationDirection();
        move();
        mon_animator_boss.update();
        CURRENT_FRAME = mon_animator_boss.getCurrentFrames();
    }

    @Override
    public void render(Graphics2D g2) {
        if(isInvincible && !isDying){
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER , 0.3f));
        }
        g2.drawImage(mon_boss[CURRENT_ACTION][CURRENT_DIRECTION][CURRENT_FRAME] , worldX - camera.getX() , worldY - camera.getY() , width , height , null);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER , 1.0f));
    }

    private void handleAnimationState(){
        if(isRunning && !isDying) {
            isIdle = false;
            CURRENT_ACTION = RUN;
        }
        else
        if(isShooting1 && !isDying){
            isIdle = false;
            CURRENT_ACTION = SHOOT_1;
        } else
        if(isShooting2 && !isDying){
            isIdle = false;
            CURRENT_ACTION = SHOOT_2;
        } else
        if(shoot3 && !isDying){
            isIdle = false;
            CURRENT_ACTION = SHOOT_3;
        } else
        if(isDying) {
            isIdle = false;
            CURRENT_ACTION = DIE;
        } else
        {
            isIdle = true;
            CURRENT_ACTION = IDLE;
        }

        if(PREVIOUS_ACTION != CURRENT_ACTION)
        {
            PREVIOUS_ACTION = CURRENT_ACTION;
            if(isRunning) mon_animator_boss.setAnimationState(mon_boss[RUN][CURRENT_DIRECTION] , 7);
            if(isDying){
                mon_animator_boss.setAnimationState(mon_boss[DIE][CURRENT_DIRECTION] , 10);
                mon_animator_boss.playOnce();
            }
            if(isShooting1){
                mon_animator_boss.setAnimationState(mon_boss[SHOOT_1][CURRENT_DIRECTION] , 6);
                mon_animator_boss.playOnce();
            }
            if(isShooting2){
                mon_animator_boss.setAnimationState(mon_boss[SHOOT_2][CURRENT_DIRECTION] , 6);
                mon_animator_boss.playOnce();
            }
            if(shoot3){
                mon_animator_boss.setAnimationState(mon_boss[SHOOT_3][CURRENT_DIRECTION] , 6);
                mon_animator_boss.playOnce();
            }
            if(isIdle){
                mon_animator_boss.setAnimationState(mon_boss[IDLE][CURRENT_DIRECTION] , 10);
            }
        }

        if(!mon_animator_boss.isPlaying() && isShooting1){
            isShooting1 = false;
        }
        if(!mon_animator_boss.isPlaying() && isShooting2){
            isShooting2 = false;
        }
        if(!mon_animator_boss.isPlaying() && shoot3){
            shoot3 = false;
        }
        if(!mon_animator_boss.isPlaying() && isDying){
            isDying = false;
            canbeDestroyed = true;
        }

    }
    @Override
    public void move() {
        collisionOn = false;
        if(up && isRunning && !isDying) newWorldY = worldY - speed; //Gì đây hả cđl
        if(down && isRunning && !isDying) newWorldY = worldY + speed;
        if(left && isRunning && !isDying) newWorldX = worldX - speed;
        if(right && isRunning && !isDying) newWorldX = worldX + speed;

        mp.cChecker.checkCollisionWithEntity(this , mp.inactiveObj);
        mp.cChecker.checkCollisionWithEntity(this , mp.activeObj);
        mp.cChecker.checkCollisionWithEntity(this , mp.npc);
        mp.cChecker.checkCollisionWithEntity(this , mp.enemy);
        mp.cChecker.checkCollisionPlayer(this);
//        if(mp.cChecker.checkInteractPlayer(this)) isInteracting = true;

        if(!collisionOn)
        {
            worldX = newWorldX;
            worldY = newWorldY;
        }

        newWorldX = worldX;
        newWorldY = worldY;
    }

    @Override
    public void setDialogue() {

    }

    @Override
    public void attack() {
    }

    @Override
    public void loot() {

    }

    public void shoot3() {
        shooting = true; // Đặt trạng thái bắn
        currentColumn = 1; // Bắt đầu từ cột đầu tiên
    }

    public void shoot1() {
        isShooting1 = true;
        if(!projectile1.active && shootAvailableCounter == SHOOT_INTERVAL) {
            projectile1.set(worldX+25, worldY+12, direction, true, this);
            projectile1.setHitbox();
            projectile1.setSolidArea();
            mp.addObject(projectile1, mp.projectiles);
            shootAvailableCounter = 0;
        }
    }
    public void shoot2() {
        if(!projectile2.active && shootAvailableCounter == SHOOT_INTERVAL) {
            projectile2.set(worldX+78, worldY+60, direction, true, this);
            projectile2.setHitbox();
            projectile2.setSolidArea();
            mp.addObject(projectile2, mp.projectiles);
            shootAvailableCounter = 0;
        }
    }
    public void createFlameColumn(int isLeft) {
            shootAvailableCounter++;
            if (shootAvailableCounter >= 10) {
                for (int j = 1; j <= 5; j++) {
                    Projectile newFlame = new Proj_Flame(mp);
                    newFlame.set(worldX + 50 * isLeft* currentColumn+50, worldY + 50 * (j-1) - 41, direction, true, this);
                    newFlame.setHitbox();
                    newFlame.setSolidArea();
                    proj.add(newFlame);
                }
                currentColumn++;
                for(Projectile flame : proj) mp.addObject(flame , mp.projectiles);
                shootAvailableCounter = 0;
            }
    }

    public void actionWhileShoot1() {
        shoot1();
    }

    public void actionWhileShoot2() {
        int playerCol = (mp.player.worldX + mp.player.solidArea1.x) / childNodeSize;
        int playerRow = (mp.player.worldY + mp.player.solidArea1.y) / childNodeSize;
        int posCol = (worldX + solidArea1.x) / childNodeSize;
        int posRow = (worldY + solidArea1.y) / childNodeSize;
        int isLeft = (worldX < mp.player.worldX)?-1:1;
        int desCol = playerCol;
        int desRow = playerRow;
        searchPath(desCol , desRow);
        decideToMove();

        boolean check3TilesAway = (Math.abs(desCol - posCol) <= 12) || (Math.abs(desRow - posRow) <= 12);
        boolean checkShootInterval = (shootAvailableCounter == SHOOT_INTERVAL);
        boolean checkIfConcurent = (Math.abs(desCol - posCol) == 0) || (Math.abs(desRow - posRow) == 0);
        if (check3TilesAway && checkShootInterval && checkIfConcurent) {
            isShooting2 = true;
            if (Math.abs(worldX-mp.player.worldX) > Math.abs(worldY-mp.player.worldY)) {
                if(worldX < mp.player.worldX)  direction = "right";
                else if(worldX > mp.player.worldX) direction = "left";
            }
             else if(worldY < mp.player.worldY) direction = "down"; else
                direction = "up";
            shoot2();
        }
        isRunning = !isShooting2;
    }

    public void actionWhileShoot3() {
        // Kích hoạt trạng thái bắn nếu chưa bắt đầu
        if (!isShooting3) {
            shoot3 = true;
            isShooting3 = true;
            isRunning = false;
            shootTimer = 0; // Reset shootTimer
            currentColumn = 1; // Reset vị trí cột lửa
        }

        // Xử lý trạng thái bắn
        if (isShooting3) {
            shootTimer++; // Tăng timer mỗi frame
            if (shootTimer >= 10) {
                int isLeft = direction.equals("left") ? -1 : 1;
                createFlameColumn(isLeft); // Tạo cột lửa
                shootTimer = 0; // Reset timer sau khi bắn
            }

            // Dừng bắn sau khi hoàn thành 10 cột lửa
            if (currentColumn > 10) {
                currentColumn = 1;
                isShooting3 = false;
                shoot3 = false;
            }
        }
    }

}