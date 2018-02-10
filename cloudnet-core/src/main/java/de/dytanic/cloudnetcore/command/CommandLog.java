/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;

/**
 * Created by Tareko on 04.10.2017.
 */
public class CommandLog extends Command {

    public CommandLog()
    {
        super("log", "cloudnet.command.log");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 1:
            {
                MinecraftServer minecraftServer = CloudNet.getInstance().getServer(args[0]);
                if(minecraftServer != null)
                {
                    String rndm = NetworkUtils.randomString(10);
                    CloudNet.getInstance().getServerLogManager().append(rndm, minecraftServer.getServerId());
                    String x = new StringBuilder(CloudNet.getInstance().getOptionSet().has("ssl") ? "https://" : "http://").append(CloudNet.getInstance().getConfig().getWebServerConfig().getAddress()).append(":").append(CloudNet.getInstance().getConfig().getWebServerConfig().getPort()).append("/cloudnet/log?server=").append(rndm).substring(0);
                    sender.sendMessage("Du kannst du Log auf: " + x +"sehen.");
                    sender.sendMessage("Der Log wird in 10 Minuten automatisch gelöscht.");
                }
                else
                {
                    sender.sendMessage("Der Server existiert nicht!");
                }
            }
                break;
            default:
                sender.sendMessage("log <server> | Erstellt einen Log vom Server (per link)");
                break;
        }
    }
}