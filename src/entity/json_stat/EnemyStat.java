package entity.json_stat;

public class EnemyStat {
    private String enemy;
    private String name;
    private int type;
    private Position position;
    private String direction;
    private boolean isAlwaysUp;
    private int attackCycle;

    // Getter và Setter
    public String getEnemy() { return enemy; }
    public void setEnemy(String object) { this.enemy = object; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }

    public int getX() { return position.getX(); }
    public int getY() { return position.getY(); }

    public String getDirection() {return direction;}

    public boolean isAlwaysUp() {return isAlwaysUp;}

    public int getAttackCycle() {return attackCycle;}
    static class Position {
        private int x;
        private int y;

        public int getX() { return x; }
        public void setX(int x) { this.x = x; }

        public int getY() { return y; }
        public void setY(int y) { this.y = y; }
    }
}

