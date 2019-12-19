package tests.world;

import luna.main.Game;
import luna.util.Tile;
import luna.world.World;

import java.util.ArrayList;
import java.util.List;

// creating world items/objects for other tests usually
public class WorldTest {

    public static List<List<Tile>> makeTestMap(){
        List<List<Tile>> tileMap = new ArrayList<>();
        int height = Game.HEIGHT;
        int width = Game.WIDTH;
        int world_scale = Game.world_scale;
        int count = 0;

        for(int y = 0; y < height/world_scale; y++){
            tileMap.add(new ArrayList<Tile>());
            for(int x = 0; x < width/world_scale; x++){
                //tileMap.get(y).add(new Tile(x*world_scale,y*world_scale,count, World.world_scale,this.height,this.width, 0));
                if(y == 5 && x == 5)
                    tileMap.get(y).add(new Tile(x*world_scale,y*world_scale,count, world_scale,height,width, 2, -1, true));
                else if(y == 1 && x == 1){
                    tileMap.get(y).add(new Tile(x*world_scale,y*world_scale,count, world_scale,height,width, 5, -1, true));
                }else if(y == 1 && x == 3){
                    tileMap.get(y).add(new Tile(x*world_scale,y*world_scale,count, world_scale,height,width, 6, -1, true));
                }
                else if (Math.random()*100 > 98 || count==0) {
                    tileMap.get(y).add(new Tile(x * world_scale, y * world_scale, count, world_scale, height, width, 1, -1, true));
                    count++;
                }
                else
                    tileMap.get(y).add(new Tile(x*world_scale,y*world_scale,count, world_scale,height,width, 0, -1, true));
                //*/
                count++;
            }
        }

        return tileMap;
    }
}
