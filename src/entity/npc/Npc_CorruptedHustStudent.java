package entity.npc;

import entity.Actable;
import entity.Entity;
import graphics.Animation;
import graphics.Sprite;
import main.GamePanel;
import main.GameState;
import map.GameMap;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.ArrayList;

import static main.GamePanel.camera;

public class Npc_CorruptedHustStudent extends Entity implements Actable {
    GameMap mp;

    //NPC STATS
    final int IDLE_TYPE1 = 1;
    final int IDLE_TYPE2 = 2;
    final int TALK = 3;

    final int RIGHT = 0;
    final int LEFT = 1;

    private int PREVIOUS_ACTION;
    private int CURRENT_ACTION;
    private int CURRENT_DIRECTION;

    //NPC IMAGE
    private final BufferedImage[][][] npc_corruptedStudent_sprite = new BufferedImage[4][][];
    private final BufferedImage[][][] npc_corruptedStudentGlowing_sprite = new BufferedImage[4][][];
    private BufferedImage[][][] currentSprite;
    private final Animation npc_animator_corruptedStudent;

    //NPC BOOLEAN
    private boolean talkOnce;
    private boolean isTalking;
    private boolean isIdling;
    private int CURRENT_FRAME;

    //NPC RNG
    private final int DESIRED_RNG = 50;
    private final int randomNumerFrames = 500;
    private int frameRandom = 0;
    private int rng = 0;
    private final Random generator = new Random();

    public Npc_CorruptedHustStudent(GameMap mp)
    {
        super();
        this.mp = mp;
        super.width = 64;
        super.height = 64;

        npc_animator_corruptedStudent = new Animation();

        getNpcImage();
        setDefault();
        setDialogue();
    }

    public Npc_CorruptedHustStudent(GameMap mp ,String name ,String direction , StringBuilder[][] dialogue , int x , int y)
    {
        super(x , y);
        this.mp = mp;
        this.idName = name;
        super.width = 64;
        super.height = 64;

        npc_animator_corruptedStudent = new Animation();

        getNpcImage();
        setDefault();
        setDialogue();
        this.direction = direction;
        talkOnce = false;
        switch(direction){
            case "left" : CURRENT_DIRECTION = LEFT; break;
            case "right": CURRENT_DIRECTION = RIGHT ; break;
            default: CURRENT_DIRECTION = RIGHT ; break;
        }
        for(int i = 0 ; i < dialogue.length ;i++){
            for(int j = 0 ; j < dialogue[i].length ; j++){
                this.dialogues[i][j] = dialogue[i][j];
            }
        }
    }

    private void getNpcImage()
    {
        npc_corruptedStudent_sprite[IDLE_TYPE1] = new Sprite("/entity/npc/npc_corruptedstudent_idle1.png" , width , height).getSpriteArray();
        npc_corruptedStudent_sprite[IDLE_TYPE2] = new Sprite("/entity/npc/npc_corruptedstudent_idle2.png" , width , height).getSpriteArray();
        npc_corruptedStudent_sprite[TALK]       = new Sprite("/entity/npc/npc_corruptedstudent_talk.png"  , width , height).getSpriteArray();

        npc_corruptedStudentGlowing_sprite[IDLE_TYPE1] = new Sprite("/entity/npc/npc_corruptedstudent_glowing_idle1.png" , width , height).getSpriteArray();
        npc_corruptedStudentGlowing_sprite[IDLE_TYPE2] = new Sprite("/entity/npc/npc_corruptedstudent_glowing_idle2.png" , width , height).getSpriteArray();
        npc_corruptedStudentGlowing_sprite[TALK] = new Sprite("/entity/npc/npc_corruptedstudent_glowing_talk.png" , width , height).getSpriteArray();
        System.gc();
    }

    private void setDefault()
    {
        CURRENT_DIRECTION = RIGHT;
        direction = "right";

        CURRENT_ACTION = IDLE_TYPE1;
        PREVIOUS_ACTION = IDLE_TYPE1;

        CURRENT_FRAME = 0;
        currentSprite = npc_corruptedStudent_sprite;
        npc_animator_corruptedStudent.setAnimationState(currentSprite[IDLE_TYPE1][CURRENT_DIRECTION] , 5 );

        solidArea1 = new Rectangle(20 , 40 , 30 , 14);
        solidArea2 = new Rectangle(27 , 54 , 18 , 7);
        interactionDetectionArea = new Rectangle(5 , 41 , 58 , 12);
        super.setDefaultSolidArea();

        dialogueIndex = 0;
        dialogueSet = -1;
    }

    @Override
    public void setDialogue() {

    }

    private void handleRNG(){
        frameRandom++;
        if(frameRandom >= randomNumerFrames)
        {
            rng = generator.nextInt(100) + 1;
            frameRandom = 0;
        } else rng = 0;

        if(rng >= DESIRED_RNG)
        {
            isIdling = true;
            npc_animator_corruptedStudent.playOnce();
        }
    }

    private void changeEffect()
    {
        if(GamePanel.gameState == GameState.PLAY_STATE ) isTalking = false;
        if(isInteracting) currentSprite = npc_corruptedStudentGlowing_sprite; else
            currentSprite = npc_corruptedStudent_sprite;
    }

    private void handleAnimationState() {
        if (isIdling && npc_animator_corruptedStudent.isPlaying()) CURRENT_ACTION = IDLE_TYPE2;
        else
        if (isTalking) CURRENT_ACTION = TALK;
        else{
            CURRENT_ACTION = IDLE_TYPE1;
            PREVIOUS_ACTION = IDLE_TYPE1;
            npc_animator_corruptedStudent.setAnimationState(currentSprite[IDLE_TYPE1][CURRENT_ACTION] , 5);
        }

        if (PREVIOUS_ACTION != CURRENT_ACTION) {
            PREVIOUS_ACTION = CURRENT_ACTION;
            if (isIdling && !isTalking) npc_animator_corruptedStudent.setAnimationState(currentSprite[IDLE_TYPE2][CURRENT_DIRECTION], 14);
            if (isTalking) npc_animator_corruptedStudent.setAnimationState(currentSprite[TALK][CURRENT_DIRECTION] , 30);
        }

        if (!npc_animator_corruptedStudent.isPlaying()) isIdling = false;

    }

    private void changeDirection()
    {
        switch (direction){
            case "left" : CURRENT_DIRECTION = LEFT; break;
            case "right": CURRENT_DIRECTION = RIGHT; break;
        }
    }

    private void facePlayer()
    {
        switch(mp.player.direction)
        {
            case "right" : direction = "left"; break;
            case "left"  : direction = "right"; break;
        }
    }

    @Override
    public void move() {

    }

    @Override
    public void set() {

    }

    @Override
    public void talk() {
        talkOnce = true;
        facePlayer();
        dialogueSet++;
        isTalking = true;
        if(dialogues[dialogueSet][0] == null) {
            dialogueIndex = 0;
            dialogueSet--;
        }
        startDialogue(this , dialogueSet);
    }

    @Override
    public void attack() {

    }

    @Override
    public void loot() {

    }

    @Override
    public void update() throws NullPointerException {
        changeEffect();
        handleRNG();
        changeDirection();
        handleAnimationState();
        npc_animator_corruptedStudent.update();
        CURRENT_FRAME = npc_animator_corruptedStudent.getCurrentFrames();
        isInteracting = false;
    }

    @Override
    public void render(Graphics2D g2) {
        g2.drawImage(currentSprite[CURRENT_ACTION][CURRENT_DIRECTION][CURRENT_FRAME] , worldX - camera.getX(), worldY - camera.getY(), null);
    }

    public boolean hasTalkYet(){return talkOnce;}
}

