//
// Created by BONNe
// Copyright - 2019
//


package world.bentobox.controlpanel.managers;


import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.Database;
import world.bentobox.controlpanel.ControlPanelAddon;
import world.bentobox.controlpanel.database.objects.ControlPanelObject;
import world.bentobox.controlpanel.database.objects.ControlPanelObject.ControlPanelButton;
import world.bentobox.controlpanel.panels.GuiUtils;
import world.bentobox.controlpanel.utils.Constants;
import world.bentobox.controlpanel.utils.Utils;


/**
 * This class manages control panel addon data.
 */
public class ControlPanelManager
{
    // ---------------------------------------------------------------------
    // Section: Constructor
    // ---------------------------------------------------------------------


    /**
     * Default constructor.
     * @param addon Likes Addon instance
     */
    public ControlPanelManager(ControlPanelAddon addon)
    {
        this.addon = addon;

        // save template file into directory.
        if (!new File(this.addon.getDataFolder(), "controlPanelTemplate.yml").exists())
        {
            this.addon.saveResource("controlPanelTemplate.yml", false);
        }

        this.controlPanelCache = new HashMap<>();
        this.controlPanelDatabase = new Database<>(addon, ControlPanelObject.class);

        this.load();
    }


    // ---------------------------------------------------------------------
    // Section: Load Methods
    // ---------------------------------------------------------------------


    /**
     * This method loads all control panel objects.
     */
    public void load()
    {
        this.controlPanelCache.clear();

        this.addon.getLogger().info("Loading control panels...");

        this.controlPanelDatabase.loadObjects().forEach(this::load);
    }


    /**
     * This method loads given controlPanelObject inside cache.
     * @param controlPanelObject Object that must be added to cache.
     */
    private void load(ControlPanelObject controlPanelObject)
    {
        // Migrate older data
        controlPanelObject.getPanelButtons().forEach(button -> {
            if (button.getName() == null)
            {
                button.setName(button.getCommand());
            }

            if (button.getDescriptionLines() == null &&
                    button.getDescription() != null &&
                    !button.getDescription().isEmpty())
            {
                button.setDescriptionLines(new ArrayList<>());
                button.getDescriptionLines().addAll(GuiUtils.stringSplit(button.getDescription(), 999, false));
                button.setDescription(null);
            }
        });

        // Add object into cache
        this.controlPanelCache.put(controlPanelObject.getUniqueId(), controlPanelObject);
    }


    /**
     * This method reloads all control panels from database to cache.
     */
    public void reload()
    {
        this.controlPanelCache.clear();

        this.addon.getLogger().info("Reloading control panels...");

        this.controlPanelDatabase.loadObjects().forEach(this::load);
    }


    // ---------------------------------------------------------------------
    // Section: Save methods
    // ---------------------------------------------------------------------


    /**
     * This method saves all cached values into database.
     */
    public void save()
    {
        this.controlPanelCache.values().forEach(this.controlPanelDatabase::saveObjectAsync);
    }


    // ---------------------------------------------------------------------
    // Section: Wipe methods
    // ---------------------------------------------------------------------


    /**
     * This method removes all data from database that referee to given world.
     */
    public void wipeData(@Nullable World world, @Nullable User user)
    {
        this.wipeData(Utils.getGameMode(world), user);
    }


    /**
     * This method removes all data from database that referee to given game mode addon.
     */
    public void wipeData(@NotNull GameModeAddon addon, @Nullable User user)
    {
        this.wipeData(Utils.getGameMode(addon), user);
    }


    /**
     * This method removes all data from database that referee to given game mode addon.
     */
    private void wipeData(@NotNull String addon, @Nullable User user)
    {
        if (addon.isEmpty())
        {
            // Missing gamemode name.
            return;
        }

        if (this.hasAnyControlPanel(addon))
        {
            // Empty sorted cache
            List<String> keySet = new ArrayList<>(this.controlPanelCache.keySet());

            // Remove everything that starts with gamemode name.
            keySet.forEach(uniqueId -> {
                if (uniqueId.startsWith(addon))
                {
                    this.controlPanelCache.remove(uniqueId);
                    this.controlPanelDatabase.deleteID(uniqueId);
                }
            });

            if (user != null)
            {
                user.sendMessage(Constants.MESSAGE + "control-panels-wiped", Constants.VARIABLE_GAMEMODE, addon);
            }
            else
            {
                this.addon.log("Control Panels in " + addon + " are removed!");
            }
        }
    }


    // ---------------------------------------------------------------------
    // Section: Import methods
    // ---------------------------------------------------------------------


    /**
     * This method imports control panels into world gamemode.
     * @param user User who called import method.
     * @param world World that is targeted.
     * @param fileName Specifies from which file control panel will be loaded
     */
    public void importControlPanels(@Nullable User user, @NotNull World world, @NotNull String fileName)
    {
        this.importControlPanels(user, Utils.getGameMode(world), fileName);
    }


    /**
     * This method imports control panels into gamemode.
     * @param user User who called import method.
     * @param addon Addon that is targeted.
     */
    public void importControlPanels(@Nullable User user, @NotNull GameModeAddon addon)
    {
        this.importControlPanels(user, addon, "controlPanelTemplate");
    }


    /**
     * This method imports control panels into gamemode.
     * @param user User who called import method.
     * @param addon Addon that is targeted.
     * @param fileName Specifies from which file control panel will be loaded
     */
    public void importControlPanels(@Nullable User user, @NotNull GameModeAddon addon, @NotNull String fileName)
    {
        this.importControlPanels(user, Utils.getGameMode(addon), fileName);
    }


    /**
     * This method imports control panels
     *
     * @param user - user
     * @param gameModeName - gamemode name where ControlPanels must be imported.
     * @param fileName Specifies from which file control panel will be loaded
     * @return true if successful
     */
    private void importControlPanels(@Nullable User user, String gameModeName, @NotNull String fileName)
    {
        if (gameModeName.isEmpty())
        {
            if (user != null)
            {
                user.sendMessage(Constants.ERRORS + "not-a-gamemode-world");
            }
            else
            {
                this.addon.logError("Not a GameMode world.");
            }

            return;
        }

        // Add yml at the end.
        if (!fileName.endsWith(".yml"))
        {
            fileName = fileName + ".yml";
        }

        File controlFile = new File(this.addon.getDataFolder(), fileName);

        if (!controlFile.exists())
        {
            if (user != null)
            {
                user.sendMessage(Constants.ERRORS + "no-file",
                    Constants.VARIABLE_FILENAME, fileName);
            }
            else
            {
                this.addon.logError("Missing [file] file.");
            }

            return;
        }

        YamlConfiguration config = new YamlConfiguration();

        try
        {
            config.load(controlFile);
        }
        catch (IOException | InvalidConfigurationException e)
        {
            if (user != null)
            {
                user.sendMessage(Constants.ERRORS + "no-load",
                    Constants.VARIABLE_MESSAGE, e.getMessage(),
                    Constants.VARIABLE_FILENAME, fileName);
            }
            else
            {
                this.addon.logError(e.getMessage());
                e.printStackTrace();
            }

            return;
        }

        this.readControlPanel(config, user, gameModeName);

        // Update biome order.
        this.addon.getAddonManager().save();
    }


    /**
     * This method creates control panel object from config file.
     * @param config YamlConfiguration that contains all control panels.
     * @param user User who calls reading.
     * @param gameMode GameMode where current panel works.
     */
    private void readControlPanel(YamlConfiguration config, @Nullable User user, final String gameMode)
    {
        int newControlPanelCount = 0;

        ConfigurationSection reader = config.getConfigurationSection("panel-list");

        for (String keyReference : Objects.requireNonNull(reader).getKeys(false))
        {
            final String uniqueId = gameMode + "_" + keyReference;

            if (!this.controlPanelCache.containsKey(uniqueId))
            {
                ControlPanelObject controlPanel = new ControlPanelObject();
                controlPanel.setUniqueId(uniqueId);
                controlPanel.setGameMode(gameMode);

                ConfigurationSection panelSection = reader.getConfigurationSection(keyReference);

                if (panelSection != null)
                {
                    controlPanel.setPanelName(panelSection.getString("panelName", "&1Commands"));
                    controlPanel.setPermissionSuffix(panelSection.getString("permission", "default"));
                    controlPanel.setDefaultPanel(panelSection.getBoolean("defaultPanel", false));

                    List<ControlPanelButton> buttonList = new ArrayList<>();
                    controlPanel.setPanelButtons(buttonList);

                    ConfigurationSection buttonListSection = panelSection.getConfigurationSection("buttons");

                    if (buttonListSection != null)
                    {
                        buttonListSection.getKeys(false).forEach(slotReference -> {
                            ControlPanelButton button = new ControlPanelButton();
                            button.setSlot(Integer.parseInt(slotReference));

                            ConfigurationSection buttonSection =
                                buttonListSection.getConfigurationSection(slotReference);

                            if (buttonSection != null)
                            {
                                button.setName(buttonSection.getString("name"));
                                button.setCommand(buttonSection.getString("command", "[user_command]"));

                                // Create empty list
                                button.setDescriptionLines(new ArrayList<>());

                                if (buttonSection.isList("description"))
                                {
                                    // Read description by each line
                                    buttonSection.getStringList("description").forEach(line ->
                                        button.getDescriptionLines().add(
                                            line.replace("[gamemode]", gameMode.toLowerCase())));
                                }
                                else if (buttonSection.isString("description"))
                                {
                                    // Check if description is not defined as simple string
                                    String input = buttonSection.getString("description", "");

                                    if (input != null && !input.isEmpty())
                                    {
                                        button.getDescriptionLines().add(
                                            input.replace("[gamemode]", gameMode.toLowerCase()));
                                    }
                                }
                                else
                                {
                                    this.addon.logWarning("Description for button " +
                                        + button.getSlot() + " could not be read.");
                                }

                                button.setMaterial(Material.matchMaterial(buttonSection.getString("material", "GRASS")));

                                buttonList.add(button);
                            }
                        });
                    }

                    // Save and load in cache.
                    this.controlPanelDatabase.saveObjectAsync(controlPanel);
                    this.load(controlPanel);

                    newControlPanelCount++;
                }
            }
        }

        if (user != null)
        {
            user.sendMessage(Constants.MESSAGE + "import-count",
                "[number]",
                String.valueOf(newControlPanelCount));
        }
        else
        {
            this.addon.log("Imported " + newControlPanelCount + " control panels in " + gameMode);
        }
    }


    // ---------------------------------------------------------------------
    // Section: Processing methods
    // ---------------------------------------------------------------------


    /**
     * This method returns if controlPanel in given gamemode exists in cache.
     * @param world World that must be checked.
     * @return {@code true} if game mode has a control panel in database, {@code false} otherwise.
     */
    public boolean hasAnyControlPanel(World world)
    {
        return this.hasAnyControlPanel(Utils.getGameMode(world));
    }


    /**
     * This method returns if controlPanel in given gamemode exists in cache.
     * @param addon GameMode addon.
     * @return {@code true} if game mode has a control panel in database, {@code false} otherwise.
     */
    public boolean hasAnyControlPanel(GameModeAddon addon)
    {
        return this.hasAnyControlPanel(Utils.getGameMode(addon));
    }


    /**
     * This method returns if controlPanel in given gamemode exists in cache.
     * @param gameModeName GameMode addon name.
     * @return {@code true} if game mode has a control panel in database, {@code false} otherwise.
     */
    private boolean hasAnyControlPanel(String gameModeName)
    {
        return !gameModeName.isEmpty() && this.controlPanelCache.keySet().stream().
            anyMatch(value -> value.startsWith(gameModeName));
    }


    /**
     * This method finds corresponding ControlPanel Object for user in given world.
     * @param user User who wants to open panel
     * @param world World where panel should be opened
     * @param permissionPrefix Permission prefix.
     * @return ControlPanelObject or null.
     */
    public ControlPanelObject getUserControlPanel(User user, World world, String permissionPrefix)
    {
        String gameMode = Utils.getGameMode(world);

        String permission = Utils.getPermissionValue(user,
                permissionPrefix + "controlpanel.panel",
                null);

        if (permission == null || !this.controlPanelCache.containsKey(gameMode + "_" + permission))
        {
            // Find first default for current game mode.

            return this.controlPanelCache.values().stream().
                    filter(panel -> panel.isDefaultPanel() && panel.getGameMode().equals(gameMode)).
                    findFirst().orElse(null);
        }
        else
        {
            return this.controlPanelCache.get(gameMode + "_" + permission);
        }
    }


    // ---------------------------------------------------------------------
    // Section: Instance Variables
    // ---------------------------------------------------------------------


    /**
     * Control Panel Addon instance.
     */
    private ControlPanelAddon addon;

    /**
     * This database allows to access to all stored control panels.
     */
    private Database<ControlPanelObject> controlPanelDatabase;

    /**
     * This map contains all control panel object linked to their reference game mode.
     */
    private Map<String, ControlPanelObject> controlPanelCache;
}
