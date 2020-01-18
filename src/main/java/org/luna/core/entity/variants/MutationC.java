package org.luna.core.entity.variants;

import org.luna.core.entity.Entity;
import org.luna.core.util.Utility;


public class MutationC extends Entity {

    public MutationC(int world_scale, int[] gps){
        super(world_scale, gps);
    }

    protected void setStats(){
        short lifeSpan = (short)(Utility.getRnd().nextInt(3) + 13);
        replicationAge = (short)(lifeSpan/3); // once an entity is 1/3 through its life it can replicate
        //hp, maxHp, xp, maxXp, lvl, dmg, speed, sense, energy, maxEnergy, lifeSpanInTurns
        stats = new short[]{10,10,0,10,0,1,2,5,15,15,lifeSpan};
        this.deathChance = .1f;
        this.replicationChance = .19f;
        baseEnergyCost = 0.2f;
        refreshStep = 8;
        type = 3;
    }

    public Entity makeEntity(){
        if(Utility.getRnd().nextFloat() < .15)
            return new MutationD(getScale(), new int[]{getGps()[0], getGps()[1], getGps()[2]});
        return new MutationC(getScale(), new int[]{getGps()[0], getGps()[1], getGps()[2]});
    }
}
