package io.xeros.content.skills.hunter.birdhouse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.save.PlayerSave;
import io.xeros.model.items.ImmutableItem;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Birdhouses {

    static long SECOND = 1000,
            MINUTE = SECOND * 60,
            HOUR = MINUTE * 60,
            DAY = HOUR * 24;

    public static String getTimeLeft(PlayerBirdHouseData playerBirdHouseData) {
        long left = playerBirdHouseData.birdhouseTimer - System.currentTimeMillis();
        long minutes = left / MINUTE;
        return minutes < 0 ? "rougly 1 minute." : minutes + " minute(s).";
    }

    public static void receiveLoot(Player player, PlayerBirdHouseData birdHouseData) {
        player.getPA().closeAllWindows();

        if(player.getInventory().freeInventorySlots() < 2) {
            player.sendMessage("You must have two spaces in your inventory to dismantle the birdhouse.");
            return;
        }

        player.startAnimation(827);

        player.getInventory().addAnywhere(new ImmutableItem(8792), false);
        for(int index = 0; index < 10; index++)
            Server.itemHandler.createGroundItem(player, 9978, player.getX(), player.getY(), player.heightLevel, 1);
        int[] featerAmount = { 30, 40, 50, 60 };
        int featherAmount = featerAmount[Misc.random(featerAmount.length - 1)];

        player.getInventory().addAnywhere(new ImmutableItem(314, featherAmount), false);

        int nestsReceived = 0;

        player.getInventory().addAnywhere(new ImmutableItem(5073), false);

        if(wasSuccesful(player, 21, 0, 200)) {
            player.getInventory().addAnywhere(new ImmutableItem(5073), false);
            nestsReceived++;
        }

        for(int index = 0; index < 10; index++) {
            int hunterLevel = player.playerLevel[21];

            if(hunterLevel < 50)
                hunterLevel = 50;

            int nestRate = birdHouseData.birdhouseData.succesRates * (hunterLevel - 1) / 98;
            int randomRoll = Misc.random(1000);

            if(randomRoll < nestRate) {
                int nestChance = 100;
                if(player.getItems().getEquipmentItems().get(2) != null && player.getItems().getEquipmentItems().get(2).getId() == 10134)
                    nestChance = 95;
                int nestRoll = Misc.random(nestChance);

                if(nestRoll < 1) {
                    player.getInventory().addAnywhere(new ImmutableItem(5072), false);
                    nestsReceived++;
                }
                else if(nestRoll < 2) {
                    player.getInventory().addAnywhere(new ImmutableItem(5070), false);
                    nestsReceived++;
                } else if(nestRoll < 3) {
                    player.getInventory().addAnywhere(new ImmutableItem(5071), false);
                    nestsReceived++;
                } else if(nestRoll < 35) {
                    player.getInventory().addAnywhere(new ImmutableItem(5074), false);
                    nestsReceived++;
                } else {
                    player.getInventory().addAnywhere(new ImmutableItem(5075), false);
                    nestsReceived++;
                }
            }
        }

        player.sendMessage("You dismantle and discard the trap, retrieving "+nestsReceived+" nest"+(nestsReceived > 1 ? "s" : "")+", 10 dead birds, "+featherAmount+" feathers and "+birdHouseData.birdhouseData.hunterData[1]+" Hunter XP.");
        player.getPA().addSkillXP(birdHouseData.birdhouseData.hunterData[1], 21, true);

        player.getPA().object(new GlobalObject(birdHouseData.oldObjectId, birdHouseData.birdhousePosition, birdHouseData.rotation, birdHouseData.type));
        player.birdHouseData.remove(birdHouseData);
        PlayerSave.saveGame(player);
    }

    public static void checkSeeds(Player c, GlobalObject object) {
        boolean foundData = false;
        PlayerBirdHouseData birdHouseData = null;
        for (PlayerBirdHouseData playerBirdHouseData : c.birdHouseData) {
            if (playerBirdHouseData.birdhousePosition.equals(object.getPosition())) {
                birdHouseData = playerBirdHouseData;
                foundData = true;
                break;
            }
        }

        if (foundData) {

            System.out.println("System.currentTimeMillis(): " + System.currentTimeMillis());
            System.out.println("birdHouseData.birdhouseTimer: " + birdHouseData.birdhouseTimer);
            if(System.currentTimeMillis() > birdHouseData.birdhouseTimer && birdHouseData.birdhouseTimer != 0)
                c.getDH().sendStatement("Your birdhouse trap is ready to be collected.");
            else if(birdHouseData.seedAmount < 10)
                c.getDH().sendStatement("Your birdhouse seed level is: "+birdHouseData.seedAmount+"/10. <col=800000>It must be full of seed before it", "<col=800000>will start catching birds.");
            else
                c.getDH().sendStatement("Your birdhouse trap is set and consuming seed.", "Ready in " + Birdhouses.getTimeLeft(birdHouseData));
        }
        c.selectedObject = null;
    }

    public static void dismantle(Player c, GlobalObject object) {
        boolean foundData = false;
        PlayerBirdHouseData birdHouseData = null;
        for (PlayerBirdHouseData playerBirdHouseData : c.birdHouseData) {
            if (playerBirdHouseData.birdhousePosition.equals(object.getPosition())) {
                birdHouseData = playerBirdHouseData;
                foundData = true;
                break;
            }
        }

        if (foundData) {
            if(birdHouseData.seedAmount < 10 || birdHouseData.seedAmount >= 10 && birdHouseData.birdhouseTimer > System.currentTimeMillis()) {
                c.getDH().sendOption2("<col=800000>You will lose any seed in the birdhouse", "Yes - dismantle and <col=800000>discard the birdhouse and contents</col>.", "No, leave it be.");
                c.selectedBirdhouseData = birdHouseData;
                c.dialogueAction = 902130;
            } else {
                Birdhouses.receiveLoot(c, birdHouseData);
            }
        }
        c.selectedObject = null;
    }

    private static boolean wasSuccesful(Player player, int skill, int low, int high) {
        int level = player.playerLevel[skill];

        int odds = 1 + (low * (99 - level) / 98) + (high * (level - 1) / 98);
        double percent = ((double)odds / 256D) * 100D;
        int random = new Random().nextInt(256);

        return odds >= random;
    }

    public static void save(Player player) {
        try {
            File file = new File("save_files/public/character_saves/birdhouses/"+player.getLoginName()+".json");
            if(!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            FileWriter writer = new FileWriter(file);

            gson.toJson(player.birdHouseData, writer);

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load(Player player, String playerName) {
        try {
            File file = new File("save_files/public/character_saves/birdhouses/"+playerName+".json");
            if(file.exists() == false)
                return;

            FileReader reader = new FileReader(file);

            player.birdHouseData = new Gson().fromJson(reader, new TypeToken<List<PlayerBirdHouseData>>(){}.getType());

            reader.close();
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }
    }
}
