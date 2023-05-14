package io.xeros.model.entity.player.packets.objectoptions;

import io.xeros.Server;
import io.xeros.content.dialogue.impl.OutlastLeaderboard;
import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.content.skills.hunter.birdhouse.Birdhouses;
import io.xeros.content.skills.hunter.birdhouse.PlayerBirdHouseData;
import io.xeros.content.tradingpost.Listing;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.world.objects.GlobalObject;

/*
 * @author Matt
 * Handles all 3rd options for objects.
 */

public class ObjectOptionThree {

	public static void handleOption(final Player c, int objectType, int obX, int obY) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		c.clickObjectType = 0;
		// c.sendMessage("Object type: " + objectType);

		GlobalObject object = new GlobalObject(objectType, obX, obY, c.heightLevel);

		if (c.getRights().isOrInherits(Right.OWNER) && c.debugMessage)
			c.sendMessage("Clicked Object Option 3:  "+objectType+"");

		if (OutlastLeaderboard.handleInteraction(c, objectType, 3))
			return;

		switch (objectType) {
			case 30555:
			case 30558:
			case 30561:
			case 30564:
			case 31829:
			case 31832:
			case 31835:
			case 31838:
			case 31841:

			case 30554:
			case 30557:
			case 30560:
			case 30563:
			case 31828:
			case 31831:
			case 31834:
			case 31837:
			case 31840:
				Birdhouses.dismantle(c, object);
				break;

			case 29320:
				ObjectOptionOne.take(c, 10);
				break;
		case 31858:
		case 29150:
			c.sendMessage("You switch to the lunar spellbook.");
			c.setSidebarInterface(6, 29999);
			c.playerMagicBook = 2;
			break;

		case 29777:
		case 29734:
		case 10777:
		case 29879:
			c.objectDistance = 4;

			break;
		case 2884:
		case 16684:
		case 16683:
			if (c.absY == 3494 || c.absY == 3495 || c.absY == 3496) {
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", c.getX(), c.getY(), c.getHeight() - 1, 2);
			}
			break;
		case 29333:
			if (c.getMode().isIronmanType()) {
				c.sendMessage("@red@You are not permitted to make use of this.");			}
			Listing.collectMoney(c);
			
			break;
		case 6448:
			if (c.getMode().isIronmanType()) {
				Listing.openPost(c, false);	
			}
			c.sendMessage("@red@You cannot enter the trading post on this mode.");
			break;
		case 8356://streexerics
			c.getPA().movePlayer(1311, 3614, 0);
			break;
		case 7811:
			if (!c.getPosition().inClanWarsSafe()) {
				return;
			}
			c.getDH().sendDialogues(818, 6773);
			break;
		}
	}

}
