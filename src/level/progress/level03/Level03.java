package level.progress.level03;

import entity.Entity;
import level.EventRectangle;
import level.Level;
import main.GamePanel;
import map.MapManager;
import map.MapParser;

public class Level03 extends Level {
    private Entity[] phase = new Entity[100];
    EventHandler03 eventHandler03;

    public Level03(GamePanel gp) {
        this.gp = gp;
        MapParser.loadMap("map3", "res/map/untitled.tmx");
        map = MapManager.getGameMap("map3");
        if (map == null) System.out.println("null3");
        map.gp = gp;
        eventHandler03 = new EventHandler03(this);
        init();
        setter.setFilePathObject("res/level/level03/object_level03.json");
//        setter.setFilePathNpc("res/level/level03/npc_level03.json");
        setter.setFilePathEnemy("res/level/level03/enemy_level03.json");
        setter.loadAll();
        levelFinished = true;
        changeMapEventRect1 = new EventRectangle(0, 0, 0, 0);
    }
    public void updateProgress() {
        eventHandler03.update();
    }
}