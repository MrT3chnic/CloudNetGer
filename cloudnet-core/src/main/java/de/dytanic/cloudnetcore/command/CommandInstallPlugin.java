/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;

/**
 * Created by Tareko on 27.08.2017.
 */
public class CommandInstallPlugin extends Command {

    public CommandInstallPlugin()
    {
        super("installplugin", "cloudnet.command.installplugin");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 3:
                MinecraftServer minecraftServer = CloudNet.getInstance().getServer(args[0]);
                if(minecraftServer != null && minecraftServer.getChannel() != null)
                {
                    minecraftServer.sendCustomMessage("cloudnet_internal", "install_plugin", new Document("name", args[1]).append("url", args[2]));
                    sender.sendMessage("Das Plugin wir auf " + args[0] + "installiert ...");
                }
                else {
                    sender.sendMessage("Der Server existiert nicht!");
                }
                break;
            default:
                sender.sendMessage("installplugin <server> <name> <url> | Du kannst ein Plugin auf ein Server intallieren");
                break;
        }
    }
}