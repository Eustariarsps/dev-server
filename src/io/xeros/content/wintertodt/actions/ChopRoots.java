package io.xeros.content.wintertodt.actions;

import io.xeros.content.skills.woodcutting.Hatchet;
import io.xeros.content.wintertodt.Wintertodt;
import io.xeros.model.entity.action.Action;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ImmutableItem;

public class ChopRoots extends Action {

    int tick;

    public ChopRoots(Player player) {
        super(player);
    }

    @Override
    public void tick() {
        Player player = (Player) entity;
        Hatchet axeData = Hatchet.getBest(player);

        if (axeData == null) {
            player.sendMessage("You need an axe to chop this tree.");
            player.startAnimation(65535);
            player.setAction(null);
            return;
        }

        if(player.getInventory().freeInventorySlots() <= 0) {
            player.sendMessage("You have no space for that.");
            player.startAnimation(65535);
            player.setAction(null);
            return;
        }

        player.startAnimation(axeData.getAnimation());

        if(tick % 3 == 0) {
            player.getInventory().addToInventory(new ImmutableItem(Wintertodt.BRUMA_ROOT));
            player.sendMessage("You get a bruma root.");

            double xp = player.getPA().getLevelForXP(player.playerXP[13]) * 0.3;
            if(xp > 0) player.getPA().addSkillXP((int)xp, 13, true);
        }
        tick++;
    }
}