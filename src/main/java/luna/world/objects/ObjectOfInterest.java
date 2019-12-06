package luna.world.objects;

import java.awt.*;

public class ObjectOfInterest extends InteractableObject {

    // An object of interest will have a subMap attached to it.
    // - This will be a tileMap that is either dynamically created, or a map that only is generated once.
    //   Dungeons will be generated only once
    //   Exploration maps, battle maps, and other small need maps will be dynamically created.
    //   Additionally these maps are much smaller and can only be a max size of 16x16 but will
    //   generally be 6x6 maps.
    //   Battle maps will be 4x4
    //   Exploration maps will be 6x6 to 16x16
    //
    public ObjectOfInterest(int xPos, int yPos, String type, int objectID, int world_h, int world_w, int world_scale) {
        super(xPos, yPos, type, objectID, world_h, world_w, world_scale);
    }

    // base render
    public void render(Graphics2D g2d) {
        // This will be done based on the type of object
        super.render(g2d);
    }

    public void update(int seconds) {
        // this will remove itself
        super.update(seconds);
    }
}
