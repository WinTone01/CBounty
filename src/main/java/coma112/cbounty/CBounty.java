package coma112.cbounty;

import coma112.cbounty.config.Config;
import coma112.cbounty.database.AbstractDatabase;
import coma112.cbounty.database.MySQL;
import coma112.cbounty.hooks.BountyEconomy;
import coma112.cbounty.hooks.Placeholder;
import coma112.cbounty.language.Language;
import coma112.cbounty.managers.BountyManager;
import coma112.cbounty.utils.CommandRegister;
import coma112.cbounty.utils.ListenerRegister;
import lombok.Getter;
import me.realized.tokenmanager.api.TokenManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;

public final class CBounty extends JavaPlugin {
    @Getter private static CBounty instance;
    @Getter private static AbstractDatabase databaseManager;
    private static Language language;
    private static Config config;
    private static BountyManager bountyManager;
    @Getter private static TokenManager tokenManager;

    @Override
    public void onEnable() {
        instance = this;

        registerHooks();
        initializeComponents();
        registerListenersAndCommands();
        initializeDatabaseManager();

        MySQL mysql = (MySQL) databaseManager;
        mysql.createTable();
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) databaseManager.disconnect();
    }

    public Language getLanguage() {
        return language;
    }

    public Config getConfiguration() {
        return config;
    }

    public BountyManager getBountyManager() {
        return bountyManager;
    }

    private void initializeComponents() {
        language = new Language();
        config = new Config();
        bountyManager = new BountyManager();
    }

    private void registerHooks() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) new Placeholder().register();
        if (getServer().getPluginManager().getPlugin("Vault") != null) BountyEconomy.register();
        if (getServer().getPluginManager().getPlugin("TokenManager") != null) tokenManager = (TokenManager) getServer().getPluginManager().getPlugin("TokenManager");
    }

    private void registerListenersAndCommands() {
        ListenerRegister.registerEvents();
        CommandRegister.registerCommands();
    }

    private void initializeDatabaseManager() {
        try {
            databaseManager = new MySQL(Objects.requireNonNull(getConfiguration().getSection("database.mysql")));
        } catch (SQLException | ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }
}
