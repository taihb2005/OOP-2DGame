package level.progress.level04;

import ai.PathFinder;
import ai.PathFinder2;
import entity.Entity;
import entity.mob.Mon_Boss;
import level.EventRectangle;
import level.Level;
import level.progress.level03.EventHandler03;
import main.GamePanel;
import map.MapManager;
import map.MapParser;

import static main.GamePanel.pFinder;

public class Level04 extends Level {
    private Entity[] phase = new Entity[100];
    public EventHandler04 eventHandler04;

    public Level04(GamePanel gp) {
        this.gp = gp;
        MapParser.loadMap("map4", "/map/map4.tmx");
        map = MapManager.getGameMap("map4");
        map.gp = gp;
        eventHandler04 = new EventHandler04(this);
        init();
        setter.setFilePathObject("/level/level04/object_level04.json");
        setter.loadAll();
        GamePanel.pFinder2 = new PathFinder2(map);
        Mon_Boss boss = new Mon_Boss(map, 500, 900);
        map.addObject(boss, map.enemy);
        levelFinished = false;
        changeMapEventRect1 = new EventRectangle(0, 0, 0, 0);
        GamePanel.environmentManager.lighting.setLightRadius(1000);
    }
    public void updateProgress() {
        eventHandler04.update();
    }
}