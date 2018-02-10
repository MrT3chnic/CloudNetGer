/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.modules.Module;
import de.dytanic.cloudnetcore.CloudNet;

/**
 * Created by Tareko on 23.08.2017.
 */
public class CommandModules extends Command {

    public CommandModules()
    {
        super("modules", "cloudnet.command.modules", "m");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        sender.sendMessage("Laufende module:", " ");
        for(Module module : CloudNet.getInstance().getModuleManager().getModules())
        {
            sender.sendMessage(module.getName() + " " + module.getModuleConfig().getVersion() + " von " + module.getModuleConfig().getAuthor() + "");
        }
    }
}