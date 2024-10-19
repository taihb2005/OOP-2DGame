package map;

import tile.TileSet;

import java.awt.*;
import java.util.ArrayList;
import static main.GamePanel.scale;

public class GameMap {

    private int mapWidth;
    private int mapHeight;

    ArrayList<TileSet> tileSetList;
    ArrayList<Layer> map;

    public GameMap(int mapWidth , int mapHeight)
    {
        map = new ArrayList<>();
        tileSetList = new ArrayList<>();

        this.mapWidth = mapWidth * scale;
        this.mapHeight = mapHeight * scale;
    }

    public void render(Graphics2D g2)
    {
        for (Layer layer : map) {
            layer.render(g2);
        }
    }

    public void update()
    {

    }


    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }
}