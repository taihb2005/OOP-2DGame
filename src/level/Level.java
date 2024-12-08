package level;

import ai.PathFinder;
import entity.mob.Mon_Shooter;
import entity.mob.Monster;
import graphics.environment.EnvironmentManager;
import ai.PathFinder2;
import main.GamePanel;
import map.GameMap;

import java.awt.*;

import static main.GamePanel.*;
import static main.GamePanel.environmentManager;

public class Level{
    public GamePanel gp;
    public GameMap map;
    protected AssetSetter setter;
    public EventRectangle changeMapEventRect;
    public boolean canChangeMap;

    public boolean levelFinished;
    public boolean finishedBeginingDialogue = false;
    public boolean finishedTutorialDialogue = false;

    public Monster[] monster = new Monster[100];

    public void init(){
        camera.setCamera(windowWidth , windowHeight , map.getMapWidth() ,map.getMapHeight());
        pFinder = new PathFinder(map);
        setter = new AssetSetter(map);
        environmentManager = new EnvironmentManager(map);
        environmentManager.setup();
        environmentManager.lighting.setLightRadius(map.getBestLightingRadius());
        canChangeMap = false;
        levelFinished = false;
    };
    public void updateProgress(){}
    public void render(Graphics2D g2){};
}
