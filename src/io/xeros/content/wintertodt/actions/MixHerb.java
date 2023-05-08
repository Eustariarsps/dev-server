package io.xeros.content.wintertodt.actions;

import io.xeros.content.wintertodt.Wintertodt;
import io.xeros.model.entity.action.Action;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ImmutableItem;

public class MixHerb extends Action {

    int tick, amount;

    public MixHerb(Player player, int amount) {
        super(player);
        this.amount = amount;
    }

    @Override
    public void tick() {
        Player player = (Player) entity;
        if(!player.getInventory().containsAll(new ImmutableItem(Wintertodt.BRUMA_HERB)) || !player.getInventory().containsAll(new ImmutableItem(Wintertodt.REJUV_POT_UNF))) {
            player.startAnimation(65535);
            player.setAction(null);
            return;
        }

        if(tick % 2 == 0) {

            player.sendMessage("You combine the bruma herb into the unfinished potion.");
            player.startAnimation(363);
            player.getItems().deleteItem(Wintertodt.BRUMA_HERB, 1);
            player.getItems().deleteItem(Wintertodt.REJUV_POT_UNF, 1);
            player.getInventory().addToInventory(new ImmutableItem(Wintertodt.REJUV_POT_4));
            double xp = player.getPA().getLevelForXP(player.playerXP[15]) * 0.1;
            if(xp > 0) player.getPA().addSkillXP((int)xp, 15, true);

            amount--;
            if(amount <= 0) {
                player.startAnimation(65535);
                player.setAction(null);
                return;
            }
        }

        tick++;
    }

}