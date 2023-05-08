package io.xeros.content.wintertodt.actions;

import io.xeros.content.wintertodt.Brazier;
import io.xeros.content.wintertodt.Wintertodt;
import io.xeros.content.wintertodt.WintertodtAction;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ImmutableItem;

public class FeedBrazier extends WintertodtAction {

    Brazier brazier;
    int tick;

    public FeedBrazier(Player player, Brazier brazier) {
        super(player);
        this.brazier = brazier;
    }

    @Override
    public void tick() {
        Player player = (Player) entity;
        if (brazier.getObject().getId() != Wintertodt.BURNING_BRAZIER_ID) {
            player.sendMessage("The brazier has gone out.");
            player.startAnimation(65535);
            player.setAction(null);
            return;
        }

        if(!player.getInventory().containsAll(new ImmutableItem(Wintertodt.BRUMA_ROOT)) && !player.getInventory().containsAll(new ImmutableItem(Wintertodt.BRUMA_KINDLING))) {
            player.startAnimation(65535);
            player.setAction(null);
            return;
        }

        if(tick % 2 == 0) {
            player.startAnimation(832);
            if (player.getInventory().containsAll(new ImmutableItem(Wintertodt.BRUMA_KINDLING))) {
                player.getItems().deleteItem(Wintertodt.BRUMA_KINDLING, 1);
                double xp = player.getPA().getLevelForXP(player.playerXP[11]) * 3.8;
                if(xp > 0) player.getPA().addSkillXP((int)xp, 11, true);
                Wintertodt.addPoints(player, 25);
            } else {
                player.getItems().deleteItem(Wintertodt.BRUMA_ROOT, 1);
                double xp = player.getPA().getLevelForXP(player.playerXP[11]) * 3;
                if(xp > 0) player.getPA().addSkillXP((int)xp, 11, true);
                Wintertodt.addPoints(player, 10);
            }
        }

        tick++;
    }

}
