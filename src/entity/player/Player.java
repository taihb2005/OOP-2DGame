package entity.player;

import entity.effect.Effect;
import entity.Entity;
import entity.items.Item;
import entity.mob.Monster;
import entity.projectile.Proj_BasicProjectile;
import entity.projectile.Projectile;
import graphics.Sprite;
import main.GamePanel;
import main.GameState;
import main.KeyHandler;
import map.GameMap;
import graphics.Animation;
import status.StatusManager;
import level.progress.level02.EventHandler02;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static main.GamePanel.*;

public class Player extends Entity {

    GameMap mp;
    final int IDLE = 0;
    final int RUN  = 1;
    final int TALK = 2;
    final int SHOOT = 3;
    final int RELOAD = 4;
    final int DEATH = 5;

    final int RIGHT = 0;
    final int LEFT = 1;
    final int DOWN = 2;
    final int UP = 3;

    private int PREVIOUS_ACTION;
    private int CURRENT_ACTION;
    private int CURRENT_DIRECTION;

    private boolean isRunning;
    private boolean isShooting;
    public boolean isDying = false;

    public boolean attackCanceled;
    public final int screenX, screenY;

    private final BufferedImage[][][] player_gun = new BufferedImage[7][][];
    final protected Animation animator = new Animation();

    private int CURRENT_FRAME;
    public int SHOOT_INTERVAL ;
    public int nextLevelUp = 60;

    //PLAYER STATUS
    public int blindRadius = 200;
    private final int invincibleDuration = 60;
    private final int manaHealInterval = 180;
    private int manaHealCounter = 0;
    public HashMap<StringBuilder , Integer> effectManager = new HashMap<>();
    public ArrayList<Effect> effect = new ArrayList<>();
    public Item [] inventory = new Item[5];
    public ItemHandler iHandler = new ItemHandler();

    public Player(GameMap mp) {
        super();
        name = "Player";
        this.mp = mp;
        width = 64;
        height = 64;
        speed = 3;
        last_speed = speed;

        hitbox = new Rectangle(25 , 40 , 15 , 20);
        solidArea1 = new Rectangle(26 , 52 , 18 , 6);
        setDefaultSolidArea();

        screenX = GamePanel.windowWidth/2 - 32;
        screenY = GamePanel.windowHeight/2 - 32;

        getPlayerImages();
        setDefaultValue();
    }

    public void setDefaultValue()
    {
        projectile_name = "Basic Projectile";
        projectile = new Proj_BasicProjectile(mp);
        SHOOT_INTERVAL = projectile.maxHP + 5;

        attackCanceled = false;
        up = down = left = right = false;
        direction = "right";
        CURRENT_DIRECTION = RIGHT;
        PREVIOUS_ACTION = IDLE;
        CURRENT_ACTION = IDLE;
        CURRENT_FRAME = 0;
        animator.setAnimationState(player_gun[IDLE][RIGHT] , 5);

        Arrays.fill(inventory , null);
        resetValue();
    }

    public void storeValue(){
        sManager.setPos(worldX , worldY);
        sManager.setSavedHP(maxHP);
        sManager.setSavedMana(maxMana);
        sManager.setLevel(level);
        sManager.setExp(exp);
        sManager.setInventory(inventory);
        sManager.setDirection(direction);
    }

    public void resetValue(){
        effectManager.clear();
        effect.clear();
        level = sManager.getSavedLevel();
        exp = sManager.getSavedExp();
        worldX = sManager.getWorldX();
        worldY = sManager.getWorldY();
        newWorldX = worldX; newWorldY = worldY;
        sManager.getSavedInventory(inventory);
        set();
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



    private void keyInput()
    {
        //GOD MODE
        if(KeyHandler.godModeOn){
            hitbox = new Rectangle(0 , 0 , 0 , 0);
            solidArea1 = new Rectangle(0 , 0 , 0 , 0);
        }

        if(isDying) KeyHandler.disableKey();
        up    = KeyHandler.upPressed;
        down  = KeyHandler.downPressed;
        left  = KeyHandler.leftPressed;
        right = KeyHandler.rightPressed;

        //RUN
        isRunning = up | down | left | right;

        if(GamePanel.gameState == GameState.PLAY_STATE) attackCanceled = false; else
            if(GamePanel.gameState == GameState.DIALOGUE_STATE) attackCanceled = true;
        //SHOOT
        if (KeyHandler.enterPressed) {
            if (!attackCanceled && !isRunning && shootAvailableCounter == SHOOT_INTERVAL) {
                isShooting = true;
                shootProjectile();
            }
        }
        iHandler.useItem(this);

        //isShooting = shoot;
    }

    private void handleAnimationState() {
        if(isShooting && !isRunning  && !attackCanceled ) {
            CURRENT_ACTION = SHOOT;
        }else if(isRunning)
        {
            CURRENT_ACTION = RUN;
            switchDirection();
        } else if(isDying){
            CURRENT_ACTION = DEATH;
        } else
        {
            animator.setAnimationState(player_gun[IDLE][CURRENT_DIRECTION] , 5);
            CURRENT_ACTION = IDLE;
            PREVIOUS_ACTION = IDLE;
        }

        if(PREVIOUS_ACTION != CURRENT_ACTION)
        {
            PREVIOUS_ACTION = CURRENT_ACTION;
            if(isRunning) animator.setAnimationState(player_gun[CURRENT_ACTION][CURRENT_DIRECTION] , 10);
            if(isShooting && !isRunning)
            {
                animator.setAnimationState(player_gun[SHOOT][CURRENT_DIRECTION] , 6);
                animator.playOnce();
            }
            if(isDying){
                animator.setAnimationState(player_gun[DEATH][CURRENT_DIRECTION] , 10);
                animator.playOnce();
            }
        }
        if ((!animator.isPlaying() && isShooting) || isRunning) isShooting = false;
        if (!animator.isPlaying() && isDying){
            isDying = false;
            GamePanel.gameState = GameState.LOSE_STATE;
            stopMusic();
            playMusic(4);
        }
    }

    private void changeAnimationDirection() {
        switch(direction)
        {
            case "left" : CURRENT_DIRECTION = LEFT; break;
            case "right": CURRENT_DIRECTION = RIGHT; break;
            case "up"   : CURRENT_DIRECTION = UP; break;
            case "down" : CURRENT_DIRECTION = DOWN ; break;
        }
    }
    private void switchDirection(){
        if(left) direction = "left";
        else if(right) direction = "right";
        else if(up) direction = "up";
        else if(down) direction = "down";
    }

    private void handlePosition() {
        isInteracting = false;
        interactNpc(mp.cChecker.checkInteractEntity(this , true , mp.npc));
        interactObject(mp.cChecker.checkInteractWithActiveObject(this , true));
        collisionOn = false;
        if (up && isRunning && !isShooting) {
            if(right){newWorldX += speed / 2; newWorldY -= speed / 2;} else
            if(left){newWorldX -= speed / 2 ; newWorldY -= speed / 2;} else
                if(!down) newWorldY -= speed;
        }
        if (down && isRunning && !isShooting) {
            if(right){newWorldX += speed / 2; newWorldY += speed / 2;} else
            if(left){newWorldX -= speed / 2 ; newWorldY += speed / 2;} else
            if(!up) newWorldY += speed;
        }
        if (left && isRunning && !isShooting) {
            if(up){newWorldX -= speed / 2; newWorldY -= speed / 2;} else
            if(down){newWorldX -= speed / 2 ; newWorldY += speed / 2;} else
                if(!right) newWorldX -= speed;
        }
        if (right && isRunning && !isShooting) {
            if(up){newWorldX += speed / 2; newWorldY -= speed / 2;} else
            if(down){newWorldX += speed / 2 ; newWorldY += speed / 2;} else
            if(!left) newWorldX += speed;
        }

        mp.cChecker.checkCollisionWithEntity(this , mp.inactiveObj);
        mp.cChecker.checkCollisionWithEntity(this , mp.activeObj);
        mp.cChecker.checkCollisionWithEntity(this , mp.npc);
        mp.cChecker.checkCollisionWithEntity(this, mp.enemy);

        if(!collisionOn)
        {
            worldX = newWorldX;
            worldY = newWorldY;
        }
        newWorldX = worldX;
        newWorldY = worldY;

        if(isShooting && !attackCanceled){
            GamePanel.camera.cameraShake(worldX , worldY);
        } else GamePanel.camera.centerOn(worldX , worldY);
    }

    private void handleStatus(){
        if(shootAvailableCounter < SHOOT_INTERVAL){
            shootAvailableCounter++;
        }
        if(shootAvailableCounter > SHOOT_INTERVAL) shootAvailableCounter = SHOOT_INTERVAL;

        if(isInvincible){
            invincibleCounter++;
            if(invincibleCounter >= invincibleDuration){
                invincibleCounter = 0;
                isInvincible = false;
            }
        }
    }

    private void interactNpc(int index) {
        if(index != -1)
        {
            mp.npc[index].isInteracting = true;
            attackCanceled = true;
            isInteracting = true;
            if(GamePanel.gameState == GameState.PLAY_STATE && KeyHandler.enterPressed) {
                KeyHandler.enterPressed = false;
                mp.npc[index].talk();
            }
        }
    }
    private void interactObject(int index) {
        if(index != -1){
            attackCanceled = true;
            isInteracting = true;
//            if(KeyHandler.enterPressed)
//            {
//                KeyHandler.enterPressed = false;
//                mp.activeObj[index].isOpening = true;
//            }
        }
    }

    private void shootProjectile() {
        checkForMana();
        if(!projectile.active && !isInteracting && shootAvailableCounter == SHOOT_INTERVAL && hasResource()){
            mp.gp.playSE(2);
            projectile.set(worldX , worldY , direction , true , this);
            projectile.setHitbox();
            projectile.setSolidArea();
            currentMana -= projectile.manaCost;
            updateMana();
            mp.addObject(projectile , mp.projectiles);
            shootAvailableCounter = 0;
        }
    }

    public void damageEnemy(int index){
        if(index != -1){
            switch (mp.enemy[index].name){
                case "Spectron": mp.playerAttack.damageEnemy(index); projectile.active = false ; break;
                case "Shooter": mp.playerAttack.damageShooter(index); projectile.active = false; break;
                case "Hust Guardian": mp.playerAttack.damageGuardian(index); projectile.active = false; break;
                case "Cyborgon"   : mp.playerAttack.damageCyborgon(index); projectile.active = false; break;
                case "Effect Dealer": mp.playerAttack.damageEffectDealer(index); break;
                case "Boss": mp.playerAttack.damageEnemy(index); projectile.active = false; break;
            }
            if(mp.enemy[index].currentHP <= 0){
                exp += mp.enemy[index].expDrop;
                System.out.println("Current exp: " + exp);
                mp.enemy[index].currentHP = 0;
                mp.enemy[index].die();

                checkForLevelUp();

            }
        }
    }

    public void receiveDamage(Projectile proj , Entity attacker){
        currentHP = currentHP - (proj.base_damage + attacker.strength) ;
        System.out.println("Receive " + ((proj.base_damage + attacker.strength)) + " damage");
    }

    public void receiveDamage(Monster attacker){
        currentHP = currentHP - (attacker.strength);
        System.out.println("Receive " + ((attacker.strength)) + " damage");
    }

    public void updateInventory(){
        for(int i = 0 ; i < inventory.length ; i++){
            if (inventory[i] != null) {
                if(inventory[i].getQuantity() == 0){
                    inventory[i] = null;
                }
            }
        }
    }
    //DEMO
    private void updateHP() {
        if(currentHP > maxHP) currentHP = maxHP; else
            if(currentHP < 0) currentHP = 0;
        if (currentHP == 0) {
            isRunning = false;
            isDying = true;
        }
    }

    private void updateMana(){
        if(currentMana > maxMana) currentMana = maxMana;
        if(currentMana < 0) currentMana = 0;
    }

    private void updateEffect(){
        if(!effect.isEmpty()){
            for(Effect e : effect) {
                e.update();
                if(e.effectFinished){
                    e.remove();
                    effectManager.remove(e.name);
                }
            }
            effect.removeIf(e-> e.effectFinished);
        }
    }

    private void checkForMana(){
        if(!hasResource() && isShooting){
            isShooting = false;
            GamePanel.gameState = GameState.DIALOGUE_STATE;
            dialogues[0][0] = new StringBuilder("Không đủ mana!\nBạn cần " + projectile.manaCost + " mana(s) để bắn");
            startDialogue(this , 0);
            KeyHandler.enterPressed = false;
        }
    }

    private void healMana(){
        manaHealCounter++;
        if(manaHealCounter >= manaHealInterval){
            manaHealCounter = 0;
            currentMana += 20;
        }
        updateMana();
    }

    public boolean hasResource(){
        return currentMana >= projectile.manaCost;
    }

    public void set(){
        setDamage();
        setDefense();
        setMaxHP();
        setMaxMana();
    }

    private void setDamage(){
        strength = 10;
        damage = projectile.base_damage + strength * level ;
    }
    private void setDefense(){
        defense = level * 10;
    }
    private void setMaxHP(){
        maxHP = 150 + (level - 1) * 40;
        currentHP = maxHP;
    }
    private void setMaxMana(){
        maxMana = 100 + (level - 1) * 15;
        currentMana = maxMana;
    }
    public void checkForLevelUp(){
        if(exp >= nextLevelUp)
        {
            level++;
            set();
            exp = nextLevelUp;
            if(level == 1) nextLevelUp = 30; else
            if(level == 2) nextLevelUp = 150; else
            if(level == 3) nextLevelUp = 300; else
            if(level == 4) nextLevelUp = 700; else
            if(level == 5) nextLevelUp = 999999999;
            //GamePanel.gameState = GameState.DIALOGUE_STATE;
            playSE(3);
            dialogues[0][0] = new StringBuilder("Lên cấp!\nBạn lên cấp " + level + "\nChỉ số của bạn đều được tăng!");
            startDialogue(this , 0);
        }
    }

    @Override
    public void update()
    {
        keyInput();
        handlePosition();
        handleStatus();
        changeAnimationDirection();
        updateInventory();
        updateHP();
        healMana();
        updateEffect();
        handleAnimationState();
        animator.update();
        CURRENT_FRAME = animator.getCurrentFrames();
    }


    @Override
    public void render(Graphics2D g2)
    {
        if(isInvincible && !isDying){
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER , 0.6f));
        }
        g2.drawImage(player_gun[CURRENT_ACTION][CURRENT_DIRECTION][CURRENT_FRAME] ,
                worldX - GamePanel.camera.getX() , worldY - GamePanel.camera.getY(),
                width, height, null);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER , 1.0f));
        int positionY = worldY - camera.getY() + 20;
        if(!effect.isEmpty()){
            for(int i = 0 ; i < effect.size() ; i++){
                int positionX = worldX - camera.getX() + 35 + 20 * i;
                g2.drawImage(effect.get(i).icon , positionX , positionY , null);
            }
        }
    }

    public void setPosition(int x , int y){
        worldX = x;
        worldY = y;
        newWorldX = worldX; newWorldY = worldY;
    }
}
