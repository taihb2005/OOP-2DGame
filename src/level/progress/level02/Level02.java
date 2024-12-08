package level.progress.level02;

import level.EventRectangle;
import level.Level;
import level.AssetSetter;
import level.progress.level02.EventHandler02;
import entity.npc.Npc_CorruptedHustStudent;
import main.GamePanel;
import map.MapManager;
import map.MapParser;

import java.awt.*;

public class Level02 extends Level {
    private EventHandler02 eventHandler02;

    public Level02(GamePanel gp) {
        this.gp = gp;
        MapParser.loadMap("map2", "res/map/map2.tmx");
        map = MapManager.getGameMap("map2");
        map.gp = gp;
        init();
        setter.setFilePathObject("res/level/level02/object_level02.json");
        setter.setFilePathNpc(null);
        setter.setFilePathEnemy("res/level/level02/enemy_level02.json");
        setter.loadAll();
        eventHandler02 = new EventHandler02(this);

        changeMapEventRect = new EventRectangle(768, 768, 200, 200);
    }

    public void updateProgress(){
       eventHandler02.update();
    }
    public void render(Graphics2D g2){eventHandler02.render(g2);}
}
