package world.bentobox.controlpanel;


import org.bukkit.Bukkit;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.controlpanel.commands.admin.AdminCommand;
import world.bentobox.controlpanel.commands.user.PlayerCommand;
import world.bentobox.controlpanel.config.Settings;
import world.bentobox.controlpanel.managers.ControlPanelManager;


/**
 * This is main Addon class. It allows to load it into BentoBox hierarchy.
 */
public class ControlPanelAddon extends Addon
{
	// ---------------------------------------------------------------------
	// Section: Methods
	// ---------------------------------------------------------------------


	/**
	 * Executes code when loading the addon. This is called before {@link #onEnable()}.
	 * This <b>must</b> be used to setup configuration, worlds and commands.
	 */
	@Override
	public void onLoad()
	{
		super.onLoad();

		// in most of addons, onLoad we want to store default configuration if it does not
		// exist and load it.

		// Storing default configuration is simple. But be aware, you need
		// @StoreAt(filename="config.yml", path="addons/Likes") in header of your Config file.
		this.saveDefaultConfig();

		this.settings = new Config<>(this, Settings.class).loadConfigObject();

		if (this.settings == null)
		{
			// If we failed to load Settings then we should not enable addon.
			// We can log error and set state to DISABLED.

			this.logError("ControlPanel settings could not load! Addon disabled.");
			this.setState(State.DISABLED);
		}
	}


	/**
	 * Executes code when enabling the addon. This is called after {@link #onLoad()}.
	 * <br/> Note that commands and worlds registration <b>must</b> be done in {@link
	 * #onLoad()}, if need be. Failure to do so <b>will</b> result in issues such as
	 * tab-completion not working for commands.
	 */
	@Override
	public void onEnable()
	{
		// Check if it is enabled - it might be loaded, but not enabled.

		if (this.getPlugin() == null || !this.getPlugin().isEnabled())
		{
			Bukkit.getLogger().severe("BentoBox is not available or disabled!");
			this.setState(State.DISABLED);
			return;
		}

		// Check if addon is not disabled before.

		if (this.getState().equals(State.DISABLED))
		{
			Bukkit.getLogger().severe("ControlPanel Addon is not available or disabled!");
			return;
		}

		// Initialize data manager
		this.manager = new ControlPanelManager(this);

		// If your addon wants to hook into other GameModes, f.e. use flags, then you should
		// hook these flags into each GameMode.

		// Fortunately BentoBox provides ability to a list of all loaded GameModes.

		this.getPlugin().getAddonsManager().getGameModeAddons().forEach(gameModeAddon -> {
			// In Settings (and config) we define DisabledGameModes, list of GameModes where
			// current Addon should not work.
			// This is where we do not hook current addon into GameMode addon.

			if (!this.settings.getDisabledGameModes().contains(gameModeAddon.getDescription().getName()))
			{
				// Each GameMode could have Player Command and Admin Command and we could
				// want to integrate our Example Command into these commands.
				// It provides ability to call command with GameMode command f.e. "/island example"

				// Of course we should check if these commands exists, as it is possible to
				// create GameMode without them.

				gameModeAddon.getPlayerCommand().ifPresent(
					playerCommand -> new PlayerCommand(this, playerCommand));

				gameModeAddon.getAdminCommand().ifPresent(
					adminCommand -> new AdminCommand(this, adminCommand));

				// Load default template if there are no existing control panels
				if (!this.manager.hasAnyControlPanel(gameModeAddon))
				{
					this.manager.importControlPanels(null, gameModeAddon);
				}
			}
		});
	}


	/**
	 * Executes code when reloading the addon.
	 */
	@Override
	public void onReload()
	{
		super.onReload();

		// onReload most of addons just need to reload configuration.
		// If flags, listeners and handlers were set up correctly via Addon.class then
		// they will be reloaded automatically.

		this.settings = new Config<>(this, Settings.class).loadConfigObject();

		if (this.settings == null)
		{
			// If we failed to load Settings then we should not enable addon.
			// We can log error and set state to DISABLED.

			this.logError("ControlPanel settings could not load! Addon disabled.");
			this.setState(State.DISABLED);
		}
		else
		{
			this.manager.reload();
		}
	}


	/**
	 * Executes code when disabling the addon.
	 */
	@Override
	public void onDisable()
	{
		// onDisable we would like to save exisitng settings. It is not necessary for
		// addons that does not have interface for settings editing!

		if (this.settings != null)
		{
			new Config<>(this, Settings.class).saveConfigObject(this.settings);
		}

		this.manager.save();
	}


// ---------------------------------------------------------------------
// Section: Getters
// ---------------------------------------------------------------------


	/**
	 * Method LikesAddon#getSettings returns the settings of this object.
	 *
	 * @return the settings (type Settings) of this object.
	 */
	public Settings getSettings()
	{
		return this.settings;
	}


	/**
	 * Method ControlPanel#getManager returns the manager of this object.
	 *
	 * @return the manager (type ControlPanelManager) of this object.
	 */
	public ControlPanelManager getAddonManager()
	{
		return this.manager;
	}


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------


	/**
	 * Settings object contains
	 */
	private Settings settings;

	/**
	 * Likes addon manager.
	 */
	private ControlPanelManager manager;
}
