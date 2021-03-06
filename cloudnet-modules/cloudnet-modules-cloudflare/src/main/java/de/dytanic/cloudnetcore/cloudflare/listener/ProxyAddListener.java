/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.cloudflare.listener;

import de.dytanic.cloudnet.cloudflare.CloudFlareService;
import de.dytanic.cloudnet.event.IEventListener;
import de.dytanic.cloudnetcore.api.event.server.ProxyAddEvent;
import de.dytanic.cloudnetcore.cloudflare.CloudFlareModule;

/**
 * Created by Tareko on 20.10.2017.
 */
public class ProxyAddListener implements IEventListener<ProxyAddEvent>{

    @Override
    public void onCall(ProxyAddEvent event)
    {
        CloudFlareService.getInstance().addProxy(event.getProxyServer().getProcessMeta(), CloudFlareModule.getInstance().getCloudFlareDatabase());
    }
}