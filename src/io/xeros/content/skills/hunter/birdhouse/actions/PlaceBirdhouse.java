package io.xeros.content.skills.hunter.birdhouse.actions;

import io.xeros.content.skills.hunter.birdhouse.BirdhouseData;
import io.xeros.content.skills.hunter.birdhouse.PlayerBirdHouseData;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.action.Action;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.save.PlayerSave;
import io.xeros.model.world.objects.GlobalObject;

public class PlaceBirdhouse extends Action {

    private BirdhouseData birdhouseData;
    private GlobalObject gameObject;

    public PlaceBirdhouse(Entity entity, BirdhouseData birdhouseData, GlobalObject gameObject) {
        super(entity);
        this.birdhouseData = birdhouseData;
        this.gameObject = gameObject;
    }

    @Override
    public void tick() {
        Player player = (Player)entity;
        player.getItems().deleteItem(birdhouseData.birdHouseId, 1);
        player.birdHouseData.add(new PlayerBirdHouseData(birdhouseData, gameObject.getObjectId(), gameObject.getPosition(), gameObject.getFace(), gameObject.getType()));
        player.getPA().object(new GlobalObject(birdhouseData.objectData[0], gameObject.getPosition(), gameObject.getFace(), gameObject.getType()));
        PlayerSave.saveGame(player);
        player.setAction(null);
    }

}