package io.xeros.content.skills.hunter.birdhouse.actions;

import io.xeros.content.skills.hunter.birdhouse.PlayerBirdHouseData;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.action.Action;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.save.PlayerSave;
import io.xeros.model.items.ImmutableItem;
import io.xeros.model.world.objects.GlobalObject;

public class FillBirdhouse extends Action {

    private PlayerBirdHouseData playerBirdHouseData;
    private ImmutableItem itemUsed;
    private int itemAmount;

    public FillBirdhouse(Entity entity, PlayerBirdHouseData playerBirdHouseData, ImmutableItem itemUsed, int itemAmount) {
        super(entity);
        this.playerBirdHouseData = playerBirdHouseData;
        this.itemUsed = itemUsed;
        this.itemAmount = itemAmount;
    }

    @Override
    public void tick() {
        Player player = (Player)entity;
        player.getItems().deleteItem(itemUsed.getId(), itemAmount);

        if(itemAmount >= 10 || playerBirdHouseData.seedAmount + itemAmount >= 10) {
            player.getDH().sendStatement("Your birdhouse trap is now full of seed and will start to catch birds");
            player.sendMessage("Your birdhouse trap is now full of seed and will start to catch birds");

            playerBirdHouseData.seedAmount += itemAmount;
            playerBirdHouseData.birdhouseTimer = System.currentTimeMillis() + 1_800_000;
            player.getPA().object(new GlobalObject(playerBirdHouseData.birdhouseData.objectData[playerBirdHouseData.seedAmount >= 10 ? 1 : 0], playerBirdHouseData.birdhousePosition, playerBirdHouseData.rotation, playerBirdHouseData.type));
        } else {
            ItemDef def = ItemDef.forId(itemUsed.getId());
            player.getDH().sendStatement("You add " + itemAmount + " x " + def.getName().toLowerCase() + " to the birdhouse.");
            player.sendMessage("You add " + itemAmount + " x " + def.getName().toLowerCase() + " to the birdhouse.");

            playerBirdHouseData.seedAmount += itemAmount;
        }

        PlayerSave.saveGame(player);
        player.setAction(null);
    }

}
