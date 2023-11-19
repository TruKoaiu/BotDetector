package github.trukoaiu.botdetector.util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OnJoinTpTabCompleter implements org.bukkit.command.TabCompleter {
    private static final List<String> OPTIONS = new ArrayList<>();

    static {
        OPTIONS.add("set");
        OPTIONS.add("remove");
        OPTIONS.add("true");
        OPTIONS.add("false");
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> completions = new ArrayList<>();

        if (strings.length == 1){
            StringUtil.copyPartialMatches(strings[0], OPTIONS, completions);
        }

        return completions;
    }
}
