//
// Created by BONNe
// Copyright - 2019
//


package world.bentobox.controlpanel.database.objects;


import java.util.List;

import org.bukkit.Material;

import com.google.gson.annotations.Expose;

import world.bentobox.bentobox.database.objects.DataObject;
import world.bentobox.bentobox.database.objects.Table;


/**
 * Object that allows to define different control panels.
 */
@Table(name = "ControlPanel")
public class ControlPanelObject implements DataObject
{
    /**
     * Constructor ControlPanelObject creates a new ControlPanelObject instance.
     */
    public ControlPanelObject()
    {
        // Empty constructor
    }


    // ---------------------------------------------------------------------
    // Section: Getters and Setters
    // ---------------------------------------------------------------------


    /**
     * @return the uniqueId
     */
    @Override
    public String getUniqueId()
    {
        return this.uniqueId;
    }


    /**
     * @param uniqueId - unique ID the uniqueId to set
     */
    @Override
    public void setUniqueId(String uniqueId)
    {
        this.uniqueId = uniqueId;
    }


    /**
     * Method LikesObject#getGameMode returns the gameMode of this object.
     *
     * @return the gameMode (type String) of this object.
     */
    public String getGameMode()
    {
        return gameMode;
    }


    /**
     * Method LikesObject#setGameMode sets new value for the gameMode of this object.
     *
     * @param gameMode new value for this object.
     */
    public void setGameMode(String gameMode)
    {
        this.gameMode = gameMode;
    }


    /**
     * Method ControlPanelObject#getPermissionSuffix returns the permissionSuffix of this object.
     *
     * @return the permissionSuffix (type String) of this object.
     */
    public String getPermissionSuffix()
    {
        return permissionSuffix;
    }


    /**
     * Method ControlPanelObject#setPermissionSuffix sets new value for the permissionSuffix of this object.
     * @param permissionSuffix new value for this object.
     *
     */
    public void setPermissionSuffix(String permissionSuffix)
    {
        this.permissionSuffix = permissionSuffix;
    }


    /**
     * Method ControlPanelObject#getPanelName returns the panelName of this object.
     *
     * @return the panelName (type String) of this object.
     */
    public String getPanelName()
    {
        return panelName;
    }


    /**
     * Method ControlPanelObject#setPanelName sets new value for the panelName of this object.
     * @param panelName new value for this object.
     *
     */
    public void setPanelName(String panelName)
    {
        this.panelName = panelName;
    }


    /**
     * Method ControlPanelObject#getPanelButtons returns the panelButtons of this object.
     *
     * @return the panelButtons (type List<ControlPanelButton>) of this object.
     */
    public List<ControlPanelButton> getPanelButtons()
    {
        return panelButtons;
    }


    /**
     * Method ControlPanelObject#setPanelButtons sets new value for the panelButtons of this object.
     * @param panelButtons new value for this object.
     *
     */
    public void setPanelButtons(List<ControlPanelButton> panelButtons)
    {
        this.panelButtons = panelButtons;
    }


    /**
     * Method ControlPanelObject#isDefaultPanel returns the defaultPanel of this object.
     *
     * @return the defaultPanel (type boolean) of this object.
     */
    public boolean isDefaultPanel()
    {
        return defaultPanel;
    }


    /**
     * Method ControlPanelObject#setDefaultPanel sets new value for the defaultPanel of this object.
     * @param defaultPanel new value for this object.
     *
     */
    public void setDefaultPanel(boolean defaultPanel)
    {
        this.defaultPanel = defaultPanel;
    }


    // ---------------------------------------------------------------------
    // Section: Private Class
    // ---------------------------------------------------------------------


    /**
     * This class allows to add custom buttons to current panel.
     */
    public static class ControlPanelButton
    {
        /**
         * Constructor ControlPanelButton creates a new ControlPanelButton instance.
         */
        public ControlPanelButton()
        {
            // Empty constructor
        }


        // ---------------------------------------------------------------------
        // Section: Getters and Setters
        // ---------------------------------------------------------------------


        /**
         * Method ControlPanelButton#getSlot returns the slot of this object.
         *
         * @return the slot (type int) of this object.
         */
        public int getSlot()
        {
            return slot;
        }


        /**
         * Method ControlPanelButton#setSlot sets new value for the slot of this object.
         * @param slot new value for this object.
         *
         */
        public void setSlot(int slot)
        {
            this.slot = slot;
        }


        /**
         * Method ControlPanelButton#getMaterial returns the material of this object.
         *
         * @return the material (type Material) of this object.
         */
        public Material getMaterial()
        {
            return material;
        }


        /**
         * Method ControlPanelButton#setMaterial sets new value for the material of this object.
         * @param material new value for this object.
         *
         */
        public void setMaterial(Material material)
        {
            this.material = material;
        }


        /**
         * Method ControlPanelButton#getDescription returns the description of this object.
         *
         * @return the description (type String) of this object.
         */
        @Deprecated
        public String getDescription()
        {
            return description;
        }


        /**
         * Method ControlPanelButton#setDescription sets new value for the description of this object.
         * @param description new value for this object.
         *
         */
        @Deprecated
        public void setDescription(String description)
        {
            this.description = description;
        }


        /**
         * Method ControlPanelButton#getCommand returns the command of this object.
         *
         * @return the command (type String) of this object.
         */
        public String getCommand()
        {
            return command;
        }


        /**
         * Method ControlPanelButton#setCommand sets new value for the command of this object.
         * @param command new value for this object.
         *
         */
        public void setCommand(String command)
        {
            this.command = command;
        }


        /**
         * Method ControlPanelButton#getName returns the name of this object.
         *
         * @return the name (type String) of this object.
         */
        public String getName()
        {
            return name;
        }


        /**
         * Method ControlPanelButton#setName sets new value for the name of this object.
         * @param name new value for this object.
         */
        public void setName(String name)
        {
            this.name = name;
        }

        /**
         * Method ControlPanelButton#getDescriptionLines returns the descriptionLines of this object.
         *
         * @return the descriptionLines (type List<String></String>) of this object.
         */
        public List<String> getDescriptionLines()
        {
            return descriptionLines;
        }


        /**
         * Method ControlPanelButton#setDescriptionLines sets new value for the descriptionLines of this object.
         * @param descriptionLines new value for this object.
         */
        public void setDescriptionLines(List<String> descriptionLines)
        {
            this.descriptionLines = descriptionLines;
        }


        // ---------------------------------------------------------------------
        // Section: Variables
        // ---------------------------------------------------------------------


        /**
         * Slot number of the button
         */
        @Expose
        private int slot;

        /**
         * Material icon for button
         */
        @Expose
        private Material material;

        /**
         * Description for the button
         */
        @Expose
        @Deprecated
        private String description;

        /**
         * Description lines for the button
         */
        @Expose
        private List<String> descriptionLines;

        /**
         * Command that will run on the click.
         */
        @Expose
        private String command;

        /**
         * Name of the Button.
         */
        @Expose
        private String name;
    }


    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------

    /**
     * Likes object id. Island ID;
     */
    @Expose
    private String uniqueId;

    /**
     * Indicate that current panel is default one.
     */
    @Expose
    private boolean defaultPanel;

    /**
     * GameMode where current object operates.
     */
    @Expose
    private String gameMode;

    /**
     * Permission suffix for panel to work
     */
    @Expose
    private String permissionSuffix;

    /**
     * Name of current panel
     */
    @Expose
    private String panelName;

    /**
     * List of buttons in current panel
     */
    @Expose
    private List<ControlPanelButton> panelButtons;
}
