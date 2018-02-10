/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;

import java.util.function.Consumer;

public class CommandReload extends Command {

    public CommandReload()
    {
        super("reload", "cloudnet.command.reload", "rl");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 1:
                if (args[0].equalsIgnoreCase("all"))
                {
                    sender.sendMessage("[RELOAD] Versuche CloudNet zu reloaden...");
                    try
                    {
                        CloudNet.getInstance().reload();
                        sender.sendMessage("[RELOAD] Der Reload war Erfolgreich");
                    } catch (Exception e)
                    {
                        sender.sendMessage("[RELOAD] Der Reload ist Fehlgeschlagen");
                        e.printStackTrace();
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("config"))
                {
                    sender.sendMessage("[RELOAD] Versuche die Config zu reloaden");
                    try
                    {
                        CloudNet.getInstance().getConfig().load();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    CloudNet.getInstance().getServerGroups().clear();
                    CloudNet.getInstance().getProxyGroups().clear();
                    CloudNet.getInstance().getUsers().clear();
                    CloudNet.getInstance().getUsers().addAll(CloudNet.getInstance().getConfig().getUsers());

                    NetworkUtils.addAll(CloudNet.getInstance().getServerGroups(), CloudNet.getInstance().getConfig().getServerGroups(), new Acceptable<ServerGroup>() {
                        @Override
                        public boolean isAccepted(ServerGroup value)
                        {
                            System.out.println("Lade ServerGruppe: " + value.getName());
                            CloudNet.getInstance().setupGroup(value);
                            return true;
                        }
                    });

                    NetworkUtils.addAll(CloudNet.getInstance().getProxyGroups(), CloudNet.getInstance().getConfig().getProxyGroups(), new Acceptable<ProxyGroup>() {

                        public boolean isAccepted(ProxyGroup value)
                        {
                            System.out.println("lade ProxyGruppe: " + value.getName());
                            CloudNet.getInstance().setupProxy(value);
                            return true;
                        }
                    });

                    CloudNet.getInstance().getNetworkManager().reload();
                    CloudNet.getInstance().getNetworkManager().updateAll();
                    CloudNet.getInstance().getWrappers().values().forEach(new Consumer<Wrapper>() {
                        @Override
                        public void accept(Wrapper wrapper)
                        {
                            wrapper.updateWrapper();
                        }
                    });
                    sender.sendMessage("[RELOAD] Der Reload war Erfolgreich");
                }
                if(args[0].equalsIgnoreCase("wrapper"))
                {
                    for(Wrapper wrapper : CloudNet.getInstance().getWrappers().values())
                    {
                        if(wrapper.getChannel() != null) wrapper.writeCommand("reload");
                    }
                }
                break;
            default:
                sender.sendMessage(
                        "reload ALL | Läd alle Gruppen, Module, Permissions, etc neu.",
                        "reload CONFIG | Läd alle configs, ServerGruppen etc neu.",
                        "reload WRAPPER | Reloadet alle Wrapper mit \"reload\""
                );
                break;
        }
    }
}