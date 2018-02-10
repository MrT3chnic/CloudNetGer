/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;

public class CommandScreen extends Command {

    public CommandScreen()
    {
        super("screen", "cloudnet.command.screen", "sc");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {

        if(CloudNet.getInstance().getScreenProvider().getMainServiceId() != null && args.length > 1 && args[0].equalsIgnoreCase("write"))
        {
            ServiceId serviceId = CloudNet.getInstance().getScreenProvider().getMainServiceId();
            StringBuilder stringBuilder = new StringBuilder();
            for(short i = 1; i < args.length; i++)
            {
                stringBuilder.append(args[i]).append(" ");
            }
            String commandLine = stringBuilder.substring(0, stringBuilder.length() - 1);
            Wrapper wrapper = CloudNet.getInstance().getWrappers().get(serviceId.getWrapperId());
            if(wrapper != null)
            {
                if(wrapper.getServers().containsKey(serviceId.getServerId()))
                {
                    wrapper.writeServerCommand(commandLine, wrapper.getServers().get(serviceId.getServerId()).getServerInfo());
                }
                if(wrapper.getProxys().containsKey(serviceId.getServerId()))
                {
                    wrapper.writeProxyCommand(commandLine, wrapper.getProxys().get(serviceId.getServerId()).getProxyInfo());
                }
            }
            return;
        }

        switch (args.length)
        {
            case 1:
                if (args[0].equalsIgnoreCase("leave") && CloudNet.getInstance().getScreenProvider().getMainServiceId() != null)
                {

                    ServiceId serviceId = CloudNet.getInstance().getScreenProvider().getMainServiceId();
                    CloudNet.getInstance().getScreenProvider().disableScreen(serviceId.getServerId());
                    CloudNet.getInstance().getScreenProvider().setMainServiceId(null);
                    sender.sendMessage("Du hast die Screen session verlassen");
                    return;
                }
                break;
            case 2:
                if (args[0].equalsIgnoreCase("-s") || args[0].equalsIgnoreCase("server"))
                {

                    MinecraftServer minecraftServer = CloudNet.getInstance().getServer(args[1]);
                    if (minecraftServer != null)
                    {

                        ServiceId serviceId = CloudNet.getInstance().getScreenProvider().getMainServiceId();
                        if(serviceId != null)
                        {
                            CloudNet.getInstance().getScreenProvider().disableScreen(serviceId.getServerId());
                            CloudNet.getInstance().getScreenProvider().setMainServiceId(null);
                        }

                        minecraftServer.getWrapper().enableScreen(minecraftServer.getServerInfo());
                        sender.sendMessage("Du bist der Screen session von " + minecraftServer.getServerId() +" beigetreten");
                        CloudNet.getInstance().getScreenProvider().setMainServiceId(minecraftServer.getServiceId());
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("-p") || args[0].equalsIgnoreCase("proxy"))
                {

                    ProxyServer minecraftServer = CloudNet.getInstance().getProxy(args[1]);
                    if (minecraftServer != null)
                    {
                        ServiceId serviceId = CloudNet.getInstance().getScreenProvider().getMainServiceId();
                        if(serviceId != null)
                        {
                            CloudNet.getInstance().getScreenProvider().disableScreen(serviceId.getServerId());
                            CloudNet.getInstance().getScreenProvider().setMainServiceId(null);
                        }

                        minecraftServer.getWrapper().enableScreen(minecraftServer.getProxyInfo());
                        sender.sendMessage("Du bist der Screen session von " + minecraftServer.getServerId() +" beigetreten");
                        CloudNet.getInstance().getScreenProvider().setMainServiceId(minecraftServer.getServiceId());
                    }
                    return;
                }
                break;
            default:
                sender.sendMessage(
                        "screen server (-s) | proxy (-p) <name> | Der Log der aus Konsole des Services wird transferriert zu der Konsole dieser Instanz",
                        "screen leave | Damit kannst du den Screen wieder verlassen",
                        "screen write <command> | Damit kannst du in der Screen session schreiben in der du bist"
                );
                break;
        }
    }
}