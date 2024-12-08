package entity.object;

import entity.Entity;
import graphics.Sprite;

import java.awt.*;
import java.awt.image.BufferedImage;

import static main.GamePanel.camera;

public class Obj_Desk extends Entity {
    private final BufferedImage obj_desk;
    public int type;

    public Obj_Desk(int type)
    {
        super();
        name = "Desk";
        super.width = 64;
        super.height = 64;

        this.type = type;
        obj_desk = new Sprite("/entity/object/desk_id" + type + ".png" , width , height).getSprite(0 , 0);

        setDefault();
    }

    private void setDefault()
    {
        solidArea1 = new Rectangle(8 , 24 , 48 , 32);
        solidArea2 = new Rectangle(24 , 56 , 24 , 4);
        super.setDefaultSolidArea();

    }

    @Override
    public void update() {

    }

    @Override
    public void render(Graphics2D g2) {
        g2.drawImage(obj_desk , worldX - camera.getX() , worldY - camera.getY() , null);
    }
}