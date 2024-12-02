package entity.object;

import entity.Entity;
import graphics.Animation;
import graphics.Sprite;

import java.awt.*;
import java.awt.image.BufferedImage;

import static main.GamePanel.camera;

public class Obj_Computer extends Entity {
    private final BufferedImage[] obj_computer;
    private final Animation obj_animator_computer;
    private int currentFrame = 0;
    public Obj_Computer(String state , int x , int y){
        super(x , y);
        name = "Computer";
        width = 64;
        height = 64;

        obj_computer = new Sprite("/entity/object/computer_" + state + ".png" , width , height).getSpriteArrayRow(0);
        obj_animator_computer = new Animation();
        obj_animator_computer.setAnimationState(obj_computer , 18);
        solidArea1 = new Rectangle(8 , 32 , 48 , 28);
        solidArea2 = new Rectangle(0 , 0 , 0 , 0);
        setDefaultSolidArea();
    }
    @Override
    public void update() {
        obj_animator_computer.update();
        currentFrame = obj_animator_computer.getCurrentFrames();
    }

    @Override
    public void render(Graphics2D g2) {
        g2.drawImage(obj_computer[currentFrame] , worldX -camera.getX() , worldY - camera.getY() , width , height , null);
    }
}
