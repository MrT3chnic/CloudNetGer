/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.*;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.advanced.AdvancedServerConfig;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.server.version.ProxyVersion;
import de.dytanic.cloudnet.lib.user.BasicUser;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.map.WrappedMap;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.network.components.WrapperMeta;
import de.dytanic.cloudnetcore.setup.SetupProxyGroup;
import de.dytanic.cloudnetcore.setup.SetupServerGroup;
import de.dytanic.cloudnetcore.setup.SetupWrapper;
import de.dytanic.cloudnetcore.util.defaults.BasicProxyConfig;
import de.dytanic.cloudnetcore.util.defaults.DefaultServerGroup;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class CommandCreate extends Command {

    public CommandCreate()
    {
        super("create", "cloudnet.command.create");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {

        if (args.length > 2)
        {
            if (args[0].equalsIgnoreCase("dispatchCommand"))
            {
                //create dispatchCommand name create
                StringBuilder builder = new StringBuilder();
                for (short i = 2; i < args.length; i++)
                {
                    builder.append(args[i]);
                }

                CloudNet.getInstance().getDbHandlers().getCommandDispatcherDatabase().appendCommand(
                        args[1], builder.substring(0, (builder.substring(0)
                                .endsWith(" ") ? builder.length() - 1 : builder.length())));
                sender.sendMessage("Der Alias wurde Erstellt \"" + args[1] + "\": \"" + builder.substring(0) + "\"");
                return;
            }
        }

        switch (args.length)
        {
            case 2:
                if (args[0].equalsIgnoreCase("proxy") || args[0].equalsIgnoreCase("-p"))
                {
                    if (CloudNet.getInstance().getProxyGroups().containsKey(args[1]))
                    {
                        CloudNet.getInstance().startProxy(CloudNet.getInstance().getProxyGroups().get(args[1]));
                        sender.sendMessage("Versuche ein ProxyServer zu staretn...");
                    } else
                    {
                        sender.sendMessage("Die ProxyGruppe exisitiert nicht!");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("-s"))
                {
                    if (CloudNet.getInstance().getServerGroups().containsKey(args[1]))
                    {
                        CloudNet.getInstance().startGameServer(CloudNet.getInstance().getServerGroups().get(args[1]));
                        sender.sendMessage("Versuche ein GameServer zu staretn");
                    } else
                    {
                        sender.sendMessage("Die ServerGruppe exisitiert nicht!");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("wrapper") && !CloudNet.getInstance().getWrappers().containsKey(args[1]))
                {
                    new SetupWrapper(sender, args[1]);
                    return;
                }
                if (args[0].equalsIgnoreCase("serverGroup"))
                {
                    new SetupServerGroup(sender, args[1]);
                    return;
                }
                if (args[0].equalsIgnoreCase("proxyGroup"))
                {
                    new SetupProxyGroup(sender, args[1]);
                    return;
                }
                break;
            case 3:
                if ((args[0].equalsIgnoreCase("proxy") || args[0].equalsIgnoreCase("-p")) && NetworkUtils.checkIsNumber(args[2]))
                {
                    if (CloudNet.getInstance().getProxyGroups().containsKey(args[1]))
                    {
                        for (short i = 0; i < Integer.parseInt(args[2]); i++)
                        {
                            CloudNet.getInstance().startProxy(CloudNet.getInstance().getProxyGroups().get(args[1]));
                            NetworkUtils.sleepUninterruptedly(2000L);
                        }
                        sender.sendMessage("Versuche ein ProxyServer zu staretn...");
                    } else
                    {
                        sender.sendMessage("Die ServerGruppe exisitiert nicht!");
                    }
                    return;
                }
                if ((args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("-s")) && NetworkUtils.checkIsNumber(args[2]))
                {
                    if (CloudNet.getInstance().getServerGroups().containsKey(args[1]))
                    {
                        for (short i = 0; i < Integer.parseInt(args[2]); i++)
                        {
                            CloudNet.getInstance().startGameServer(CloudNet.getInstance().getServerGroups().get(args[1]));
                            NetworkUtils.sleepUninterruptedly(2000L);
                        }
                        sender.sendMessage("Versuche ein GameServer zu staretn...");
                    } else
                    {
                        sender.sendMessage("Die ServerGruppe exisitiert nicht!");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("user"))
                {
                    if (!CloudNet.getInstance().getUsers().contains(args[1]))
                    {
                        User user = new BasicUser(args[1], args[2], Arrays.asList());
                        CloudNet.getInstance().getUsers().add(user);
                        CloudNet.getInstance().getConfig().save(CloudNet.getInstance().getUsers());
                        sender.sendMessage("Der User wurde Erstellt!");
                    } else
                    {
                        sender.sendMessage("Der User existiert bereits!");
                    }
                }
                break;
            case 4:
                if(args[0].equalsIgnoreCase("cloudserver") || args[0].equalsIgnoreCase("cs"))
                {
                    if(NetworkUtils.checkIsNumber(args[2]))
                    {
                        CloudNet.getInstance().startCloudServer(args[1], Integer.parseInt(args[2]), args[3].equalsIgnoreCase("true"));
                        sender.sendMessage("Versuche ein Cloud server...");
                    }
                    else
                    {
                        sender.sendMessage("Verkehrte Angabe!");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("TEMPLATE"))
                {
                    if (CloudNet.getInstance().getServerGroups().containsKey(args[2]))
                    {
                        if (args[3].equalsIgnoreCase("LOCAL"))
                        {
                            ServerGroup serverGroup = CloudNet.getInstance().getServerGroups().get(args[2]);
                            serverGroup.getTemplates().add(new Template(args[1], TemplateResource.LOCAL, null, new String[]{}, Arrays.asList()));
                            CloudNet.getInstance().getConfig().createGroup(serverGroup);

                            NetworkUtils.addAll(CloudNet.getInstance().getServerGroups(), CloudNet.getInstance().getConfig().getServerGroups(), new Acceptable<ServerGroup>() {
                                @Override
                                public boolean isAccepted(ServerGroup value)
                                {
                                    return true;
                                }
                            });

                            NetworkUtils.addAll(CloudNet.getInstance().getProxyGroups(), CloudNet.getInstance().getConfig().getProxyGroups(), new Acceptable<ProxyGroup>() {
                                @Override
                                public boolean isAccepted(ProxyGroup value)
                                {
                                    return true;
                                }
                            });
                            CloudNet.getInstance().getWrappers().values().forEach(new Consumer<Wrapper>() {
                                @Override
                                public void accept(Wrapper wrapper)
                                {
                                    wrapper.updateWrapper();
                                }
                            });
                            sender.sendMessage("Das Template wurde Erstellt und alle Wrapper wurde reloaded!");
                        }
                        if (args[3].equalsIgnoreCase("MASTER"))
                        {
                            ServerGroup serverGroup = CloudNet.getInstance().getServerGroups().get(args[2]);
                            serverGroup.getTemplates().add(new Template(args[1], TemplateResource.MASTER, null, new String[]{}, Arrays.asList()));
                            CloudNet.getInstance().getConfig().createGroup(serverGroup);

                            NetworkUtils.addAll(CloudNet.getInstance().getServerGroups(), CloudNet.getInstance().getConfig().getServerGroups(), new Acceptable<ServerGroup>() {
                                @Override
                                public boolean isAccepted(ServerGroup value)
                                {
                                    return true;
                                }
                            });

                            NetworkUtils.addAll(CloudNet.getInstance().getProxyGroups(), CloudNet.getInstance().getConfig().getProxyGroups(), new Acceptable<ProxyGroup>() {
                                @Override
                                public boolean isAccepted(ProxyGroup value)
                                {
                                    return true;
                                }
                            });
                            CloudNet.getInstance().getWrappers().values().forEach(new Consumer<Wrapper>() {
                                @Override
                                public void accept(Wrapper wrapper)
                                {
                                    wrapper.updateWrapper();
                                }
                            });
                            sender.sendMessage("Das Template wurde Erstellt und alle Wrapper wurde reloaded!");
                        }
                    } else
                    {
                        sender.sendMessage("Die Servergruppe existiert nicht!");
                    }
                }
                break;
            case 5:
                if (args[0].equalsIgnoreCase("TEMPLATE"))
                {
                    if (CloudNet.getInstance().getServerGroups().containsKey(args[2]))
                    {
                        if (args[3].equalsIgnoreCase("URL"))
                        {
                            ServerGroup serverGroup = CloudNet.getInstance().getServerGroups().get(args[2]);
                            serverGroup.getTemplates().add(new Template(args[1], TemplateResource.URL, args[4], new String[]{("-Dtest=true")}, Arrays.asList()));
                            CloudNet.getInstance().getConfig().createGroup(serverGroup);

                            NetworkUtils.addAll(CloudNet.getInstance().getServerGroups(), CloudNet.getInstance().getConfig().getServerGroups(), new Acceptable<ServerGroup>() {
                                @Override
                                public boolean isAccepted(ServerGroup value)
                                {
                                    return true;
                                }
                            });

                            NetworkUtils.addAll(CloudNet.getInstance().getProxyGroups(), CloudNet.getInstance().getConfig().getProxyGroups(), new Acceptable<ProxyGroup>() {
                                @Override
                                public boolean isAccepted(ProxyGroup value)
                                {
                                    return true;
                                }
                            });
                            CloudNet.getInstance().getWrappers().values().forEach(new Consumer<Wrapper>() {
                                @Override
                                public void accept(Wrapper wrapper)
                                {
                                    wrapper.updateWrapper();
                                }
                            });
                            sender.sendMessage("Das Template wurde und alle Wrapper wurde reloaded!");
                        }
                    } else
                    {
                        sender.sendMessage("Die Servergruppe existiert nicht!");
                    }
                }
                break;
            default:
                sender.sendMessage(
                        "create PROXY <proxyGroup> <count> | Startet einen neuen ProxyServer der Gruppe. <count> ist nicht wichtig",
                        "create SERVER <serverGroup> <count> | Startet einen neuen GameServer der Gruppe. <count> ist nicht wichtig",
                        "create CLOUDSERVER <name> <memory> <priorityStop>",
                        "create USER <name> <password> | Erstellen eins User mit Passowrd",
                        "create PROXYGROUP <name> | Erstellen einer Proxygruppe mit eigenen Einstellungen etc.",
                        "create SERVERGROUP <name> | Erstellen einer Servergruppe mit eigenen Einstellungen etc.",
                        "create DISPATCHCOMMAND <main-command> <command> | Erstellt einen command Alias",
                        "create WRAPPER <name> | Whitelistet einen neuen Wrapper damit er sich Verbinden darf.",
                        "create TEMPLATE <name> <group> LOCAL | Erstellt ein neues (Wrapper locales) template für eine ServerGruppe",
                        "create TEMPLATE <name> <group> MASTER |  Erstellt ein neues ( Master locales) template für eine ServerGruppe",
                        "create TEMPLATE <name> <group> URL <url> | Erstellt ein neues Tempate für eine Servergruppe per URL"
                );
                break;
        }
    }
}