package util;

import entity.Entity;
import map.GameMap;

import java.awt.*;

public class CollisionHandler {

    GameMap mp;

    public CollisionHandler(GameMap mp) {
        this.mp = mp;
    }

    public int checkInteractWithActiveObject(Entity entity , boolean isPlayer)
    {
        int index = -1;
        for (int i = 0; i < mp.activeObj.length; i++) {
            if (mp.activeObj[i] != null) {

                int newSolidAreaX1 = entity.newWorldX + entity.solidArea1.x;
                int newSolidAreaY1 = entity.newWorldY + entity.solidArea1.y;
                Rectangle tmp1 = new Rectangle(mp.activeObj[i].worldX + mp.activeObj[i].interactionDetectionArea.x, mp.activeObj[i].worldY + mp.activeObj[i].interactionDetectionArea.y,
                        mp.activeObj[i].interactionDetectionArea.width, mp.activeObj[i].interactionDetectionArea.height);
                Rectangle tmp = new Rectangle(newSolidAreaX1, newSolidAreaY1, entity.solidArea1.width, entity.solidArea1.height);

                if (tmp.intersects(tmp1)) {
                    if(isPlayer) {
                        mp.activeObj[i].isInteracting = true;
                        index = i;
                    }
                    break;
                }
            }
        }
        return index;
    }

    public int checkInteractWithNpc(Entity entity , boolean isPlayer){
        int index = -1;
        for (int i = 0; i < mp.npc.length; i++) {
            if (mp.npc[i] != null) {

                int newSolidAreaX1 = entity.newWorldX + entity.solidArea1.x;
                int newSolidAreaY1 = entity.newWorldY + entity.solidArea1.y;
                Rectangle tmp1 = new Rectangle(mp.npc[i].worldX + mp.npc[i].interactionDetectionArea.x, mp.npc[i].worldY + mp.npc[i].interactionDetectionArea.y,
                        mp.npc[i].interactionDetectionArea.width, mp.npc[i].interactionDetectionArea.height);
                Rectangle tmp = new Rectangle(newSolidAreaX1, newSolidAreaY1, entity.solidArea1.width, entity.solidArea1.height);

                if (tmp.intersects(tmp1)) {
                    if(isPlayer) {
                        mp.npc[i].isInteracting = true;
                        index = i;
                    }
                    break;
                }
            }
        }
        return index;
    };

    public int checkEntityForDamage(Entity entity , Entity [] list){
        int index = -1;
        for (int i = 0; i < list.length; i++) {
            if (list[i] != null) {
                int newSolidAreaX1 = entity.worldX + entity.solidArea1.x;
                int newSolidAreaY1 = entity.worldY + entity.solidArea1.y;
                Rectangle tmp1 = new Rectangle(list[i].worldX + list[i].hitbox.x , list[i].worldY + list[i].hitbox.y,
                        list[i].hitbox.width , list[i].hitbox.height);
                Rectangle tmp = new Rectangle(newSolidAreaX1 , newSolidAreaY1, entity.solidArea1.width , entity.solidArea1.height);

                if(tmp.intersects(tmp1)) {
                    index = i;
                    break;
                }

//                if(list[i].solidArea2 != null)
//                {
//                    Rectangle tmp2 = new Rectangle(list[i].worldX + list[i].solidArea2.x , list[i].worldY + list[i].solidArea2.y,
//                            list[i].solidArea2.width , list[i].solidArea2.height);
//                    if(tmp.intersects(tmp2)) {
//                        index = i;
//                        break;
//                    }
//
//                }
            }
        }

        return index;
    }


    public int checkCollisionWithEntity(Entity entity , Entity [] list)
    {
        int index = -1;
        for (int i = 0; i < list.length; i++) {
            if (list[i] != null) {

                int newSolidAreaX1 = entity.newWorldX + entity.solidArea1.x;
                int newSolidAreaY1 = entity.newWorldY + entity.solidArea1.y;
                Rectangle tmp1 = new Rectangle(list[i].worldX + list[i].solidArea1.x , list[i].worldY + list[i].solidArea1.y,
                        list[i].solidArea1.width , list[i].solidArea1.height);
                Rectangle tmp = new Rectangle(newSolidAreaX1 , newSolidAreaY1, entity.solidArea1.width , entity.solidArea1.height);

                if(tmp.intersects(tmp1)) {
                    entity.collisionOn = true;
                    index = i;
                    if(list[i].solidArea2 == null) break;
                }

                if(list[i].solidArea2 != null)
                {
                    Rectangle tmp2 = new Rectangle(list[i].worldX + list[i].solidArea2.x , list[i].worldY + list[i].solidArea2.y,
                            list[i].solidArea2.width , list[i].solidArea2.height);
                    if(tmp.intersects(tmp2)) {
                        entity.collisionOn = true;
                        index = i;
                        break;
                    }

                }
            }
        }
        return index;
    }


}
