/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.ConnectableAddress;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.user.BasicUser;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.Catcher;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.MapWrapper;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.util.WebServerConfig;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.network.components.WrapperMeta;
import de.dytanic.cloudnetcore.util.defaults.BungeeGroup;
import de.dytanic.cloudnetcore.util.defaults.LobbyGroup;
import jline.console.ConsoleReader;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Tareko on 16.09.2017.
 */
@Getter
public class CloudConfig {

    private static final ConfigurationProvider CONFIGURATION_PROVIDER = ConfigurationProvider.getProvider(YamlConfiguration.class);

    private final Path configPath = Paths.get("config.yml"), servicePath = Paths.get("services.json"), usersPath = Paths.get("users.json");

    private Collection<ConnectableAddress> addresses;

    private boolean autoUpdate, notifyService, cloudDynamicServices, cloudDevServices;

    private String formatSplitter, wrapperKey;

    private WebServerConfig webServerConfig;

    private List<WrapperMeta> wrappers;

    private Configuration config;

    private Document serviceDocument, userDocument;

    private List<String> disabledModules, cloudServerWrapperList;

    private Map<String, Object> networkProperties;

    public CloudConfig(ConsoleReader consoleReader) throws Exception
    {
        if (!Files.exists(Paths.get("groups"))) Files.createDirectory(Paths.get("groups"));
        if (!Files.exists(Paths.get("local"))) Files.createDirectory(Paths.get("local"));
        if (!Files.exists(Paths.get("local/libs"))) Files.createDirectory(Paths.get("local/libs"));
        if (!Files.exists(Paths.get("local/templates"))) Files.createDirectory(Paths.get("local/templates"));
        if (!Files.exists(Paths.get("local/plugins"))) Files.createDirectory(Paths.get("local/plugins"));
        if (!Files.exists(Paths.get("local/cache"))) Files.createDirectory(Paths.get("local/cache"));
        if (!Files.exists(Paths.get("local/servers"))) Files.createDirectory(Paths.get("local/servers"));
        if (!Files.exists(Paths.get("local/servers/TestServer")))
            Files.createDirectory(Paths.get("local/servers/TestServer"));
        if (!Files.exists(Paths.get("local/servers/TestServer/plugins")))
            Files.createDirectory(Paths.get("local/servers/TestServer/plugins"));

        NetworkUtils.writeWrapperKey();

        defaultInit(consoleReader);
        defaultInitDoc(consoleReader);
        defaultInitUsers(consoleReader);
        load();
    }

    private void defaultInit(ConsoleReader consoleReader) throws Exception
    {
        if (Files.exists(configPath)) return;

        String hostName = NetworkUtils.getHostName();
        if (hostName.equals("127.0.0.1") || hostName.equals("127.0.1.1") || hostName.split("\\.").length != 4)
        {
            String input;
            System.out.println("Your IP address where located is 127.0.0.1 please write your service ip");
            while ((input = consoleReader.readLine()) != null)
            {
                if ((input.equals("127.0.0.1") || input.equals("127.0.1.1")) || input.split("\\.").length != 4)
                {
                    System.out.println("Please write your real ip address :)");
                    continue;
                }
                hostName = input;
                break;
            }
        }

        Configuration configuration = new Configuration();

        configuration.set("general.auto-update", false);
        configuration.set("general.dynamicservices", false);
        configuration.set("general.server-name-splitter", "-");
        configuration.set("general.notify-service", true);
        configuration.set("general.disabled-modules", new ArrayList<>());
        configuration.set("general.cloudGameServer-wrapperList", Arrays.asList("Wrapper-1"));

        configuration.set("server.hostaddress", hostName);
        configuration.set("server.ports", Arrays.asList(1410));
        configuration.set("server.webservice.hostaddress", hostName);
        configuration.set("server.webservice.port", 1420);

        configuration.set("cloudnet-statistics.enabled", true);
        configuration.set("cloudnet-statistics.uuid", UUID.randomUUID().toString());

        configuration.set("networkproperties.test", true);

        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(configPath), StandardCharsets.UTF_8))
        {
            CONFIGURATION_PROVIDER.save(configuration, outputStreamWriter);
        }
    }

    private void defaultInitDoc(ConsoleReader consoleReader) throws Exception
    {
        if (Files.exists(servicePath)) return;

        String hostName = NetworkUtils.getHostName();
        if (hostName.equals("127.0.0.1") || hostName.equalsIgnoreCase("127.0.1.1") || hostName.split("\\.").length != 4)
        {
            String input;
            System.out.println("Bitte schreibe die IP Adresse vom Server:");
            while ((input = consoleReader.readLine()) != null)
            {
                if ((input.equals("127.0.0.1") || input.equalsIgnoreCase("127.0.1.1") || input.split("\\.").length != 4))
                {
                    System.out.println("Bitte schreibe die Richtige IP Adresse :)");
                    continue;
                }

                hostName = input;
                break;
            }
        }
        new Document("wrapper", Arrays.asList(new WrapperMeta("Wrapper-1", hostName, "admin")))
                //.append("serverGroups", Arrays.asList(new LobbyGroup()))
                .append("proxyGroups", Arrays.asList(new BungeeGroup()))
                .saveAsConfig(servicePath);

        new Document("group", new LobbyGroup()).saveAsConfig(Paths.get("groups/Lobby.json"));
    }

    private void defaultInitUsers(ConsoleReader consoleReader)
    {
        if (Files.exists(usersPath)) return;

        String password = NetworkUtils.randomString(8);
        System.out.println("\"admin\" Password: " + password);
        System.out.println(" ");
        new Document().append("users", Arrays.asList(new BasicUser("admin", password, Arrays.asList("*")))).saveAsConfig(usersPath);
    }

    public CloudConfig load() throws Exception
    {

        try (InputStream inputStream = Files.newInputStream(configPath); InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        {
            Configuration configuration = CONFIGURATION_PROVIDER.load(inputStreamReader);
            this.config = configuration;

            String host = configuration.getString("server.hostaddress");

            Collection<ConnectableAddress> addresses = new ArrayList<>();
            for (int value : configuration.getIntList("server.ports"))
            {
                addresses.add(new ConnectableAddress(host, value));
            }
            this.addresses = addresses;

            this.wrapperKey = NetworkUtils.readWrapperKey();
            this.autoUpdate = configuration.getBoolean("general.auto-update");
            this.notifyService = configuration.getBoolean("general.notify-service");
            this.cloudDevServices = configuration.getBoolean("general.devservices");
            this.cloudDynamicServices = configuration.getBoolean("general.dynamicservices");
            this.webServerConfig = new WebServerConfig(true,
                    configuration.getString("server.webservice.hostaddress"),
                    configuration.getInt("server.webservice.port")
            );
            this.formatSplitter = configuration.getString("general.server-name-splitter");
            this.networkProperties = configuration.getSection("networkproperties").self;
            //        configuration.set("general.disabled-modules", new ArrayList<>());
            if (!configuration.getSection("general").self.containsKey("disabled-modules"))
            {
                configuration.set("general.disabled-modules", new ArrayList<>());

                try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(configPath), StandardCharsets.UTF_8))
                {
                    CONFIGURATION_PROVIDER.save(configuration, outputStreamWriter);
                }
            }

            if (!configuration.getSection("general").self.containsKey("cloudGameServer-wrapperList"))
            {
                configuration.set("general.cloudGameServer-wrapperList", Arrays.asList("Wrapper-1"));

                try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(configPath), StandardCharsets.UTF_8))
                {
                    CONFIGURATION_PROVIDER.save(configuration, outputStreamWriter);
                }
            }

            this.disabledModules = configuration.getStringList("general.disabled-modules");
            this.cloudServerWrapperList = configuration.getStringList("general.cloudGameServer-wrapperList");
        }

        this.serviceDocument = Document.loadDocument(servicePath);

        this.wrappers = this.serviceDocument.getObject("wrapper", new TypeToken<List<WrapperMeta>>() {
        }.getType());

        this.userDocument = Document.loadDocument(usersPath);

        /* ============================================================== */
        return this;
    }

    public void createWrapper(WrapperMeta wrapperMeta)
    {
        Collection<WrapperMeta> wrapperMetas = this.serviceDocument.getObject("wrapper", new TypeToken<Collection<WrapperMeta>>() {
        }.getType());
        WrapperMeta is = CollectionWrapper.filter(wrapperMetas, new Acceptable<WrapperMeta>() {
            @Override
            public boolean isAccepted(WrapperMeta wrapperMeta_)
            {
                return wrapperMeta_.getId().equalsIgnoreCase(wrapperMeta.getId());
            }
        });
        if (is != null) wrapperMetas.remove(is);

        wrapperMetas.add(wrapperMeta);
        this.serviceDocument.append("wrapper", wrapperMetas).saveAsConfig(servicePath);
        CloudNet.getInstance().getWrappers().put(wrapperMeta.getId(), new Wrapper(wrapperMeta));
    }

    public Collection<User> getUsers()
    {
        if (this.userDocument == null) return null;
        return userDocument.getObject("users", new TypeToken<Collection<User>>() {
        }.getType());
    }

    public CloudConfig save(Collection<User> users)
    {
        if (userDocument != null)
            userDocument.append("users", users).saveAsConfig(usersPath);
        return this;
    }

    public void createGroup(@NonNull ServerGroup serverGroup)
    {
        /*
        Collection<ServerGroup> groups = this.serviceDocument.getObject("serverGroups", new TypeToken<Collection<ServerGroup>>() {
        }.getType());
        CollectionWrapper.checkAndRemove(groups, new Acceptable<ServerGroup>() {
            @Override
            public boolean isAccepted(ServerGroup value)
            {
                return value.getName().equals(serverGroup.getName());
            }
        });

        groups.add(serverGroup);
        this.serviceDocument.append("serverGroups", groups).saveAsConfig(servicePath);
        */

        new Document("group", serverGroup).saveAsConfig(Paths.get("groups/" + serverGroup.getName() + ".json"));

    }

    public void createGroup(@NonNull ProxyGroup serverGroup)
    {
        Collection<ProxyGroup> groups = this.serviceDocument.getObject("proxyGroups", new TypeToken<Collection<ProxyGroup>>() {
        }.getType());
        CollectionWrapper.checkAndRemove(groups, new Acceptable<ProxyGroup>() {
            @Override
            public boolean isAccepted(ProxyGroup value)
            {
                return value.getName().equals(serverGroup.getName());
            }
        });

        groups.add(serverGroup);
        this.serviceDocument.append("proxyGroups", groups).saveAsConfig(servicePath);
    }

    public void deleteGroup(ServerGroup serverGroup)
    {
        /*
        Collection<ServerGroup> groups = this.serviceDocument.getObject("serverGroups", new TypeToken<Collection<ServerGroup>>() {
        }.getType());
        CollectionWrapper.checkAndRemove(groups, new Acceptable<ServerGroup>() {
            @Override
            public boolean isAccepted(ServerGroup value)
            {
                return value.getName().equals(serverGroup.getName());
            }
        });

        this.serviceDocument.append("serverGroups", groups).saveAsConfig(servicePath);
        */

        new File("groups/" + serverGroup.getName() + ".json").delete();
    }

    public void deleteGroup(ProxyGroup proxyGroup)
    {
        Collection<ProxyGroup> groups = this.serviceDocument.getObject("proxyGroups", new TypeToken<Collection<ProxyGroup>>() {
        }.getType());
        CollectionWrapper.checkAndRemove(groups, new Acceptable<ProxyGroup>() {
            @Override
            public boolean isAccepted(ProxyGroup value)
            {
                return value.getName().equals(proxyGroup.getName());
            }
        });
        this.serviceDocument.append("proxyGroups", groups).saveAsConfig(servicePath);
    }

    public java.util.Map<String, ServerGroup> getServerGroups()
    {
        /*
        Collection<ServerGroup> collection = serviceDocument.getObject("serverGroups", new TypeToken<Collection<ServerGroup>>() {
        }.getType());
        return MapWrapper.collectionCatcherHashMap(collection, new Catcher<String, ServerGroup>() {
            @Override
            public String doCatch(ServerGroup key)
            {
                return key.getName();
            }
        });
        */

        Map<String, ServerGroup> groups = new ConcurrentHashMap<>();

        if (serviceDocument.contains("serverGroups"))
        {

            Collection<ServerGroup> collection = serviceDocument.getObject("serverGroups", new TypeToken<Collection<ServerGroup>>() {
            }.getType());

            for (ServerGroup serverGroup : collection)
                createGroup(serverGroup);

            serviceDocument.remove("serverGroups");
            serviceDocument.saveAsConfig(servicePath);
        }

        File groupsDirectory = new File("groups");
        Document entry;

        if(groupsDirectory.isDirectory())
        for(File file : groupsDirectory.listFiles())
        {
            if(file.getName().endsWith(".json"))
            try
            {
                entry = Document.$loadDocument(file);
                ServerGroup serverGroup = entry.getObject("group", ServerGroup.TYPE);
                groups.put(serverGroup.getName(), serverGroup);
            } catch (Throwable ex) {
                System.out.println("Die Servergroup datei konnte nicht geladen werden [" + file.getName() + "]");
            }
        }

        return groups;
    }

    public Map<String, ProxyGroup> getProxyGroups()
    {
        Collection<ProxyGroup> collection = serviceDocument.getObject("proxyGroups", new TypeToken<Collection<ProxyGroup>>() {
        }.getType());

        return MapWrapper.collectionCatcherHashMap(collection, new Catcher<String, ProxyGroup>() {
            @Override
            public String doCatch(ProxyGroup key)
            {
                return key.getName();
            }
        });
    }

}