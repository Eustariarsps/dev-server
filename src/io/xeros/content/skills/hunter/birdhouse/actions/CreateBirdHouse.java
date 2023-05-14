package io.xeros.content.skills.hunter.birdhouse.actions;

import io.xeros.content.skills.hunter.birdhouse.BirdhouseData;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.action.Action;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ImmutableItem;

public class CreateBirdHouse extends Action {

    private int tick;
    private BirdhouseData birdHouseData;

    public CreateBirdHouse(Entity entity, BirdhouseData birdHouseData) {
        super(entity);
        this.birdHouseData = birdHouseData;
    }

    @Override
    public void tick() {
        Player player = (Player)entity;
        if(tick == 1) {

            player.getItems().deleteItem(8792, 1);
            player.getItems().deleteItem(birdHouseData.logId, 1);
            player.getInventory().addToInventory(new ImmutableItem(birdHouseData.birdHouseId));

            player.getPA().addSkillXP(birdHouseData.craftingData[1], 12, true);

            player.setAction(null);
        }
        tick++;
    }
}