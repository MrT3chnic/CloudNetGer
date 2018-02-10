/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.setup;

import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.map.WrappedMap;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ProxyGroupMode;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.server.version.ProxyVersion;
import de.dytanic.cloudnet.lib.utility.Catcher;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.setup.*;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.util.defaults.BasicProxyConfig;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tareko on 23.10.2017.
 */
@Getter
public class SetupProxyGroup {

    private String name;

    public SetupProxyGroup(CommandSender commandSender, String name)
    {
        this.name = name;

        Setup setup = new Setup()
                .setupCancel(new ISetupCancel() {
                    public void cancel()
                    {
                        commandSender.sendMessage("Setup abgebrochen!");
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

                        ProxyGroupMode proxyGroupMode = null;

                        for(ProxyGroupMode proxyGroup : ProxyGroupMode.values())
                        if(proxyGroup.name().equalsIgnoreCase(data.getString("mode").toUpperCase()))
                        {
                            proxyGroupMode = proxyGroup;
                        }

                        if(proxyGroupMode == null) proxyGroupMode = ProxyGroupMode.DYNAMIC;

                        ProxyGroup proxyGroup = new ProxyGroup(name, wrappers, new Template(
                                "default",
                                TemplateResource.valueOf(data.getString("template")),
                                null,
                                new String[0],
                                new ArrayList<>()
                        ), ProxyVersion.BUNGEECORD, data.getInt("startport"), data.getInt("startup"), data.getInt("memory"), new BasicProxyConfig(),
                                proxyGroupMode, new WrappedMap());

                        CloudNet.getInstance().getConfig().createGroup(proxyGroup);
                        CloudNet.getInstance().getProxyGroups().put(proxyGroup.getName(), proxyGroup);
                        commandSender.sendMessage("Die Proxygruppe " + proxyGroup.getName() + " wurde erstellt!");
                        CloudNet.getInstance().setupProxy(proxyGroup);
                        for (Wrapper wrapper : CloudNet.getInstance().toWrapperInstances(wrappers))
                        {
                            wrapper.updateWrapper();
                        }
                    }
                })
                .request(new SetupRequest("memory", "Wie viel RAM soll die Proxygruppe haben? (in MB angeben)", "Der Ram ist ungültig", SetupResponseType.NUMBER, new Catcher<Boolean, String>() {
                    @Override
                    public Boolean doCatch(String key)
                    {
                        return NetworkUtils.checkIsNumber(key) && Integer.parseInt(key) > 64;
                    }
                }))
                .request(new SetupRequest("startport", "Was ist der start port von der Proxygruppe?", "Der Port ist ungültig", SetupResponseType.NUMBER, new Catcher<Boolean, String>() {
                    @Override
                    public Boolean doCatch(String key)
                    {
                        return NetworkUtils.checkIsNumber(key) && Integer.parseInt(key) > 128 && Integer.parseInt(key) < 70000;
                    }
                }))
                .request(new SetupRequest("startup", "Wie viele Proxy sollen immer Online sein?", "Die Anzahl ist ungültig", SetupResponseType.NUMBER, null))
                .request(new SetupRequest("mode", "Soll die Proxygruppe STATIC oder DYNAMIC sein?", "Der Groupmode ist ungültig", SetupResponseType.STRING, new Catcher<Boolean, String>() {
                    @Override
                    public Boolean doCatch(String key)
                    {
                        return key.equalsIgnoreCase("STATIC") || key.equalsIgnoreCase("DYNAMIC");
                    }
                }))
                .request(new SetupRequest("template", "Wo soll das Template liegen? [\"LOCAL\" für im Wrapper | \"MASTER\" für im Master template]", "Die Angabe ist ungültig", SetupResponseType.STRING, new Catcher<Boolean, String>() {
                    @Override
                    public Boolean doCatch(String key)
                    {
                        return key.equals("MASTER") || key.equals("LOCAL");
                    }
                }))
                .request(new SetupRequest("wrapper", "Auf welchen Wrapper soll die Gruppe sein?", "Die Angabe ist ungültig", SetupResponseType.STRING, new Catcher<Boolean, String>() {
                    @Override
                    public Boolean doCatch(String key)
                    {
                        java.util.List<String> wrappers = (List<String>) CollectionWrapper.toCollection(key, ",");
                        if (wrappers.size() == 0) return false;
                        for (short i = 0; i < wrappers.size(); i++)
                        {
                            if (!CloudNet.getInstance().getWrappers().containsKey(wrappers.get(i)))
                            {
                                wrappers.remove(wrappers.get(i));
                            }
                        }
                        if (wrappers.size() == 0) return false;
                        else return true;
                    }
                }));
        setup.start(CloudNet.getLogger().getReader());
    }
}