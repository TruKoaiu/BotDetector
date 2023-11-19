package github.trukoaiu.botdetector.commands;

import github.trukoaiu.botdetector.BotDetector;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OnAnswerTeleport implements CommandExecutor {
    private final BotDetector plugin;

    public OnAnswerTeleport(BotDetector plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player p){
            Location pL = p.getLocation();

            if (strings.length > 0){
                if (strings[0].equalsIgnoreCase("set")){
                    plugin.getConfig().set("teleport-on-answer-cords", pL);
                    plugin.getConfig().set("teleport-on-answer", true);
                    plugin.saveConfig();
                    p.sendMessage(ChatColor.YELLOW + "You have set On Answer Teleport.");
                    p.sendMessage(ChatColor.YELLOW + "Config 'teleport-on-answer' has been set to true");
                } else if (strings[0].equalsIgnoreCase("remove")){
                    plugin.getConfig().set("teleport-on-answer-cords", null);
                    plugin.getConfig().set("teleport-on-answer", false);
                    plugin.saveConfig();
                    p.sendMessage(ChatColor.YELLOW + "You have removed On Answer Teleport.");
                    p.sendMessage(ChatColor.YELLOW + "Config 'teleport-on-answer' has been set to false");
                } else if (strings[0].equalsIgnoreCase("true")) {
                    plugin.getConfig().set("teleport-on-answer", true);
                    plugin.saveConfig();
                    p.sendMessage(ChatColor.YELLOW + "Config 'teleport-on-answer' has been set to true");
                } else if (strings[0].equalsIgnoreCase("false")) {
                    plugin.getConfig().set("teleport-on-answer", false);
                    plugin.saveConfig();
                    p.sendMessage(ChatColor.YELLOW + "Config 'teleport-on-answer' has been set to false");
                } else {
                    p.sendMessage(ChatColor.RED + "You can only add 'true', 'false', 'set' or 'remove' parameters");
                }
            } else {
                p.sendMessage(ChatColor.YELLOW + "You have to specify what you want to use it for.");
            }
        }


        return true;
    }
}
