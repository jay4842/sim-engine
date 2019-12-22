package luna.world.util;

public class TileParameters {
    //xPos,yPos,"food_apple_" + tileMapPos, world_h, world_w, world_scale, test
    public int xPos;
    public int yPos;
    public String type;
    public int world_h;
    public int world_w;
    public int world_scale;
    public boolean test;

    public TileParameters(int xPos, int yPos, String type, int world_h, int world_w, int world_scale, boolean test){
        this.xPos = xPos;
        this.yPos = yPos;
        this.type = type;
        this.world_h = world_h;
        this.world_w = world_w;
        this.world_scale = world_scale;
        this.test = test;
    }

}
