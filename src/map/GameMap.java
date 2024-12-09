package map;

import entity.Entity;
import entity.mob.Monster;
import entity.object.Obj_Wall;
import entity.player.AttackEnemy;
import entity.player.Player;
import entity.projectile.Projectile;
import level.EventRectangle;
import level.Level;
import main.GamePanel;
import main.GameState;
import main.KeyHandler;
import util.CollisionHandler;

import java.awt.*;
import java.util.*;

import static main.GamePanel.camera;
import static main.GamePanel.scale;


public class GameMap {

    public GamePanel gp;
    public Player player = new Player(this);
    public CollisionHandler cChecker = new CollisionHandler(this);
    public AttackEnemy playerAttack = new AttackEnemy(this);

    public static int childNodeSize = 32;

    private final int mapWidth;
    private final int mapHeight;
    public int maxWorldCol;
    public int maxWorldRow;

    public ArrayList<TileLayer> mapLayer;

    public Entity [] inactiveObj; //Danh sách objects không tương tác được ở trên map
    public Entity [] activeObj;   //Danh sách objects tương tác đươc ở trên map
    public Entity [] npc;//Danh sách target ở trên map
    public Monster [] enemy;
    public Projectile[] projectiles;
    public ArrayList<Entity> objList;     //Danh sách tất cả các object trên map bao gồm player , target,...

    //MAP STAT
    private int bestLightingRadius = 2000;

    private long startTime = System.nanoTime();
    public GameMap(int mapWidth , int mapHeight)
    {
        mapLayer    = new ArrayList<>();
        inactiveObj = new Entity[500];
        activeObj   = new Entity[100];
        npc         = new Entity[100];
        enemy       = new Monster[50];
        projectiles = new Projectile[100];
        objList     = new ArrayList<>();
        dispose();

        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.maxWorldCol = (mapWidth / childNodeSize) + 1 ;
        this.maxWorldRow = (mapHeight/ childNodeSize) + 1;
    }

    public void render(Graphics2D g2)
    {
        objList.clear();
        objList.add(player);
        for (Entity entity : inactiveObj) if(entity != null) objList.add(entity);
        for (Entity entity : activeObj) if(entity != null) objList.add(entity);
        for (Entity entity : npc) if(entity != null) objList.add(entity);
        for (Entity entity : enemy) if(entity != null) objList.add(entity);

        Collections.sort(objList, (e1, e2) -> {
            int index;
            index = Integer.compare(e1.worldY, e2.worldY);
            return index;
        });

        long lasttime = System.nanoTime();
        mapLayer.get(0).render(g2);
        mapLayer.get(0).render(g2); //Base Layer
        mapLayer.get(1).render(g2);
        for (Entity mapObject : objList)
        {
            if(mapObject != null && !mapObject.isCollected) mapObject.render(g2);
        }
        for(Entity projectile : projectiles)
        {
            if(projectile != null) projectile.render(g2);
        }

        long currenttime = System.nanoTime();
        long drawTime;

        //DEBUG MENU
        if (KeyHandler.showDebugMenu) //NHẤN F3 ĐỂ HỆN THỊ TỌA ĐỘ CỦA NHÂN VẬT
        {
            drawTime = currenttime - lasttime;
            g2.setColor(Color.white);
            int x = 10;
            int y = 20;
            int lineHeight = 20;
            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            g2.drawString("WorldX: " + player.worldX, x, y);
            g2.drawString("WorldY: " + player.worldY, x, y + lineHeight);
            g2.drawString("Row: " + (player.worldY + player.solidArea1.y) / 64, x, y + lineHeight * 2);
            g2.drawString("Col: " + (player.worldX + player.solidArea1.x) / 64, x, y + lineHeight * 3);
            g2.drawString("Draw time: " + drawTime, x, y + lineHeight * 4);
            g2.drawString("FPS: " + gp.currentFPS , x , y + lineHeight * 5);
        }

        //DEBUG HITBOX
        if (KeyHandler.showHitbox)  // NHẤN F4 ĐỂ HIỂN THỊ HITBOX CỦA TẤT CẢ CÁC OBJECT
        {
            g2.setColor(Color.YELLOW);
            g2.setStroke(new BasicStroke(1));
            EventRectangle x = new EventRectangle(896 , 1408 , 128, 64 , true);
            g2.drawRect(x.x - camera.getX(), x.y - camera.getY(), x.width, x.height);
            for (Entity e : objList) {
                if (e != null) {
                    g2.drawRect(e.solidAreaDefaultX1 + e.worldX - camera.getX(), e.solidAreaDefaultY1 + e.worldY - camera.getY(), e.solidArea1.width, e.solidArea1.height);
                    if (e.solidArea2 != null) {
                        g2.drawRect(e.solidAreaDefaultX2 + e.worldX - camera.getX(), e.solidAreaDefaultY2 + e.worldY - camera.getY(), e.solidArea2.width, e.solidArea2.height);
                    }
                }
            }
            for(Entity e : projectiles){
                if(e != null){
                    g2.drawRect(e.solidAreaDefaultX1 + e.worldX - camera.getX(), e.solidAreaDefaultY1 + e.worldY - camera.getY(), e.solidArea1.width, e.solidArea1.height);
                }
            }
            g2.setColor(Color.RED);
            for(Entity e : objList){
                if(e != null){
                    if(e.hitbox != null) g2.drawRect(e.hitbox.x + e.worldX - camera.getX() , e.hitbox.y + e.worldY - camera.getY() , e.hitbox.width , e.hitbox.height);
                }
            }
            for(Entity e : projectiles){
                if(e != null){
                    if(e.hitbox != null) g2.drawRect(e.hitbox.x + e.worldX - camera.getX() , e.hitbox.y + e.worldY - camera.getY() , e.hitbox.width , e.hitbox.height);
                }
            }
            g2.setColor(Color.GREEN);
            for(Entity e : enemy){
                if(e != null && e.interactionDetectionArea != null){
                    g2.drawRect(e.interactionDetectionArea.x + e.worldX - camera.getX() , e.interactionDetectionArea.y + e.worldY - camera.getY() , e.interactionDetectionArea.width , e.interactionDetectionArea.height);
                }
            }
        }
    }

    public void update()
    {
        if(GamePanel.gameState == GameState.PLAY_STATE || GamePanel.gameState == GameState.DIALOGUE_STATE || GamePanel.gameState == GameState.PASSWORD_STATE) {

            //UPDATE ENTITY
            for(int i = 0 ; i < activeObj.length ; i++){
                if(activeObj[i] != null){
                    if(activeObj[i].canbeDestroyed){
                        activeObj[i] = null;
                    }
                }
            }
            for(int i = 0 ; i < enemy.length ; i++){
                if(enemy[i] != null){
                    if(enemy[i].canbeDestroyed) {
                        enemy[i] = null;
                    }
                }
            }
            for(int i = 0 ; i < projectiles.length ; i++){
                if(projectiles[i] != null){
                    if(!projectiles[i].active){
                        projectiles[i] = null;
                    }
                }
            }
            //UPDATE ITEM
            for(Entity entity : inactiveObj) if(entity != null) entity.update();
            for(Entity entity : activeObj) if(entity != null) entity.update();
            for(Entity entity : npc) if(entity != null) entity.update();
            for(Entity entity : enemy) if(entity != null) entity.update();
            for(Entity entity : projectiles) if(entity != null) entity.update();
            player.update();
        }
    }

    public void parseWallObject(TileLayer layer){
        for(int i = 0 ; i < layer.numRows ;i++)
        {
            for(int j = 0 ; j < layer.numCols ;j++)
            {
                if(layer.tileLayerData[i][j] == null) continue;

                int tileID = layer.tileLayerDataIndex[i][j];
                int index = layer.getIndexTileSet(layer.tileLayerDataIndex[i][j]);

                if(layer.tileSetList.get(index).objects.get(tileID - 1) != null) {


                    Obj_Wall wall = new Obj_Wall(layer.tileLayerData[i][j], layer.tileSetList.get(index).objects.get(tileID - 1));
                    wall.worldX = layer.tileSetList.get(index).getTileWidth() * j;
                    wall.worldY = layer.tileSetList.get(index).getTileHeight() * i;
                    addObject(wall, inactiveObj);
                }
//                inactiveObj[inactiveObjIndex] = wall;
//                inactiveObjIndex++;
            }
        }
    }

    public void dispose(){
        for(Entity e : activeObj) if(e != null) e.dispose();
        for(Entity e : enemy) if(e != null) e.dispose();
        for(Entity e : projectiles) if(e != null) e.dispose();
        Arrays.fill(inactiveObj, null);
        Arrays.fill(activeObj, null);
        Arrays.fill(npc, null);
        Arrays.fill(enemy, null);
        Arrays.fill(projectiles, null);
        objList.clear();
    }

    public void addObject(Entity entity , Entity[] list){
        for(int i = 0 ; i < list.length ; i++){
            if(list[i] == null){
                list[i] = entity;
                break;
            }
        }
    }

    public void setBestLightingRadius(int r){bestLightingRadius = r;}
    public int getBestLightingRadius(){return bestLightingRadius;}

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

}