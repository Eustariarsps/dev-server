package io.xeros.content.wintertodt.actions;

import io.xeros.content.wintertodt.Wintertodt;
import io.xeros.content.wintertodt.WintertodtAction;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ImmutableItem;

public class FletchKindling extends WintertodtAction {

    int tick, amount;

    public FletchKindling(Player player, int amount) {
        super(player);
        this.amount = amount;
    }

    @Override
    public void tick() {
        Player player = (Player) entity;
        if(tick % 3 == 0) {
            player.startAnimation(1248);

            player.getItems().deleteItem(Wintertodt.BRUMA_ROOT, 1);
            player.getInventory().addToInventory(new ImmutableItem(Wintertodt.BRUMA_KINDLING));
            double xp = player.getPA().getLevelForXP(player.playerXP[9]) * 0.6;
            if(xp > 0) player.getPA().addSkillXP((int)xp, 9, true);

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