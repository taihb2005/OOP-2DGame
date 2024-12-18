package level.progress.level00;

import entity.Entity;
import entity.npc.Npc_CorruptedHustStudent;
import entity.object.Obj_Door;
import level.EventHandler;
import level.Level;

import java.util.TimerTask;

public class EventHandler00 extends EventHandler {
    public boolean finishedBeginingDialogue = false;
    public boolean finishedTutorialDialogue = false;
    public EventHandler00(Level lvl) {
        super(lvl);
        setDialogue();
    }
    void checkForTutorialEvent(){
        for(Entity npc : lvl.map.npc){
            if(npc != null && npc.idName.equals("Chill Guy")){
                Npc_CorruptedHustStudent npc_tmp = (Npc_CorruptedHustStudent) npc;
                finishedTutorialDialogue = npc_tmp.hasTalkYet();
            }
        }
    }

    void openTutorialDoor(){
        for(Entity object: lvl.map.activeObj){
            if(object != null && (object.idName.equals("Begin DoorMap 01") || object.idName.equals("Begin DoorMap 02") || object.idName.equals("Begin DoorMap 03"))){
                Obj_Door door = (Obj_Door) object;
                door.canChangeState = true;
            }
        }
    }

    void startingDialogue(){
        TimerTask beginGameDialogue = new TimerTask() {
            @Override
            public void run() {
                eventMaster.startDialogue(eventMaster , 0);
                finishedBeginingDialogue = true;
            }
        };
        timer.schedule(beginGameDialogue , 800);
    }

    private void setDialogue(){
        eventMaster.dialogues[0][0] = new StringBuilder("Năm 2700, bạn nhận được nhiệm vụ\ngiải cứu một đại học...");
        eventMaster.dialogues[0][1] = new StringBuilder("Nhưng ngay sau khi nhận nhiệm vụ\nbạn thấy mình nằm trong một căn\nphòng kì lạ!");
        eventMaster.dialogues[0][2] = new StringBuilder("...Cùng với một gã lạ mặt....");
        eventMaster.dialogues[0][3] = new StringBuilder("Thử đến nói chuyện xem sao.");
    }

    public void update(){
        checkForTutorialEvent();
        if(!finishedBeginingDialogue && !lvl.gp.darker && !lvl.gp.lighter) startingDialogue();
        if(finishedTutorialDialogue) openTutorialDoor();
        lvl.canChangeMap = triggerEvent(lvl.changeMapEventRect1);
        if(lvl.canChangeMap) lvl.levelFinished = true;
    }

}
