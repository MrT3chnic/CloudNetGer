/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.threading.Runnabled;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.CloudServer;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;

public class CommandShutdown extends Command {

    public CommandShutdown()
    {
        super("shutdown", "cloudnet.command.shutdown");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 2:
                if (args[0].equalsIgnoreCase("wrapper"))
                {
                    if (CloudNet.getInstance().getWrappers().containsKey(args[1]))
                    {
                        Wrapper wrapper = CloudNet.getInstance().getWrappers().get(args[1]);
                        if (wrapper.getChannel() != null)
                        {
                            wrapper.writeCommand("stop");
                        }
                        sender.sendMessage("Wrapper " + args[1] + " wird gestoppt");
                    } else
                    {
                        sender.sendMessage("Der Wrapper existiert nicht!");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("group"))
                {
                    if (CloudNet.getInstance().getServerGroups().containsKey(args[1]))
                    {
                        System.out.println("Alle Server aus der Gruppe " + args[1] + " werden gestoppt!");
                        CollectionWrapper.iterator(CloudNet.getInstance().getServers(args[1]), new Runnabled<MinecraftServer>() {
                            @Override
                            public void run(MinecraftServer obj)
                            {
                                obj.getWrapper().stopServer(obj);
                                NetworkUtils.sleepUninterruptedly(1000);
                            }
                        });
                        return;
                    }
                    if (CloudNet.getInstance().getProxyGroups().containsKey(args[1]))
                    {
                        System.out.println("Alle Proxies aus der Gruppe" + args[1] + " werden gestoppt!");
                        CollectionWrapper.iterator(CloudNet.getInstance().getProxys(args[1]), new Runnabled<ProxyServer>() {
                            @Override
                            public void run(ProxyServer obj)
                            {
                                obj.getWrapper().stopProxy(obj);
                                NetworkUtils.sleepUninterruptedly(1000);
                            }
                        });
                        return;
                    }

                    sender.sendMessage("Group doesn't exist");
                    return;
                }
                if (args[0].equalsIgnoreCase("server"))
                {
                    MinecraftServer proxyServer = CloudNet.getInstance().getServer(args[1]);
                    if (proxyServer != null)
                    {
                        proxyServer.getWrapper().stopServer(proxyServer);
                        sender.sendMessage("Der Server " + args[1] + " wird gestoppt!");
                    } else
                    {
                        CloudServer proxyServers = CloudNet.getInstance().getCloudGameServer(args[1]);
                        if(proxyServers != null)
                        {
                            proxyServers.getWrapper().stopServer(proxyServers);
                        }
                        else
                        {
                            sender.sendMessage("Der Server existiert nicht!");
                        }
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("proxy"))
                {
                    ProxyServer proxyServer = CloudNet.getInstance().getProxy(args[1]);
                    if (proxyServer != null)
                    {
                        proxyServer.getWrapper().stopProxy(proxyServer);
                        sender.sendMessage("Der Proxy Server " + args[1] + " wird gestoppt!");
                    } else
                    {
                        sender.sendMessage("Der Proxy existiert nicht!");
                    }
                    return;
                }
                break;
            default:
                sender.sendMessage(
                        " ",
                        "shutdown WRAPPER <wrapper-id> | Stoppt einen Wrapper mit der entsprechenden \"Wrapper ID\"",
                        "shutdown GROUP <group-id> | Stoppt eine Gruppe von einem Proxy oder einer Servergruppe und startet sie standardmäßig neu",
                        "shutdown PROXY <proxy-id> | Stoppt den BungeeCord udn startet in wieder.",
                        "shutdown SERVER <server-id> | Stoppt den MineCraft Server und startet ihn Wieder.",
                        " "
                );
                break;
        }
    }
}