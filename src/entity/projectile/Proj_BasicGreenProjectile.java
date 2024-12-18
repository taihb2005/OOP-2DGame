package entity.projectile;

import graphics.Sprite;
import map.GameMap;

import java.awt.*;

public class Proj_BasicGreenProjectile extends Projectile{

    public Proj_BasicGreenProjectile(GameMap mp)
    {
        super(mp);
        name = "Basic Green Projectile";
        width = 64;
        height = 64;
        maxHP = 80;
        speed = 10;
        base_damage = 5;
        solidArea1 = new Rectangle(28 , 30 , 8 , 8);
        hitbox = new Rectangle(28 , 30, 8 , 8);
        solidArea2 = new Rectangle(0  ,0 , 0 , 0);
        setDefaultSolidArea();
        getImage();
        projectile_animator.setAnimationState(projectile_sprite[CURRENT_DIRECTION] , 10);
    }

    private void getImage(){
        projectile_sprite = new Sprite("/entity/projectile/basic_green_projectile.png" , width , height).getSpriteArray();
    }

}
