package luna.entity.util;

// a simple class to manage how an entity thinks of another
/*
* Entities will have a list of bonds and a bond will be created on first contact.
*  - Note: the initial contact will be affected by other factors. (will be defined later)
* */
public class Bond {
    protected int entityID;
    protected int bondLevel;

    // a bond level can go negative or past 100. For instance if the bond is negative that means
    //  the entity the bond relates too will most likely never be raised.
    public Bond(int id){
        this.entityID = id;
        this.bondLevel = 50; // starting level (normal gage 0 to 100)
    }

    public void updateBond(int x){
        this.bondLevel += x;
    }

    public int getEntityID() {
        return entityID;
    }

    public void setEntityID(int entityID) {
        this.entityID = entityID;
    }

    public int getBondLevel() {
        return bondLevel;
    }

    public void setBondLevel(int bondLevel) {
        this.bondLevel = bondLevel;
    }

    public boolean compareTo(Bond b){
        if(getEntityID() == b.getEntityID())
            return true;
        return false;
    }
}
