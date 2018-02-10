/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;

/**
 * Created by Tareko on 20.08.2017.
 */
public class CommandCmd extends Command {

    public CommandCmd()
    {
        super("cmd", "cloudnet.command.cmd", "command");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        if(args.length > 1)
        {
            for(Wrapper wrapper : CloudNet.getInstance().getWrappers().values())
            {
                for(MinecraftServer minecraftServer : wrapper.getServers().values())
                {
                    if(minecraftServer.getServiceId().getServerId().equalsIgnoreCase(args[0]))
                    {
                        StringBuilder stringBuilder = new StringBuilder();
                        for(short i = 1; i < args.length; i++)
                        {
                            stringBuilder.append(args[i]).append(" ");
                        }
                        minecraftServer.getWrapper().writeServerCommand(stringBuilder.substring(0, stringBuilder.length() - 1), minecraftServer.getServerInfo());
                        sender.sendMessage("Sende command zu " + minecraftServer.getServiceId().getServerId() + " mit [\"" + stringBuilder.substring(0, stringBuilder.length() -1) + "\"]");
                        return;
                    }
                }

                for(ProxyServer minecraftServer : wrapper.getProxys().values())
                {
                    if(minecraftServer.getServiceId().getServerId().equalsIgnoreCase(args[0]))
                    {
                        StringBuilder stringBuilder = new StringBuilder();
                        for(short i = 1; i < args.length; i++)
                        {
                            stringBuilder.append(args[i]).append(" ");
                        }
                        minecraftServer.getWrapper().writeProxyCommand(stringBuilder.substring(0, stringBuilder.length() - 1), minecraftServer.getProxyInfo());
                        sender.sendMessage("Sende command zu " + minecraftServer.getServiceId().getServerId() + " mit [\"" + stringBuilder.substring(0, stringBuilder.length() -1) + "\"]");
                        return;
                    }
                }
            }
        }
        else
        {
            sender.sendMessage("cmd <name> <command> | Sende befehl an einen Proxy oder Server");
        }
    }
}