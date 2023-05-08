package io.xeros.content.wintertodt;

import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.world.objects.GlobalObject;

public class Brazier {

    private WorldObject object;
    private NPC pyromancer;
    private boolean snowStorm;
    private int flameOffsetX, flameOffsetY;

    public Brazier(WorldObject object, NPC pyromancer, int flameOffsetX, int flameOffsetY) {
        this.object = object;
        this.pyromancer = pyromancer;
        this.flameOffsetX = flameOffsetX;
        this.flameOffsetY = flameOffsetY;
    }

    public WorldObject getObject() {
        return object;
    }

    public void setObject(int objectId) {
        this.object = new WorldObject(objectId, this.object.getX(), this.object.getY(), this.object.getHeight(), this.object.getType(), this.object.getFace());
    }

    public NPC getPyromancer() {
        return pyromancer;
    }

    public boolean isPyromancerAlive() {
        return pyromancer.getNpcId() == Wintertodt.PYROMANCER;
    }

    public int getFlameOffsetX() {
        return flameOffsetX;
    }

    public int getFlameOffsetY() {
        return flameOffsetY;
    }

    public boolean hasSnowStorm() {
        return snowStorm;
    }

    public void setSnowStorm(boolean snowStorm) {
        this.snowStorm = snowStorm;
    }

    public int getBrazierState() {
        return object.getId() == Wintertodt.EMPTY_BRAZIER_ID ? 0 :
                (object.getId() == Wintertodt.BURNING_BRAZIER_ID ? 1 : 3);
    }

}