/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnetcore.CloudNet;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;

/**
 * Created by Tareko on 26.07.2017.
 */
public class CommandHelp extends Command {

    public CommandHelp()
    {
        super("help", "cloudnet.command.help");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {

        StringBuilder proxyGrouPBuilder = new StringBuilder();
        for(String group : CloudNet.getInstance().getProxyGroups().keySet())
            proxyGrouPBuilder.append(group).append(", ");

        StringBuilder serverGrouPBuilder = new StringBuilder();
        for(String group : CloudNet.getInstance().getServerGroups().keySet())
            serverGrouPBuilder.append(group).append(", ");

        sender.sendMessage(
                "",
                "create | Creates new Wrapper, ServerGroup, PermissionGroup, ProxyGroup or custom server",
                "stop | Stopt den Master",
                "clear | Leert die Console",
                "reload | Reloadet die Config und Module",
                "shutdown | Stoppt alle Wrapper, Proxies, Servers or Proxy/Server gruppen",
                "perms | Damit kannst du das Permission-System steuern",
                "screen | Zeigt den Log vom Aktiven an",
                "cmd | Zum ausführen von commands auf Proxy/Server",
                "statistic | Zeigt die Statistiken an",
                "modules | Zeigt alle Module und Versionen mit Author",
                "clearcache | Löscht den plugin und Template chache von Allen Wrappern",
                "list | Zeigt alle Informationen zu dem Netzwerk",
                "install | Zum installieren von Modulen und Templates",
                "installplugin | Zum installieren von Plugin auf ein Server",
                "copy | Kopiert das den Server in das Template",
                "delete | Zum löschen von Servergruppen und custom Server",
                "log | Zum erstellen eines Weblogs",
                "version | Zeigt die Version an",
                "info | Zeigt informationen von Proxy/Server udn Wrapper an",
                "Server groups:",
                serverGrouPBuilder.substring(0),
                "Proxy groups: ",
                proxyGrouPBuilder.substring(0),
                "The Cloud uses " + (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L) + "/" + (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / 1048576L) + "MB",
                "CPU on this instance " + new DecimalFormat("##.##").format(NetworkUtils.internalCpuUsage()) + "/100 %",
                " "
        );
    }
}