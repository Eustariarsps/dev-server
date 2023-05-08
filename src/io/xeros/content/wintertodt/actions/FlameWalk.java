package io.xeros.content.wintertodt.actions;

import io.xeros.content.commands.owner.Debug;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.action.Action;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Position;

public class FlameWalk extends Action {

    int tickDelay;

    public FlameWalk(Entity entity) {
        super(entity);
    }

    @Override
    public void tick() {
        NPC npc = (NPC) entity;
        if(!npc.getPosition().equals(new Position(1630, 4007)) && tickDelay == 0) {
            npc.moveTowards(1630, 4007, false, false);
            tickDelay = 1;
        } else if(npc.getPosition().equals(new Position(1630, 4007))) {
            npc.teleport(new Position(0, 0, 0));
            npc.setAction(null);
            npc.unregister();
        }
        if(tickDelay > 0)
            tickDelay--;
    }

}