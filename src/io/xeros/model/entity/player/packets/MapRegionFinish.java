package io.xeros.model.entity.player.packets;

import io.xeros.Server;
import io.xeros.content.skills.hunter.birdhouse.PlayerBirdHouseData;
import io.xeros.model.entity.player.PacketType;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;

public class MapRegionFinish implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {

		for(PlayerBirdHouseData playerBirdHouseData : c.birdHouseData) {
			if(Misc.goodDistance(playerBirdHouseData.birdhousePosition.getX(), playerBirdHouseData.birdhousePosition.getY(), c.getPosition().getX(), c.getPosition().getY(), 60)) {
				int objectId = playerBirdHouseData.birdhouseData.objectData[playerBirdHouseData.seedAmount >= 10 ? 1 : 0];
				c.getPA().object(new GlobalObject(objectId, playerBirdHouseData.birdhousePosition, playerBirdHouseData.rotation, playerBirdHouseData.type));
			}
		}

		Server.itemHandler.reloadItems(c);
		Server.getGlobalObjects().updateRegionObjects(c);
		if (c.getPA().viewingOtherBank) {
			c.getPA().resetOtherBank();
		}

		if (c.skullTimer > 0) {
			c.isSkulled = true;
			c.headIconPk = 0;
			c.getPA().requestUpdates();
		}
	}

}
