/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.setup;

import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import de.dytanic.cloudnet.lib.server.ServerGroupType;
import de.dytanic.cloudnet.lib.server.advanced.AdvancedServerConfig;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.utility.Catcher;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.setup.*;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tareko on 21.10.2017.
 */
@Getter
public class SetupServerGroup {

    private String name;

    public SetupServerGroup(CommandSender commandSender, String name)
    {
        this.name = name;

        Setup setup = new Setup()
                .setupCancel(new ISetupCancel() {
                    @Override
                    public void cancel()
                    {
                        System.out.println("Setup abgebrochen!");
                    }
                })
                .setupComplete(new ISetupComplete() {
                    @Override
                    public void complete(Document data)
                    {
                        java.util.List<String> wrappers = (List<String>) CollectionWrapper.toCollection(data.getString("wrapper"), ",");
                        if (wrappers.size() == 0) return;
                        for (short i = 0; i < wrappers.size(); i++)
                        {
                            if (!CloudNet.getInstance().getWrappers().containsKey(wrappers.get(i)))
                            {
                                wrappers.remove(wrappers.get(i));
                            }
                        }
                        if (wrappers.size() == 0) return;

                        ServerGroupMode serverGroupMode = ServerGroupMode.valueOf(data.getString("mode").toUpperCase());

                        ServerGroupType serverGroupType = null;

                        for(ServerGroupType serverGroup : ServerGroupType.values())
                        {
                            if(serverGroup.name().equalsIgnoreCase(data.getString("type").toUpperCase()))
                            {
                                serverGroupType = serverGroup;
                            }
                        }
                        if(serverGroupType == null) serverGroupType = ServerGroupType.BUKKIT;

                        ServerGroup serverGroup = new ServerGroup(
                                name,
                                wrappers,
                                serverGroupMode.equals(ServerGroupMode.LOBBY),
                                data.getInt("memory"),
                                data.getInt("memory"),
                                0,
                                true,
                                data.getInt("startup"),
                                data.getInt("onlineGlobal"),
                                data.getInt("onlineGroup"),
                                180,
                                100,
                                100,
                                data.getInt("percent"),
                                serverGroupType,
                                serverGroupMode,
                                Arrays.asList(new Template(
                                        "default",
                                        TemplateResource.valueOf(data.getString("template")),
                                        null,
                                        new String[0],
                                        new ArrayList<>()
                                )),
                        new AdvancedServerConfig(false, false, false, !serverGroupMode.equals(ServerGroupMode.STATIC)));
                        CloudNet.getInstance().getConfig().createGroup(serverGroup);
                        CloudNet.getInstance().getServerGroups().put(serverGroup.getName(), serverGroup);
                        CloudNet.getInstance().setupGroup(serverGroup);
                        for (Wrapper wrapper : CloudNet.getInstance().toWrapperInstances(wrappers))
                        {
                            wrapper.updateWrapper();
                        }
                        commandSender.sendMessage("Die Servergruppe " + serverGroup.getName() + " wurde Erstellt!");
                    }
                })
                .request(new SetupRequest("memory", "Wie viel RAM soll die Servergruppe haben? (in MB angeben)", "Der Ram ist ungültig", SetupResponseType.NUMBER, new Catcher<Boolean, String>() {
                    @Override
                    public Boolean doCatch(String key)
                    {
                        return NetworkUtils.checkIsNumber(key) && Integer.parseInt(key) > 64;
                    }
                }))
                .request(new SetupRequest("startup", "Wie viele Server sollen immer Online sein?", "Die Anzahl ist ungültig", SetupResponseType.NUMBER, new Catcher<Boolean, String>() {
                    @Override
                    public Boolean doCatch(String key)
                    {
                        return true;
                    }
                }))
                .request(new SetupRequest("percent", "Wie viele Spieler müssen auf dem Server sein, bis ein neuer Server gestaret wurde? (Anzahl in Prozent)", "Die Prozent zahl ist ungültig", SetupResponseType.NUMBER, new Catcher<Boolean, String>() {
                    @Override
                    public Boolean doCatch(String key)
                    {
                        return NetworkUtils.checkIsNumber(key) && Integer.parseInt(key) <= 100;
                    }
                }))
                .request(new SetupRequest("mode", "Welchen Groupmode soll die Gruppe haben? [STATIC, STATIC_LOBBY, LOBBY, DYNAMIC]", "Der Gruopmode ist ungültig", SetupResponseType.STRING, new Catcher<Boolean, String>() {
                    @Override
                    public Boolean doCatch(String key)
                    {
                        return key.equalsIgnoreCase("STATIC") || key.equalsIgnoreCase("STATIC_LOBBY") || key.equalsIgnoreCase("LOBBY") || key.equalsIgnoreCase("DYNAMIC");
                    }
                }))
                .request(new SetupRequest("type", "Welche Servergruppe soll genutzt werden? [BUKKIT, CAULDRON, GLOWSTONE]", "Specified group type is invalid", SetupResponseType.STRING, new Catcher<Boolean, String>() {
                    @Override
                    public Boolean doCatch(String key)
                    {
                        return key.equals("BUKKIT") || key.equals("GLOWSTONE") || key.equals("CAULDRON");
                    }
                }))
                .request(new SetupRequest("template", "Wo soll das Template liegen? [\"LOCAL\" für im Wrapper | \"MASTER\" für im Master template]", "Die Angabe ist ungültig", SetupResponseType.STRING, new Catcher<Boolean, String>() {
                    @Override
                    public Boolean doCatch(String key)
                    {
                        return key.equals("MASTER") || key.equals("LOCAL");
                    }
                }))
                .request(new SetupRequest("onlineGroup", "Wie viele Server sollen ansein wenn 100 Spieler sich in der Gruppe befinden?", "Die Zahl ist ungültig", SetupResponseType.NUMBER, null))
                .request(new SetupRequest("onlineGlobal", "Wie viele Server sollen ansein wenn 100 Spieler sich auf dem Netzwerk befinden?", "Die Zahl ist ungültig", SetupResponseType.NUMBER, null))

                .request(new SetupRequest("wrapper", "Auf welchen Wrapper soll die Gruppe sein?", "Die Angabe ist ungültig", SetupResponseType.STRING, new Catcher<Boolean, String>() {
                    @Override
                    public Boolean doCatch(String key)
                    {
                        java.util.List<String> wrappers = (List<String>) CollectionWrapper.toCollection(key, ",");
                        if (wrappers.size() == 0) return false;
                        for (short i = 0; i < wrappers.size(); i++)
                        {
                            if (!CloudNet.getInstance().getWrappers().containsKey(wrappers.get(i)))
                                wrappers.remove(wrappers.get(i));
                        }

                        if (wrappers.size() == 0) return false; else return true;
                    }
                }))
                ;
        setup.start(CloudNet.getLogger().getReader());
    }
}