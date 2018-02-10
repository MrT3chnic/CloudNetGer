/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.util;

import de.dytanic.cloudnet.lib.utility.document.Document;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Tareko on 20.08.2017.
 */
public class MessageConfig {

    private Path path = Paths.get("local/ingame_messages.json");

    public MessageConfig()
    {
        if (!Files.exists(path))
        {
            new Document()
                    .append("prefix", "§bCloud §8|§7 ")
                    .append("kick-maintenance", "§cDas Netzwerk befindet sich gerade in den Wartungen")
                    .append("full-join", "§cDas Netzwerk ist voll, schau später mal vorbei.")
                    .append("hubCommandNoServerFound", "§cEs wurde kein Server gefunden, Bitte warte kurz.")
                    .append("joinpower-deny", "§cDu kannst da nicht rauf. Dazu hast du zuwenig rechte")
                    .append("server-group-maintenance-kick", "§cDie Gruppe befindet sich monetan in den Wartungen!")
                    .append("mob-selector-maintenance-message", "§cDie Gruppe befindet sich monetan in den Wartungen, bitte warte, bevor du Spielen kannt!")
                    .append("notify-message-server-add", "§8Der Server §a%server% §8startet jetzt.....")
                    .append("notify-message-server-remove", "§8Der Server §c%server% §8stopt jetzt!")
                    .append("hub-already", "§cDu befindest dich bereits auf einem Lobby-Server.")
                    .saveAsConfig(path);
        }
    }

    public Document load()
    {

        Document document = Document.loadDocument(path);

        return document;
    }
}