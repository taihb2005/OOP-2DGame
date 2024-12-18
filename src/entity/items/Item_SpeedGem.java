package entity.items;

import entity.effect.Effect;
import entity.effect.type.SpeedBoost;
import entity.player.Player;
import graphics.Sprite;

public class Item_SpeedGem extends Item{
    public Item_SpeedGem() {
        super( 4, new Sprite("/entity/object/ITEM_speedgem.png" , 32 , 32).getSpriteSheet());
        name = new StringBuilder("Ngọc tốc độ");
        description = new StringBuilder("Sử dụng để tăng tốc");
    }

    public void use(Player player){
        if(!player.effectManager.containsKey("Slow") && !player.effectManager.containsKey("Speed Boost")) {
            SpeedBoost speedBoost = new SpeedBoost(player, 300);
            quantity--;
            speedBoost.add();
        }
    }
}
