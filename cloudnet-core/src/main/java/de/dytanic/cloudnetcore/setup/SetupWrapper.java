/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.setup;

import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.utility.Catcher;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.setup.*;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.network.components.WrapperMeta;
import lombok.Getter;

import java.util.function.Consumer;

/**
 * Created by Tareko on 21.10.2017.
 */
@Getter
public class SetupWrapper {

    private String name;

    public SetupWrapper(CommandSender commandSender, String name)
    {
        this.name = name;

        Setup setup = new Setup().setupCancel(new ISetupCancel() {
            @Override
            public void cancel()
            {
                System.out.println("Setup wurde abgebrochen");
            }
        }).setupComplete(new ISetupComplete() {
            @Override
            public void complete(Document data)
            {
                String host = data.getString("address");
                String user = data.getString("user");

                WrapperMeta wrapperMeta = new WrapperMeta(name, host, user);
                CloudNet.getInstance().getConfig().createWrapper(wrapperMeta);
                commandSender.sendMessage("Der Wrapper [" + wrapperMeta.getId() + "] wurde registiert in CloudNet");
            }
        });
        Consumer<SetupRequest> request = setup::request;
        request.accept(new SetupRequest("address", "Was ist die IP vom Wrapper?", "Die IP adresse ist ungültig!", SetupResponseType.STRING, new Catcher<Boolean, String>() {
            @Override
            public Boolean doCatch(String key)
            {
                return key.split("\\.").length == 4 && !key.equalsIgnoreCase("127.0.0.1");
            }
        }));
        request.accept(new SetupRequest("user", "Was ist der Name vom Wrapper", "Der Name ist ungültig!", SetupResponseType.STRING, new Catcher<Boolean, String>() {
            @Override
            public Boolean doCatch(String key)
            {
                return CloudNet.getInstance().getUser(key) != null;
            }
        }));
        setup.start(CloudNet.getLogger().getReader());
    }

}