package entity.object;

import entity.Entity;
import graphics.Sprite;

import java.awt.*;
import java.awt.image.BufferedImage;

import static main.GamePanel.camera;

public class Obj_Vault extends Entity {
    private BufferedImage obj_vault;

    public Obj_Vault(String state , int id , int x , int y){
        super(x , y);
        name = "Vault";
        width = 64;
        height = 64;
        solidArea1 = new Rectangle(6 , 37 , 50 , 23);
        solidArea2 = new Rectangle(0 , 0 , 0 , 0);
        setDefaultSolidArea();

        obj_vault = new Sprite("/entity/object/vault_" + state + "_id" + id +".png", width , height).getSpriteSheet();
    }
    @Override
    public void update() {

    }

    @Override
    public void render(Graphics2D g2) {
        g2.drawImage(obj_vault , worldX - camera.getX() , worldY - camera.getY() , width , height , null);
    }
}