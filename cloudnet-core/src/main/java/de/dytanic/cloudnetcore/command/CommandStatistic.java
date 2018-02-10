/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.database.StatisticManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by Tareko on 21.08.2017.
 */
public class CommandStatistic extends Command {

    public CommandStatistic()
    {
        super("statistic", "cloudnet.command.statistic");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        Document document = StatisticManager.getInstance().getStatistics();
        sender.sendMessage(
                "CloudNet2 Statistiken:",
                " ",
                "Wie oft die Cloud schon gestartet wurde: " + StatisticManager.getInstance().getStatistics().getInt("cloudStartup"),
                "Wie lange die Cloud schon an ist: " + TimeUnit.MILLISECONDS.toMinutes(document.getInt("cloudOnlineTime")) + "Minuten",
                "Wie oft sich der/die Wrapper schon Verbunden haben: " + document.getInt("wrapperConnections"),
                "Wie viele Server höchsten schon Online waren: " + document.getInt("highestServerOnlineCount"),
                "Wie viele Server schon gestartet wurden: " + document.getLong("startedServers"),
                " ",
                "Spieler Statistiken:",
                " ",
                "Registierte Spieler: " + CloudNet.getInstance().getDbHandlers().getPlayerDatabase().getDatabase().size(),
                "Höchste Speiler Anzahl: " + document.getInt("highestPlayerOnline"),
                "Logins: " + document.getInt("playerLogin"),
                "Wie viele Commands schon ausgeführt wurden: " + document.getInt("playerCommandExecutions"),
                " "
        );
    }
}