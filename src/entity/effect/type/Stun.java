package entity.effect.type;

import entity.effect.Effect;
import entity.player.Player;

import java.awt.*;

public class Stun extends Effect {
    public Stun(Player player) {
        super(player);
        id = 3;
    }

    @Override
    public void render(Graphics2D g2) {

    }

    @Override
    public void update() {

    }
}
