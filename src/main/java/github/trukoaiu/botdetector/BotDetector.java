package github.trukoaiu.botdetector;

import github.trukoaiu.botdetector.commands.AnswerCatcher;
import github.trukoaiu.botdetector.commands.OnAnswerTeleport;
import github.trukoaiu.botdetector.commands.OnJoinTeleport;
import github.trukoaiu.botdetector.listeners.ServerJoin;
import github.trukoaiu.botdetector.util.OnJoinTpTabCompleter;
import github.trukoaiu.botdetector.util.TabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class BotDetector extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new ServerJoin(this), this);
        getCommand("answer").setExecutor(new AnswerCatcher());
        getCommand("onjointeleport").setExecutor(new OnJoinTeleport(this));
        getCommand("onanswerteleport").setExecutor(new OnAnswerTeleport(this));
        Bukkit.getServer().getPluginCommand("onanswerteleport").setTabCompleter(new TabCompleter());
        Bukkit.getServer().getPluginCommand("onjointeleport").setTabCompleter(new OnJoinTpTabCompleter());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
