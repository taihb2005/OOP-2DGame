package level;

import main.GamePanel;

import static main.GamePanel.*;

public class LevelManager {
    GamePanel gp;
    private final int maxLevel = 2;
    public LevelManager(GamePanel gp){this.gp = gp;}
    public void update() {
        if (levelProgress == previousLevelProgress) {
            if (levelProgress == 0) levelProgress = 1;
            else if (levelProgress == 1) levelProgress = 2;
            else if (levelProgress == 2) levelProgress = 3;
            else if (levelProgress == 3) levelProgress = 4;
            currentMap.player.storeValue();
            gp.loadMap();
            previousLevelProgress = levelProgress;

        }
    }
}
