package io.xeros.content.wintertodt.actions;

import io.xeros.content.wintertodt.Wintertodt;
import io.xeros.model.entity.action.Action;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ImmutableItem;

public class PickHerb extends Action {

    int tick;

    public PickHerb(Player player) {
        super(player);
    }

    @Override
    public void tick() {
        Player player = (Player) entity;
        if(tick % 2 == 0) {
            if(player.getInventory().freeInventorySlots() > 0) {
                player.sendMessage("You pick a bruma herb.");
                player.getInventory().addToInventory(new ImmutableItem(Wintertodt.BRUMA_HERB));
                double xp = player.getPA().getLevelForXP(player.playerXP[19]) * 0.1;
                if(xp > 0) player.getPA().addSkillXP((int)xp, 19, true);
                player.startAnimation(2282);
            } else {
                player.sendMessage("You have no space for that.");
                player.setAction(null);
            }
        }
        tick++;
    }

}
