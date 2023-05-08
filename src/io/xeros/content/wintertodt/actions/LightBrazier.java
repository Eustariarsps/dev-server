package io.xeros.content.wintertodt.actions;

import io.xeros.Server;
import io.xeros.content.wintertodt.Brazier;
import io.xeros.content.wintertodt.Wintertodt;
import io.xeros.model.entity.action.Action;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.world.objects.GlobalObject;

public class LightBrazier extends Action {
    private int tick;
    private Brazier brazier;

    public LightBrazier(Player player, Brazier brazier) {
        super(player);
        this.brazier = brazier;
    }

    @Override
    public void tick() {
        Player player = (Player) entity;
        if(tick == 2) {
            if(brazier.getBrazierState() != 2) {
                brazier.setObject(Wintertodt.BURNING_BRAZIER_ID);
                Server.getGlobalObjects().add(new GlobalObject(brazier.getObject().getId(), new Position(brazier.getObject().getX(), brazier.getObject().getY(), brazier.getObject().getHeight()), brazier.getObject().getFace(), brazier.getObject().getType()));
            }
            double xp = player.getPA().getLevelForXP(player.playerXP[11]) * 6;
            if(xp > 0) player.getPA().addSkillXP((int)xp, 11, true);
            Wintertodt.addPoints(player, 25);
            player.startAnimation(65535);
            player.setAction(null);
        }
        tick++;
    }

}