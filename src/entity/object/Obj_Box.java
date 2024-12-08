package entity.object;

import entity.Entity;
import graphics.Sprite;
import main.GamePanel;
import main.GameState;
import main.KeyHandler;

import java.awt.*;
import java.awt.image.BufferedImage;

import static main.GamePanel.camera;

public class Obj_Box extends Entity {
    final private BufferedImage[] obj_box;

    public Obj_Box()
    {
        super();
        name = "Box";
        super.width = 32;
        super.height = 32;
        isInteracting = false;
        obj_box = new Sprite("/entity/object/ITEM_box_wShadow.png", width , height).getSpriteArrayRow(0);

        setDefault();
    }

    private void setDefault()
    {
        solidArea1 = new Rectangle(0 , 9 , 32 , 25);
        hitbox = new Rectangle(9 , 12 , 14 , 12);
        interactionDetectionArea = new Rectangle(3 , 7 , 50 , 50);
        super.setDefaultSolidArea();
    }

    public void handleAnimationState() {
        if (isInteracting) {  // Kiểm tra nếu nhân vật đang tương tác với đối tượng
            if (KeyHandler.enterPressed) {// Đánh dấu là đã thu thập
                collect();
                canbeDestroyed = true;
            }
        }
    }

    private void collect() {
        dialogues[0] = "Bạn đã nhận được 1 box!";
        GamePanel.gameState = GameState.DIALOGUE_STATE;
        startDialogue(this);
    }

    @Override
    public void update() {
        handleAnimationState();
    }

    @Override
    public void render(Graphics2D g2) {
        g2.drawImage(obj_box[0] , worldX - camera.getX(), worldY - camera.getY()
                , width , height , null);
    }

}