package io.xeros.content.wintertodt;

import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.skills.woodcutting.Hatchet;
import io.xeros.content.wintertodt.actions.*;
import io.xeros.model.SlottedItem;
import io.xeros.model.collisionmap.RegionProvider;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCDumbPathFinder;
import io.xeros.model.entity.player.PathFinder;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.model.items.ContainerUpdate;
import io.xeros.model.items.ImmutableItem;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;

import java.rmi.ServerError;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Wintertodt {

    private static final int DEFAULT_DELAY = 20;

    /**
     * Region of the wintertodt game
     */
    public static final int REGION_ID = 6462;
    private static final int REGION_X = 1630, REGION_Y = 4004;

    /**
     * Game data
     */
    private static boolean started = false;
    public static final int MAX_HP = 3500;
    public static int health = 0;
    public static int startDelay = DEFAULT_DELAY;
    private static int pyroSpeechDelay;
    public static List<Player> playersInRegion;

    /**
     * Item ids
     */
    public static final int BRUMA_ROOT = 20695;
    public static final int BRUMA_KINDLING = 20696;
    public static final int REJUV_POT_UNF = 20697;
    public static final int REJUV_POT_4 = 20699;
    public static final int REJUV_POT_3 = 20700;
    public static final int REJUV_POT_2 = 20701;
    public static final int REJUV_POT_1 = 20702;
    public static final int BRUMA_HERB = 20698;

    /**
     * Npc ids
     */
    private static final String[] PYROMANCER_DEAD_TEXT = {"My flame burns low.", "Mummy!", "I think I'm dying.", "We are doomed.", "Ugh, help me!"};

    public static final int PYROMANCER = 7371;
    public static final int INCAPACITATED_PYROMANCER = 7372;
    public static final int FLAME = 7373;

    /**
     * Game object ids
     */
    private static final int SNOW_EFFECT_ID = 26690;
    private static final int ACTIVE_STORM_ID = 29308;
    private static final int INACTIVE_STORM_ID = 29309;
    public static final int EMPTY_BRAZIER_ID = 29312;
    public static final int BROKEN_BRAZIER_ID = 29313;
    public static final int BURNING_BRAZIER_ID = 29314;
    private static GlobalObject WINTERTODT;

    /**
     * List of all minigame items
     */
    private static final List<Integer> GAME_ITEMS = Arrays.asList(BRUMA_ROOT, BRUMA_KINDLING, BRUMA_HERB, REJUV_POT_UNF, REJUV_POT_4, REJUV_POT_3, REJUV_POT_2, REJUV_POT_1);

    /**
     * List of all warm clothing
     */

    private static final List<Integer> WARM_CLOTHING = Arrays.asList(
            //Santa outfits
            1050, 12887, 12888, 12889, 12890, 12891, 12892, 12893, 12894, 12895, 12896, 13343, 13344,
            //Bunny outfit
            23448, 13663, 13664, 13665, 13182,
            //Clue hunter outfit
            19689, 19691, 19693, 19695, 19697,
            //Polar camo
            10065, 10066,
            //Wood camo
            10053, 10055,
            //Jungle camo
            10057, 10059,
            //Desert camo
            10061, 10063,
            //Larupia
            10045, 10043, 10041,
            //Graahk
            10051, 10049, 10047,
            //Kyatt
            10039, 10037, 10035,
            //Bomber
            9945, 9944,
            //Yakhide armour
            10822, 10824,
            //Pyromancer
            20708, 20706, 20704, 20710,
            //Chicken outfit
            11021, 11020, 11022, 11019,
            //Evil chicken
            20439, 20436, 20442, 20434,
            //Bearhead
            4502,
            //Fire tiara
            5537,
            //Lumberjack hat
            10941,
            //Firemaking hood
            9806,
            //Fire cape max hood
            13330,
            //Infernal max hood
            21282,
            //Scarfs
            6857, 6859, 6861, 6863, 9470, 21314,
            //Gloves of silence
            10075,
            //Fremennik gloves
            3799,
            //Warm gloves
            20712,
            //Firemaking cape
            9804, 9805,
            //Max cape
            13280, 13329, 21284, 21285, 13337, 20760, 21898, 24855, 21776, 21780, 21778, 13331, 13333, 13335,
            //Fire cape
            6570,
            //Obsidian cape
            6568, 20050,
            //Weapons
            1387, 1393, 3053, 11787, 11998, 1401, 3054, 11789, 12000, 13241, 13242, 13243, 13244, 21031, 21033, 12773, 20056, 20720,
            //Shields
            20714, 20716, 7053
            //TODO: Add the last few (https://i.imgur.com/Pif4I0l.png)
    );

    /**
     * Brazier data
     */
    public static final Brazier[] BRAZIERS = {
            new Brazier(RegionProvider.getGlobal().get(REGION_X, REGION_Y).getWorldObject(29312, 1620, 3997, 0).get(), new NPC(PYROMANCER, new Position(1619, 3996, 0)), 0, 0), // sw
            new Brazier(RegionProvider.getGlobal().get(REGION_X, REGION_Y).getWorldObject(29312, 1620, 4015, 0).get(), new NPC(PYROMANCER, new Position(1619, 4018, 0)), 0, 2), // nw
            new Brazier(RegionProvider.getGlobal().get(REGION_X, REGION_Y).getWorldObject(29312, 1638, 4015, 0).get(), new NPC(PYROMANCER, new Position(1641, 4018, 0)), 2, 2), // ne
            new Brazier(RegionProvider.getGlobal().get(REGION_X, REGION_Y).getWorldObject(29312, 1638, 3997, 0).get(), new NPC(PYROMANCER, new Position(1641, 3996, 0)), 2, 0) // se
    };

    public static void init() {
        pyroSpeechDelay = 8;
        startDelay = DEFAULT_DELAY;
        WINTERTODT = new GlobalObject(INACTIVE_STORM_ID, new Position(1627, 4004, 0), 0, 10);
        Server.getGlobalObjects().add(WINTERTODT);
        started = false;

        /**
         * Delay & interface update
         */

        CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.WITERTODT_LOBBY, new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer b) {
                if(startDelay > 0)
                    startDelay--;

                playersInRegion = PlayerHandler.getPlayersByRegion(REGION_ID);
                if(startDelay <= 0 && !started) start();

                Wintertodt.update();
            }

            @Override
            public void onStopped() {

            }
        }, 1);

        /**
         * Main game loop
         */
        CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.WINTERTODT_GAME_LOOP, new Object(), new CycleEvent() {

            @Override
            public void execute(CycleEventContainer b) {
                if(startDelay <= 0 && playersInRegion.size() != 0) {
                    if(playersInRegion.size() > 0) {
                        pyroSpeechDelay--;

                        applyColdDamage();
                        extinguishBraziers();
                        doMagicAttack();
                        attackPyromancers();
                        pyromancerText();
                        dealDamage();
                    }
                }
            }

            @Override
            public void onStopped() {

            }
        }, 2);
    }

    /**
     * Handles starting the wintertodt game
     */
    private static void start() {
        System.out.println("Start the wintertodt game!");
        WINTERTODT = new GlobalObject(ACTIVE_STORM_ID, new Position(1627, 4004, 0), 10, 0);
        Server.getGlobalObjects().add(WINTERTODT);

        for (Brazier brazier : BRAZIERS) {
            brazier.setObject(EMPTY_BRAZIER_ID);
            Server.getGlobalObjects().add(new GlobalObject(brazier.getObject().getId(), new Position(brazier.getObject().getX(), brazier.getObject().getY(), brazier.getObject().getHeight()), brazier.getObject().getFace(), brazier.getObject().getType()));
            if (!brazier.isPyromancerAlive())
                brazier.getPyromancer().requestTransform(PYROMANCER);
        }

        health = MAX_HP;
        started = true;
        update();
    }

    /**
     * Handles updating the interfaces for all the players
     */
    private static void update() {
        playersInRegion.forEach(Wintertodt::send);
    }

    /**
     * Handles sending the interface data
     */
    public static void send(Player player) {
        /*player.send(new SendString("Wintertodt's Energy: "+(health / (MAX_HP / 100))+"%", 750, 2));
        player.send(new SendString(isActive() ? "" : "The Wintertodt returns in: " + ((startDelay * 600) / 1000) + " secs", 750, 3));
        player.send(new SendString("Points\\n" + player.wintertodtPoints, 750, 5));

        //Sends the pyromancer config stuff
        player.send(new SendConfig(2224, BRAZIERS[0].isPyromancerAlive() ? 1 : 0));
        player.send(new SendConfig(2222, BRAZIERS[1].isPyromancerAlive() ? 1 : 0));
        player.send(new SendConfig(2223, BRAZIERS[2].isPyromancerAlive() ? 1 : 0));
        player.send(new SendConfig(2225, BRAZIERS[3].isPyromancerAlive() ? 1 : 0));
        //Sends the brazier config stuff
        player.send(new SendConfig(2228, BRAZIERS[0].getBrazierState()));
        player.send(new SendConfig(2226, BRAZIERS[1].getBrazierState()));
        player.send(new SendConfig(2227, BRAZIERS[2].getBrazierState()));
        player.send(new SendConfig(2229, BRAZIERS[3].getBrazierState()));*/

    }

    /**
     * Handles applying all the damage to the players
     */
    private static void applyColdDamage() {
        playersInRegion.forEach(player -> {
            if(player.getPosition().getY() <= 3987 || Misc.random(25) != 0) return;
            player.appendDamage(getColdDamage(player), Hitmark.HIT);
            player.sendMessage("The cold of the Wintertodt seeps into your bones.");
            if(player.action instanceof WintertodtAction)
                player.setAction(null);
        });
    }

    /**
     * The amount of damagae the player gets from cold
     */
    public static int getColdDamage(Player player) {
        return (int) ((16.0 - getWarmItemsWorn(player) - (2 * getBraziersLit())) * (player.getPA().getLevelForXP(player.playerXP[3]) + 1) / player.getPA().getLevelForXP(player.playerXP[11]));
    }

    /**
     * How much warm clothing the player is wearing
     */
    public static int getWarmItemsWorn(Player player) {
        int warmClothing = 0;
        for(int id : WARM_CLOTHING) {
            for(SlottedItem slottedItem : player.getItems().getEquipmentItems()) {
                if(slottedItem.getId() == id) {
                    warmClothing++;
                    break;
                }
            }
            if(warmClothing >= 4) break;
        }
        return warmClothing;
    }

    /**
     * How many braziers are currently lit
     */
    public static int getBraziersLit() {
        int count = 0;
        for (Brazier b : BRAZIERS)
            if (b.getObject().getId() == BURNING_BRAZIER_ID)
                count++;
        return Math.min(count, 3);
    }

    /**
     * Handles extinguishing braziers
     */
    private static void extinguishBraziers() {
        for (Brazier brazier : BRAZIERS) {
            int roll = Misc.random(Misc.random(health + 1500) / 100);
            if(brazier.getObject().getId() == BURNING_BRAZIER_ID && roll == 10) {
                if(brazier.hasSnowStorm()) continue;

                if(Misc.random(health < (MAX_HP / 2) ? 2 : 3) == 1)
                    breakBrazier(brazier);
                else {
                    //Graphic (502, 115, 0)
                    brazier.setObject(EMPTY_BRAZIER_ID);
                    Server.getGlobalObjects().add(new GlobalObject(brazier.getObject().getId(), new Position(brazier.getObject().getX(), brazier.getObject().getY(), brazier.getObject().getHeight()), brazier.getObject().getFace(), brazier.getObject().getType()));
                }
            }
        }
    }

    /**
     * Handles breaking the brazier
     */
    private static void breakBrazier(Brazier brazier) {

        List<GlobalObject> objects = Arrays.asList(
                new GlobalObject(SNOW_EFFECT_ID, new Position(brazier.getObject().getPosition().getX() + 1, brazier.getObject().getPosition().getY(), 0), 0, 10),
                new GlobalObject(SNOW_EFFECT_ID, new Position(brazier.getObject().getPosition().getX(), brazier.getObject().getPosition().getY() + 1, 0), 0, 10),
                new GlobalObject(SNOW_EFFECT_ID, new Position(brazier.getObject().getPosition().getX() + 1, brazier.getObject().getPosition().getY() + 1, 0), 0, 10),
                new GlobalObject(SNOW_EFFECT_ID, new Position(brazier.getObject().getPosition().getX() + 2, brazier.getObject().getPosition().getY() + 1, 0), 0, 10),
                new GlobalObject(SNOW_EFFECT_ID, new Position(brazier.getObject().getPosition().getX() + 1, brazier.getObject().getPosition().getY() + 2, 0), 0, 10)
        );

        for(GlobalObject gameObject : objects)
            Server.getGlobalObjects().add(gameObject);

        brazier.setSnowStorm(true);

        CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.WINTERTODT_BREAK_BRAZIER, new Object(), new CycleEvent() {

            @Override
            public void execute(CycleEventContainer b) {
                for(GlobalObject gameObject : objects)
                    Server.getGlobalObjects().remove(gameObject);

                brazier.setSnowStorm(false);

                if(isActive()) {
                    //Graphio (502, 90, 0)
                    brazier.setObject(BROKEN_BRAZIER_ID);
                    Server.getGlobalObjects().add(new GlobalObject(brazier.getObject().getId(), new Position(brazier.getObject().getX(), brazier.getObject().getY(), brazier.getObject().getHeight()), brazier.getObject().getFace(), brazier.getObject().getType()));
                    playersInRegion.forEach(player -> {
                        if(Misc.goodDistance(brazier.getObject().getPosition().getX() + 1, brazier.getObject().getPosition().getY() + 1, player.getPosition().getX(), player.getPosition().getY(), 2)) {
                            player.sendMessage("The brazier is broken and shrapnel damages you.");
                            player.appendDamage(Misc.random(getBrazierAttackDamage(player)), Hitmark.HIT);
                        }
                    });
                }
                b.stop();
            }

            @Override
            public void onStopped() {

            }
        }, 4);
    }

    /**
     * The amount of brazier damage the player takes when it breaks
     */
    public static int getBrazierAttackDamage(Player player) {
        return (int) ((10.0 - getWarmItemsWorn(player)) * (player.getPA().getLevelForXP(player.playerXP[3]) + 1) / player.getPA().getLevelForXP(player.playerXP[11])) * 2;
    }

    public static int getAreaAttackDamage(Player player) {
        return (int) ((10.0 - getWarmItemsWorn(player)) * (player.getPA().getLevelForXP(player.playerXP[3]) + 1) / player.getPA().getLevelForXP(player.playerXP[11]) * 3);
    }

    /**
     * Handles doing the magic attack within wintertodt
     */
    private static void doMagicAttack() {
        if(Misc.random(25) != 1) return;

        Player player = playersInRegion.get(Misc.random(playersInRegion.size() - 1));
        if (player.getPosition().getY() <= 3987) return;

        int baseX = player.getPosition().getX();
        int baseY = player.getPosition().getY();
        List<GlobalObject> snowAttacks = new ArrayList<>();

        snowAttacks.add(new GlobalObject(SNOW_EFFECT_ID, new Position(baseX, baseY, 0), 0, 10));
        if(!Server.getGlobalObjects().anyExists(baseX + 1, baseY + 1, 0))
            snowAttacks.add(new GlobalObject(SNOW_EFFECT_ID, new Position(baseX + 1, baseY + 1, 0), 0, 10));
        if(!Server.getGlobalObjects().anyExists(baseX + 1, baseY - 1, 0))
            snowAttacks.add(new GlobalObject(SNOW_EFFECT_ID, new Position(baseX + 1, baseY - 1, 0), 0, 10));
        if(!Server.getGlobalObjects().anyExists(baseX - 1, baseY + 1, 0))
            snowAttacks.add(new GlobalObject(SNOW_EFFECT_ID, new Position(baseX - 1, baseY + 1, 0), 0, 10));
        if(!Server.getGlobalObjects().anyExists(baseX - 1, baseY - 1, 0))
            snowAttacks.add(new GlobalObject(SNOW_EFFECT_ID, new Position(baseX - 1, baseY - 1, 0), 0, 10));

        for(GlobalObject gameObject : snowAttacks)
            Server.getGlobalObjects().add(gameObject);

        CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.WINTERTODT_MAGIC_ATTACK, new Object(), new CycleEvent() {
            int index = 0;
            @Override
            public void execute(CycleEventContainer b) {
                for(GlobalObject gameObject : snowAttacks) {
                    Server.getGlobalObjects().add(new GlobalObject(index == 0 ? 29325 : 29324, gameObject.getPosition(), 0, 10));
                    index++;
                }
                playersInRegion.forEach(players -> {
                    if(Misc.goodDistance(baseX, baseY, players.getPosition().getX(), player.getPosition().getY(), 1)) {
                        players.sendMessage("The freezing cold attack of the Wintertodt's magic hits you.");
                        players.appendDamage(1 + (getAreaAttackDamage(players) - 1), Hitmark.HIT);
                    }
                });
                b.stop();
            }

            @Override
            public void onStopped() {

            }
        }, 4);

        CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.WINTERTODT_MAGIC_ATTACK_CLEAR, new Object(), new CycleEvent() {

            @Override
            public void execute(CycleEventContainer b) {
                for(GlobalObject gameObject : snowAttacks)
                    Server.getGlobalObjects().remove(gameObject);
                b.stop();
            }

            @Override
            public void onStopped() {

            }
        }, 14);
    }

    /**
     * Handles attacking the pyromancers
     */
    private static void attackPyromancers() {
        List<NPC> pyros = Arrays.stream(BRAZIERS).filter(Brazier::isPyromancerAlive).map(Brazier::getPyromancer).collect(Collectors.toList());
        if (!pyros.isEmpty() && Misc.random(pyros.size() * 3) == 0) {
            NPC pyro = pyros.get(Misc.random(pyros.size() - 1));
            if(pyro.getHealth().getCurrentHealth() <= 0) return;

            if(pyro.pyroSnowAttack) {
                System.out.println("already a snow attack going for this pyro...");
                return;
            }

            pyro.pyroSnowAttack = true;
            GlobalObject snow = new GlobalObject(SNOW_EFFECT_ID, pyro.getPosition(), 0, 10);
            Server.getGlobalObjects().add(snow);

            CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.WINTERTODT_ATTACK_PYROMANCER, new Object(), new CycleEvent() {

                @Override
                public void execute(CycleEventContainer b) {
                    int damage = 6 + Misc.random(4);
                    if(damage > pyro.getHealth().getCurrentHealth())
                        damage = pyro.getHealth().getCurrentHealth();

                    pyro.appendDamage(damage, Hitmark.HIT);

                    if(pyro.getHealth().getCurrentHealth() <= 0)
                        pyro.requestTransform(INCAPACITATED_PYROMANCER);
                    pyro.pyroSnowAttack = false;
                    Server.getGlobalObjects().remove(snow);
                    b.stop();
                }

                @Override
                public void onStopped() {

                }
            }, 4);
        }
    }

    /**
     * Handles the random speech of the pyromancers linked to a brazier
     */
    private static void pyromancerText() {
        if(pyroSpeechDelay <= 0) {
            pyroSpeechDelay = 8;
            for (Brazier brazier : BRAZIERS) {
                if (!brazier.isPyromancerAlive())
                    brazier.getPyromancer().forceChat(PYROMANCER_DEAD_TEXT[Misc.random(PYROMANCER_DEAD_TEXT.length - 1)]);
                else if (brazier.getObject().getId() == EMPTY_BRAZIER_ID)
                    brazier.getPyromancer().forceChat("Light this brazier!");
                else if (brazier.getObject().getId() == BROKEN_BRAZIER_ID)
                    brazier.getPyromancer().forceChat("Fix this brazier!");
                else if (Misc.random(4) == 1)
                    brazier.getPyromancer().forceChat("Yemalo shi cardito!");

                if (brazier.isPyromancerAlive() && brazier.getObject().getId() == BURNING_BRAZIER_ID && Misc.random(3) == 1)
                    brazier.getPyromancer().startAnimation(4432);
            }
        }
    }

    /**
     * Handles dealing damage
     */
    private static void dealDamage() {
        int damage = 0;
        for (Brazier brazier : BRAZIERS) {
            if (brazier.isPyromancerAlive() && brazier.getObject().getId() == BURNING_BRAZIER_ID) {
                shootFlame(brazier);
                damage += 5;
            }
        }

        if (damage > 0) {
            health -= damage;
            if (health <= 0) {
                health = 0;
                death();
            }
        } else
            health = Math.min(MAX_HP, health + 5);
    }

    /**
     * Handles shooting the flame representing damage
     * @param brazier
     */
    public static void shootFlame(Brazier brazier) {
        NPC flame = new NPC(FLAME, new Position(brazier.getObject().getPosition().getX() + brazier.getFlameOffsetX(), brazier.getObject().getPosition().getY() + brazier.getFlameOffsetY(), brazier.getObject().getPosition().getHeight()));
        flame.getBehaviour().setWalkHome(false);
        flame.setAction(new FlameWalk(flame));
    }

    /**
     * Handles the death of the boss
     */
    private static void death() {
        started = false;
        startDelay = DEFAULT_DELAY;

        WINTERTODT = new GlobalObject(INACTIVE_STORM_ID, new Position(1627, 4004, 0), 0, 10);
        Server.getGlobalObjects().add(WINTERTODT);

        for (Brazier brazier : BRAZIERS) {
            brazier.getPyromancer().forcedText = "We can rest for a time.";
            brazier.getPyromancer().requestTransform(PYROMANCER);
            brazier.getPyromancer().getHealth().setCurrentHealth(brazier.getPyromancer().getHealth().getMaximumHealth());
            brazier.getPyromancer().startAnimation(65535);
            brazier.setObject(EMPTY_BRAZIER_ID);
            Server.getGlobalObjects().add(new GlobalObject(brazier.getObject().getId(), new Position(brazier.getObject().getX(), brazier.getObject().getY(), brazier.getObject().getHeight()), brazier.getObject().getFace(), brazier.getObject().getType()));

        }
        playersInRegion.forEach(Wintertodt::award);
    }

    /**
     * Handles awarding the player
     * @param player
     */
    private static void award(Player player) {
        removeGameItems(player);

        player.getPA().addSkillXP(player.getPA().getLevelForXP(player.playerXP[11]) * 100, 11, true);

        if (player.wintertodtPoints >= 500) {
            int crates = player.wintertodtPoints / 500;
            if (crates > 1)
                player.sendMessage("You have gained " + crates + " supply crates!");
            else
                player.sendMessage("You have gained a supply crate!");
            player.getInventory().addToInventory(new ImmutableItem(20703, crates));
        } else player.sendMessage("You did not earn enough points to be worthy of a gift from the citizens of Kourend this time.");

        player.wintertodtPoints = 0;
    }

    /**
     * Handles removing wintertodt items
     * @param player
     */
    public static void removeGameItems(Player player) {
        int slot = 0;
        for (var item : player.getItems().getInventoryItems()) {
            if (GAME_ITEMS.contains(item.getId())) {
                player.playerItems[slot] = 0;
                player.playerItemsN[slot] = 0;
            }
            slot++;
        }
    }

    /**
     * Checks if the game is active
     */
    public static boolean isActive() {
        return startDelay <= 0;
    }

    /**
     * Handles adding wintertodt points to a player
     */
    public static void addPoints(Player player, int amount) {
        amount *= 100;
        int old = player.wintertodtPoints;
        player.wintertodtPoints += amount;
        player.sendMessage("wintertodtPoints: " + player.wintertodtPoints);
        if (old < 500 && player.wintertodtPoints >= 500) {
            player.sendMessage("You have helped enough to earn a supply crate. Further work will go towards better rewards.");
        }
        send(player);
    }

    /**
     * Handles chopping the root
     */
    public static void chopRoot(Player player) {
        if(!isActive()) {
            player.sendMessage("There's no need to do that at this time.");
            return;
        }

        Hatchet axeData = Hatchet.getBest(player);

        if(axeData == null) {
            player.sendMessage("You do not have an axe which you have the woodcutting level to use.");
            return;
        }

        if(player.getInventory().freeInventorySlots() <= 0) {
            player.sendMessage("You have no space for that.");
            return;
        }

        player.setAction(new ChopRoots(player));
    }

    /**
     * Handles taking a bruma herb
     */
    public static void takeHerb(Player player) {
        if(!isActive()) {
            player.sendMessage("There's no need to do that at this time.");
            return;
        }

        if(player.getInventory().freeInventorySlots() <= 0) {
            player.sendMessage("You have no space for that.");
            return;
        }

        player.startAnimation(2282);
        player.setAction(new PickHerb(player));
    }

    /**
     * Handles lighting a brazier
     */
    public static void lightBrazier(Player player, GlobalObject gameObject) {
        if(!isActive()) {
            player.sendMessage("There's no need to do that at this time.");
            return;
        }

        Brazier brazier = null;

        for(Brazier b : BRAZIERS) {
            if(b.getObject().getId() == gameObject.getObjectId() && b.getObject().getPosition().getX() == gameObject.getPosition().getX() && b.getObject().getPosition().getY() == gameObject.getPosition().getY() && b.getObject().getPosition().getHeight() == gameObject.getPosition().getHeight()) {
                brazier = b;
                break;
            }
        }

        if(brazier == null) {
            System.out.println("Brazier has not been found...");
            return;
        }

        if(!brazier.isPyromancerAlive()) {
            player.sendMessage("Heal the Pyromancer before lighting the brazier.");
            return;
        }

        if(!player.getInventory().containsAll(new ImmutableItem(590)) && player.playerEquipment[3] != 20720) {
            player.sendMessage("You need a tinderbox or Bruma Torch to light that brazier.");
            return;
        }

        player.startAnimation(733);
        player.setAction(new LightBrazier(player, brazier));
    }

    /**
     * Handles feeding the brazier
     */
    public static void feedBrazier(Player player, GlobalObject gameObject) {
        if(!isActive()) {
            player.sendMessage("There's no need to do that at this time.");
            return;
        }

        Brazier brazier = null;

        for(Brazier b : BRAZIERS) {
            if(b.getObject().getId() == gameObject.getObjectId() && b.getObject().getPosition().getX() == gameObject.getPosition().getX() && b.getObject().getPosition().getY() == gameObject.getPosition().getY() && b.getObject().getPosition().getHeight() == gameObject.getPosition().getHeight()) {
                brazier = b;
                break;
            }
        }

        if(brazier == null) {
            System.out.println("Brazier has not been found...");
            return;
        }

        player.startAnimation(832);
        player.setAction(new FeedBrazier(player, brazier));
    }

    /**
     * Handles fixing the brazier
     */
    public static void fixBrazier(Player player, GlobalObject gameObject) {
        if(!isActive()) {
            player.sendMessage("There's no need to do that at this time.");
            return;
        }

        Brazier brazier = null;

        for(Brazier b : BRAZIERS) {
            if(b.getObject().getId() == gameObject.getObjectId() && b.getObject().getPosition().getX() == gameObject.getPosition().getX() && b.getObject().getPosition().getY() == gameObject.getPosition().getY() && b.getObject().getPosition().getHeight() == gameObject.getPosition().getHeight()) {
                brazier = b;
                break;
            }
        }

        if(brazier == null) {
            System.out.println("Brazier has not been found...");
            return;
        }

        if(!brazier.isPyromancerAlive()) {
            player.sendMessage("Heal the Pyromancer before fixing the brazier.");
            return;
        }

        if(!player.getInventory().containsAll(new ImmutableItem(2347))) {
            player.sendMessage("You need a hammer to fix this brazier.");
            return;
        }

        player.startAnimation(3676);
        player.setAction(new FixBrazier(player, brazier));
    }

    /**
     * Handles fetching the kindling
     */
    public static void fletch(Player player) {
        if(!isActive()) {
            player.sendMessage("There's no need to do that at this time.");
            return;
        }

        if(!player.getInventory().containsAll(new ImmutableItem(BRUMA_ROOT))) return;

        player.startAnimation(1248);
        player.setAction(new FletchKindling(player, player.getItems().getItemAmount(BRUMA_ROOT)));
    }

    /**
     * Handles mixing the potion
     */
    public static void mixHerb(Player player) {
        if(!isActive()) {
            player.sendMessage("There's no need to do that at this time.");
            return;
        }

        int herbs = player.getItems().getItemAmount(BRUMA_HERB);
        int pots = player.getItems().getItemAmount(REJUV_POT_UNF);
        int min = Misc.min(herbs, pots);

        if(min == 0) return;

        player.startAnimation(363);
        player.setAction(new MixHerb(player, min));
    }

    /**
     * Handles healing the pyromancer
     */
    public static void healPyromancer(Player player, NPC npc, int slot) {
        if(!isActive()) {
            player.sendMessage("There's no need to do that at this time.");
            return;
        }

        Brazier brazier = null;

        for(Brazier b : BRAZIERS) {
            if(b.getPyromancer().getPosition().getX() == npc.getPosition().getX() && b.getPyromancer().getPosition().getY() == npc.getPosition().getY() && b.getPyromancer().getPosition().getHeight() == npc.getPosition().getHeight() && b.getPyromancer().getNpcId() == npc.getNpcId()) {
                brazier = b;
                break;
            }
        }

        if(brazier == null) {
            System.out.println("Brazier has not been found...");
            return;
        }

        int itemUsed = player.playerItems[slot];

        if(itemUsed != REJUV_POT_4 && itemUsed != REJUV_POT_3 && itemUsed != REJUV_POT_2 && itemUsed != REJUV_POT_1) {
            System.out.println("not a rejuv potion");
            return;
        }

        int newPotion = itemUsed + 1;
        if(newPotion > REJUV_POT_1) newPotion = 0;

        player.playerItems[slot] = newPotion;
        if(newPotion <= 0)
            player.playerItemsN[slot] = 0;

        brazier.getPyromancer().requestTransform(PYROMANCER);
        brazier.getPyromancer().getHealth().setCurrentHealth(brazier.getPyromancer().getHealth().getMaximumHealth());
        player.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        addPoints(player, 30);
    }

}
