package github.trukoaiu.botdetector.listeners;

import github.trukoaiu.botdetector.BotDetector;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ServerJoin implements Listener {
    private final BotDetector plugin;

    public ServerJoin(BotDetector plugin) {
        this.plugin = plugin;
    }

    //Quick access data for EventHandlers
    private final Map<UUID, Boolean> playerAnswers = new HashMap<>();
    private final Map<UUID, Integer> mathAnswer = new HashMap<>();
    private final Map<UUID, Location> playerStartLocation = new HashMap<>();
    private final Map<UUID, Boolean> wasFlying = new HashMap<>();
    private final Map<UUID, Boolean> wasInvulnerable = new HashMap<>();
    private final Map<UUID, Boolean> wasInvisible = new HashMap<>();
    private final Map<UUID, Boolean> wasBlind = new HashMap<>();
    private final Map<UUID, List<PotionEffect>> potionEffectList = new HashMap<>();
    private final Map<UUID, Integer> airAmount = new HashMap<>();


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID playerId = p.getUniqueId();

        //Creates random numbers for simple math questions
        Random randomCreator = new Random();
        Integer randomN1 = randomCreator.nextInt(5) + 1;
        Integer randomN2 = randomCreator.nextInt(5) + 1;
        Integer sumOfAddition = randomN2 + randomN1;

        List<PotionEffect> activePotionEffects = new ArrayList<>(p.getActivePotionEffects());
        Integer setRemainingAir = p.getRemainingAir();

        //Sets HashMaps
        putIfAbsent(playerAnswers, playerId, false);
        putIfAbsent(mathAnswer, playerId, sumOfAddition);
        putIfAbsent(playerStartLocation, playerId, p.getLocation());
        putIfAbsent(wasFlying, playerId, p.getAllowFlight());
        putIfAbsent(wasInvulnerable, playerId, p.isInvulnerable());
        putIfAbsent(wasInvisible, playerId, p.hasPotionEffect(PotionEffectType.INVISIBILITY));
        putIfAbsent(wasBlind, playerId, p.hasPotionEffect(PotionEffectType.BLINDNESS));
        putIfAbsent(potionEffectList, playerId, activePotionEffects);
        putIfAbsent(airAmount, playerId, setRemainingAir);

        //Main logic
        if (!playerAnswers.get(playerId)) {
            //Player security and effect after joining the server
            setEffects(p);

            Location playerDestination = plugin.getConfig().getLocation("teleport-on-join-cords");

            if (plugin.getConfig().getBoolean("teleport-on-join") && playerDestination != null) {
                p.teleport(playerDestination);
            }

            //Create answers for quiz from previous random numbers
            String finAnswer1 = "/answer " + sumOfAddition;
            Integer mathAnswer2 = randomCreator.nextInt(10) + 1;
            if (sumOfAddition.equals(mathAnswer2)) {
                mathAnswer2--;
            }
            String finAnswer2 = "/answer " + mathAnswer2;

            //Create random order in which the answers are named (A, B)
            Integer answerOrder = randomCreator.nextInt(2); //Will also be used in send order
            String firstSymbol = plugin.getConfig().getString("symbol-one");
            String secondSymbol = plugin.getConfig().getString("symbol-two");


            //TextComponents for interactive chat messages
            //Getting and setting hover text from config
            Text hoverText = new Text(plugin.getConfig().getString("hover-message"));

            //First Text Component
            //Getting and formatting text from config
            String textToFirstAnswer = plugin.getConfig().getString("answer");
            if (textToFirstAnswer != null && textToFirstAnswer.contains("%TheAnswer%")) {
                textToFirstAnswer = textToFirstAnswer.replace("%TheAnswer%", "" + sumOfAddition);
                if (textToFirstAnswer.contains("%Symbol%") && firstSymbol != null && secondSymbol != null) {
                    if (answerOrder == 0) {
                        textToFirstAnswer = textToFirstAnswer.replace("%Symbol%", firstSymbol);
                    } else {
                        textToFirstAnswer = textToFirstAnswer.replace("%Symbol%", secondSymbol);
                    }
                    textToFirstAnswer = ChatColor.translateAlternateColorCodes('&', textToFirstAnswer);
                }
            } else {
                plugin.getLogger().info("The answer option can not be empty, and must contain at least %TheAnswer%.");
            }

            //Setting text hover and implementing click and hover event
            TextComponent componentForAnswer1 = new TextComponent(textToFirstAnswer);
            componentForAnswer1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, finAnswer1));

            //Second TextComponent
            //Getting and formatting text from config
            String textToSecondAnswer = plugin.getConfig().getString("answer");
            if (textToSecondAnswer != null && textToSecondAnswer.contains("%TheAnswer%")) {
                textToSecondAnswer = textToSecondAnswer.replace("%TheAnswer%", "" + mathAnswer2);
                if (textToSecondAnswer.contains("%Symbol%") && firstSymbol != null && secondSymbol != null) {
                    if (answerOrder == 0) {
                        textToSecondAnswer = textToSecondAnswer.replace("%Symbol%", secondSymbol);
                    } else {
                        textToSecondAnswer = textToSecondAnswer.replace("%Symbol%", firstSymbol);
                    }
                    textToSecondAnswer = ChatColor.translateAlternateColorCodes('&', textToSecondAnswer);

                }
            }
            //Setting text hover and implementing click and hover event
            TextComponent componentForAnswer2 = new TextComponent(textToSecondAnswer);
            componentForAnswer2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, finAnswer2));

            //Adding hover event
            if (plugin.getConfig().getString("hover-message") != null) {
                componentForAnswer1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
                componentForAnswer2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
            }

            //Sends messages with small delay, because of potential quicker send than JoinMessage
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                String afterJoinMessage = plugin.getConfig().getString("after-join-message");
                if (afterJoinMessage != null) {
                    afterJoinMessage = afterJoinMessage.replace("%randomN1%", "" + randomN1);
                    afterJoinMessage = afterJoinMessage.replace("%randomN2%", "" + randomN2);
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', afterJoinMessage));
                }
                if (answerOrder > 0) {
                    p.spigot().sendMessage(componentForAnswer2);
                    p.spigot().sendMessage(componentForAnswer1);
                } else {
                    p.spigot().sendMessage(componentForAnswer1);
                    p.spigot().sendMessage(componentForAnswer2);
                }
            }, 3);
        }
    }

    @EventHandler
    public void onPlayerAnswer(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        UUID playerId = p.getUniqueId();
        Integer corAnswer = mathAnswer.get(playerId);
        String strAnswer = "" + corAnswer;
        String CorAnsMess = plugin.getConfig().getString("correct-answer-message");
        String kickMess = plugin.getConfig().getString("kick-message");
        String chatMess = plugin.getConfig().getString("chat-message");

        //Checks send answer and acts accordingly
        if (playerAnswers.containsKey(playerId) && !playerAnswers.get(playerId)) {
            String command = e.getMessage().substring(1);
            //Separates "/answer" from the important part
            if (command.startsWith("answer")) {
                String[] parts = command.split(" ");
                if (parts.length > 1) {
                    String answer = parts[1];
                    //Correct answer functionality
                    if (answer.equals(strAnswer)) {
                        if (CorAnsMess != null) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', CorAnsMess));
                        }
                        playerAnswers.put(playerId, true);
                        removeEffects(p);
                    } else {
                        //Incorrect answer functionality
                        p.teleport(playerStartLocation.get(playerId));
                        if (getConfig("set-invulnerable")) {
                            if (wasInvulnerable.containsKey(p.getUniqueId()) && !wasInvulnerable.get(p.getUniqueId())) {
                                p.setInvulnerable(false);
                            }
                        }
                        removeEffects(p);
                        if (kickMess != null) {
                            p.kickPlayer(ChatColor.translateAlternateColorCodes('&', kickMess));
                        }
                    }
                }
            } else {
                //Blocks the usage of commands, until answering the question
                e.setCancelled(true);
                if (chatMess != null) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', chatMess));
                }
            }
        }
    }

    public Boolean getConfig(String configName) {
        return plugin.getConfig().getBoolean(configName);
    }

    //Set effects for the player until they answer question
    public void setEffects(Player p) {
        if (getConfig("save-potion-effects")) {
            List<PotionEffect> activePotionEffects = potionEffectList.get(p.getUniqueId());
            for (PotionEffect effect : activePotionEffects) {
                p.removePotionEffect(effect.getType());
            }
        }
        if (getConfig("set-invulnerable")) {
            p.setInvulnerable(true);
        }
        if (getConfig("block-movement")) {
            if (wasFlying.containsKey(p.getUniqueId()) && !wasFlying.get(p.getUniqueId())) {
                p.setAllowFlight(true);
            }
        }
        if (getConfig("set-invisible")) {
            if (wasInvisible.containsKey(p.getUniqueId()) && !wasInvisible.get(p.getUniqueId())) {
                PotionEffect invisibilityPotion = new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 1, false, false);
                p.addPotionEffect(invisibilityPotion);
            }
        }
        if (getConfig("set-blind")) {
            if (wasBlind.containsKey(p.getUniqueId()) && !wasBlind.get(p.getUniqueId())) {
                PotionEffect blindnessPotion = new PotionEffect(PotionEffectType.BLINDNESS, 999999, 1, false, false);
                p.addPotionEffect(blindnessPotion);
            }
        }
    }

    //Removes effects after answering questions and possibly teleport
    public void removeEffects(Player p) {
        Boolean shouldTeleportBack = plugin.getConfig().getBoolean("teleport-back-on-answer");
        Integer invulnerabilityTime = plugin.getConfig().getInt("keep-invulnerable");
        Boolean teleportOnAnswer = plugin.getConfig().getBoolean("teleport-on-answer");
        Location teleportOnAnswerLoc = plugin.getConfig().getLocation("teleport-on-answer-cords");


        if (shouldTeleportBack) {
            p.teleport(playerStartLocation.get(p.getUniqueId()));
        }
        //If the player is teleported he gains slight invulnerability, so they won't die on instant while loading
        if (getConfig("set-invulnerable")) {
            if (shouldTeleportBack || teleportOnAnswer) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (wasInvulnerable.containsKey(p.getUniqueId()) && !wasInvulnerable.get(p.getUniqueId())) {
                        p.setInvulnerable(false);
                    }
                }, invulnerabilityTime);
            } else {
                if (wasInvulnerable.containsKey(p.getUniqueId()) && !wasInvulnerable.get(p.getUniqueId())) {
                    p.setInvulnerable(false);
                }
            }
        }
        //Teleports on answer if chosen in config
        if (teleportOnAnswer && teleportOnAnswerLoc != null) {
            p.teleport(teleportOnAnswerLoc);
        }
        //Removes the player ability to fly (rest of functionality in another class)
        if (getConfig("block-movement")) {
            if (wasFlying.containsKey(p.getUniqueId()) && !wasFlying.get(p.getUniqueId())) {
                p.setAllowFlight(false);
            }
        }
        //Sets effects that limit player vision and visibility after joining
        if (getConfig("set-invisible")) {
            if (wasInvisible.containsKey(p.getUniqueId()) && !wasInvisible.get(p.getUniqueId())) {
                p.removePotionEffect(PotionEffectType.INVISIBILITY);
            }
        }
        if (getConfig("set-blind")) {
            if (wasBlind.containsKey(p.getUniqueId()) && !wasBlind.get(p.getUniqueId())) {
                p.removePotionEffect(PotionEffectType.BLINDNESS);
            }
        }
        //Reapply the potion effect player had before
        if (getConfig("save-potion-effects")) {
            List<PotionEffect> activePotionEffects = potionEffectList.get(p.getUniqueId());
            for (PotionEffect effect : activePotionEffects) {
                p.addPotionEffect(effect);
            }
        }
        //Sets player air to the one he had before he joined
        if (airAmount.containsKey(p.getUniqueId())) {
            p.setRemainingAir(airAmount.get(p.getUniqueId()));
        }
    }

    //Used in putting values to hashMaps
    private <T> void putIfAbsent(Map<UUID, T> map, UUID key, T value) {
        if (!map.containsKey(key)) {
            map.put(key, value);
        }
    }

    //Blocks players from sending messages until answering the question
    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        UUID playerId = p.getUniqueId();
        String chatMess = plugin.getConfig().getString("chat-message");
        chatMess = ChatColor.translateAlternateColorCodes('&', chatMess);

        if (playerAnswers.containsKey(playerId) && !playerAnswers.get(playerId)) {
            e.setCancelled(true);
            p.sendMessage(chatMess);
        }
    }


    //Blocks players movement until they answer question
    @EventHandler
    public void onPlayerMovement(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        UUID playerId = p.getUniqueId();

        if (playerAnswers.containsKey(playerId) && !playerAnswers.get(playerId) && getConfig("block-movement")) {
            e.setCancelled(true);
        }
    }

    //Block players interaction with world until they answer question
    @EventHandler
    public void onInteraction(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        UUID playerId = p.getUniqueId();

        if (playerAnswers.containsKey(playerId) && !playerAnswers.get(playerId) && getConfig("block-interaction")) {
            e.setCancelled(true);
        }
    }

    //Block player from picking up items before they join the server
    @EventHandler
    public void onPickUpItems(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player p) {
            UUID playerId = p.getUniqueId();

            if (!playerAnswers.containsKey(playerId) ||
                    playerAnswers.containsKey(playerId) && !playerAnswers.get(playerId) && getConfig("block-interaction")) {
                e.setCancelled(true);
            }
        }
    }

    //Block players ability to attack until they answer question
    @EventHandler
    public void onEntityAttack(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player attacker) {
            UUID attackerId = attacker.getUniqueId();

            if (playerAnswers.containsKey(attackerId) && !playerAnswers.get(attackerId) && getConfig("block-attack")) {
                e.setCancelled(true);
            }
        }
    }
    //Resets players HashMap
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        UUID playerId = e.getPlayer().getUniqueId();
        if (playerAnswers.containsKey(playerId) && !playerAnswers.get(playerId)) {
            removeEffects(e.getPlayer());
        }
        playerAnswers.remove(playerId);
        mathAnswer.remove(playerId);
        playerStartLocation.remove(playerId);
        wasFlying.remove(playerId);
        wasInvulnerable.remove(playerId);
        wasInvisible.remove(playerId);
        wasBlind.remove(playerId);
        potionEffectList.remove(playerId);
        airAmount.remove(playerId);
    }
}
