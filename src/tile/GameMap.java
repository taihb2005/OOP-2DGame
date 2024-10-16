package tile;

import java.awt.*;
import java.util.ArrayList;

public class GameMap {

    private int mapWidth;
    private int mapHeight;

    ArrayList<TileSet> tileSetList;
    ArrayList<Layer> map;

    public GameMap(int mapWidth , int mapHeight)
    {
        map = new ArrayList<>();
        tileSetList = new ArrayList<>();

        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
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

    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }
}