/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.permissions.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.GroupEntityData;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.threading.Runnabled;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnet.lib.player.permission.DefaultPermissionGroup;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutUpdateOfflinePlayer;
import de.dytanic.cloudnetcore.permissions.PermissionModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tareko on 20.08.2017.
 */
public class CommandPermissions extends Command {

    public CommandPermissions()
    {
        super("perms", "cloudnet.command.permissions", "permissions", "cperms");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        PermissionPool permissionPool = PermissionModule.getInstance().getPermissionPool();
        if(args.length > 0)
        {
            if(args[0].equalsIgnoreCase("group"))
            {
                if(args.length == 1)
                {
                    sender.sendMessage(" ", "Permission gruppen:");
                    for(PermissionGroup permissionGroup : PermissionModule.getInstance().getPermissionPool().getGroups().values())
                    {
                        sender.sendMessage(permissionGroup.getName() + " [" + permissionGroup.getJoinPower() + "] implements " + permissionGroup.getImplementGroups());
                    }
                    sender.sendMessage(" ");
                    return;
                }
                if(args.length == 2)
                {
                    if(permissionPool.getGroups().containsKey(args[1]))
                    {
                        sender.sendMessage(" ");
                        PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                        sender.sendMessage("Name: " + permissionGroup.getName());
                        sender.sendMessage("Implementations: " + permissionGroup.getImplementGroups());
                        sender.sendMessage("TagId: " + permissionGroup.getTagId());
                        sender.sendMessage("JoinPower: " + permissionGroup.getJoinPower());
                        for(Map.Entry<String, Boolean> x : permissionGroup.getPermissions().entrySet())
                        sender.sendMessage("- " + x.getKey() + ":" + x.getValue());
                        sender.sendMessage(" ");
                        sender.sendMessage("Permissions für Server groups:");
                        for(Map.Entry<String, List<String>> x : permissionGroup.getServerGroupPermissions().entrySet())
                        {
                            sender.sendMessage(x.getKey() + ":");
                            CollectionWrapper.iterator(x.getValue(), new Runnabled<String>() {
                                @Override
                                public void run(String obj)
                                {
                                    sender.sendMessage("- " + obj);
                                }
                            });
                        }
                        sender.sendMessage(" ");
                    }
                    else
                    {
                        sender.sendMessage("Die Permission Gruppe existiert nicht");
                    }
                }

                if(args.length == 4)
                {
                    if(args[2].equalsIgnoreCase("setDisplay"))
                    {
                        if(permissionPool.getGroups().containsKey(args[1]))
                        {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.setDisplay(args[3].replace("_", " "));
                            PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                            CloudNet.getInstance().getNetworkManager().reload();
                            CloudNet.getInstance().getNetworkManager().updateAll0();
                            sender.sendMessage("Du setzt den Display für die Permission gruppe " + permissionGroup.getName() + " zu \"" + permissionGroup.getDisplay() + "\"");
                        }
                        else
                        {
                            sender.sendMessage("Die Permission Gruppe existiert nicht");
                        }
                    }

                    if(args[2].equalsIgnoreCase("setPrefix"))
                    {
                        if(permissionPool.getGroups().containsKey(args[1]))
                        {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.setPrefix(args[3].replace("_", " "));
                            PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                            CloudNet.getInstance().getNetworkManager().reload();
                            CloudNet.getInstance().getNetworkManager().updateAll0();
                            sender.sendMessage("Du setzt den Prefix für die Permission gruppe " + permissionGroup.getName() + " zu \"" + permissionGroup.getPrefix() + "\"");
                        }
                        else
                        {
                            sender.sendMessage("Die Permission Gruppe existiert nicht");
                        }
                    }

                    if(args[2].equalsIgnoreCase("setSuffix"))
                    {
                        if(permissionPool.getGroups().containsKey(args[1]))
                        {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.setSuffix(args[3].replace("_", " "));
                            PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                            CloudNet.getInstance().getNetworkManager().reload();
                            CloudNet.getInstance().getNetworkManager().updateAll0();
                            sender.sendMessage("Du setzt den Suffix für die Permission gruppe " + permissionGroup.getName() + " zu \"" + permissionGroup.getSuffix() + "\"");
                        }
                        else
                        {
                            sender.sendMessage("Die Permission Gruppe existiert nicht");
                        }
                    }

                    if(args[2].equalsIgnoreCase("setDefault"))
                    {
                        if(permissionPool.getGroups().containsKey(args[1]))
                        {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.setDefaultGroup(args[3].equalsIgnoreCase("true"));
                            PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                            CloudNet.getInstance().getNetworkManager().reload();
                            CloudNet.getInstance().getNetworkManager().updateAll0();
                            sender.sendMessage("Du setzt den default attribut für die Permission gruppe " + permissionGroup.getName() + " to \"" + permissionGroup.isDefaultGroup() + "\"");
                        }
                        else
                        {
                            sender.sendMessage("Die Permission Gruppe existiert nicht");
                        }
                    }

                    if(args[2].equalsIgnoreCase("setJoinPower"))
                    {
                        if(permissionPool.getGroups().containsKey(args[1]) && NetworkUtils.checkIsNumber(args[3]))
                        {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.setJoinPower(Integer.parseInt(args[3]));
                            PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                            CloudNet.getInstance().getNetworkManager().reload();
                            CloudNet.getInstance().getNetworkManager().updateAll0();
                            sender.sendMessage("Du setzt die JoinPower für die Permission gruppe " + permissionGroup.getName() + " to \"" + permissionGroup.getJoinPower() + "\"");
                        }
                        else
                        {
                            sender.sendMessage("Die Permission Gruppe existiert nicht");
                        }
                    }

                    if(args[2].equalsIgnoreCase("setTagId"))
                    {
                        if(permissionPool.getGroups().containsKey(args[1]) && NetworkUtils.checkIsNumber(args[3]))
                        {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.setTagId(Integer.parseInt(args[3]));
                            PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                            CloudNet.getInstance().getNetworkManager().reload();
                            CloudNet.getInstance().getNetworkManager().updateAll0();
                            sender.sendMessage("Du setzt die TagId für die Permission gruppe " + permissionGroup.getName() + " to \"" + permissionGroup.getTagId() + "\"");
                        }
                        else
                        {
                            sender.sendMessage("Die Permission Gruppe existiert nicht");
                        }
                    }
                    return;
                }

                if(args.length == 5)
                {
                    if(args[2].equalsIgnoreCase("add") && args[3].equalsIgnoreCase("permission"))
                    {
                        if(permissionPool.getGroups().containsKey(args[1]))
                        {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.getPermissions().put(args[4].replaceFirst("-", ""), !args[4].startsWith("-"));
                            PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                            CloudNet.getInstance().getNetworkManager().reload();
                            CloudNet.getInstance().getNetworkManager().updateAll0();
                            sender.sendMessage("Du addest eine Permission " + args[4] + " zu der Permissingruppe \"" + permissionGroup.getName() + "\"");
                        }
                        else
                        {
                            sender.sendMessage("Die Permission Gruppe existiert nicht");
                        }
                    }

                    if(args[2].equalsIgnoreCase("remove") && args[3].equalsIgnoreCase("permission"))
                    {
                        if(permissionPool.getGroups().containsKey(args[1]))
                        {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.getPermissions().remove(args[4]);
                            PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                            CloudNet.getInstance().getNetworkManager().reload();
                            CloudNet.getInstance().getNetworkManager().updateAll0();
                            sender.sendMessage("Du Entfernst eine Permission " + args[4] + " von der Permission gruppe \"" + permissionGroup.getName() + "\"");
                        }
                        else
                        {
                            sender.sendMessage("Die Permission Gruppe existiert nicht");
                        }
                    }
                }

                if(args.length == 6)
                {
                    if(args[2].equalsIgnoreCase("add") && args[3].equalsIgnoreCase("permission"))
                    {
                        if(permissionPool.getGroups().containsKey(args[1]))
                        {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);

                            if(!permissionGroup.getServerGroupPermissions().containsKey(args[5]))
                            {
                                permissionGroup.getServerGroupPermissions().put(args[5], new ArrayList<>());
                            }

                            permissionGroup.getServerGroupPermissions().get(args[5]).add(args[4].replaceFirst("-", ""));
                            PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                            CloudNet.getInstance().getNetworkManager().reload();
                            CloudNet.getInstance().getNetworkManager().updateAll0();
                            sender.sendMessage("Du addest eine Permission " + args[4] + " zu der Permissingruppe \"" + permissionGroup.getName() + "\" bei der ServerGruppe " + args[5]);
                        }
                        else
                        {
                            sender.sendMessage("Die Permission Gruppe existiert nicht");
                        }
                    }

                    if(args[2].equalsIgnoreCase("remove") && args[3].equalsIgnoreCase("permission"))
                    {
                        if(permissionPool.getGroups().containsKey(args[1]))
                        {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);

                            if(!permissionGroup.getServerGroupPermissions().containsKey(args[5]))
                            {
                                permissionGroup.getServerGroupPermissions().put(args[5], new ArrayList<>());
                            }

                            permissionGroup.getServerGroupPermissions().get(args[5]).remove(args[4].replaceFirst("-", ""));
                            PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                            CloudNet.getInstance().getNetworkManager().reload();
                            CloudNet.getInstance().getNetworkManager().updateAll0();
                            sender.sendMessage("Du Entfernst eine Permission " + args[4] + " von der Permission gruppe \"" + permissionGroup.getName() + "\" bei der ServerGruppe " + args[5]);
                        }
                        else
                        {
                            sender.sendMessage("Die Permission Gruppe existiert nicht");
                        }
                    }
                }

                return;
            }
            if(args[0].equalsIgnoreCase("user"))
            {
                if(args.length == 2)
                {
                    UUID uniqueId = CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase().get(args[1]);
                    if(uniqueId != null)
                    {
                        OfflinePlayer offlinePlayer = CloudNet.getInstance().getDbHandlers().getPlayerDatabase().getPlayer(uniqueId);
                        if(offlinePlayer != null && offlinePlayer.getPermissionEntity() != null)
                        {
                            StringBuilder stringBuilder = new StringBuilder();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
                            for(GroupEntityData groupEntityData : offlinePlayer.getPermissionEntity().getGroups())
                            {
                                stringBuilder.append(groupEntityData.getGroup() + "@" + (groupEntityData.getTimeout() == 0 || groupEntityData.getTimeout() == -1 ? "LIFETIME" : simpleDateFormat.format(groupEntityData.getTimeout())) + " ");
                            }

                            sender.sendMessage(
                                    " ",
                                    "Player " + offlinePlayer.getName() + ": " + offlinePlayer.getUniqueId(),
                                    "Groups: " + stringBuilder.substring(0),
                                    " "
                            );

                            for(Map.Entry<String, Boolean> booleanEntry : offlinePlayer.getPermissionEntity().getPermissions().entrySet())
                            {
                                sender.sendMessage("- " + booleanEntry.getKey() + " [" + booleanEntry.getValue() + "]");
                            }

                            sender.sendMessage(" ");
                        }
                        else
                        {
                            sender.sendMessage("Der Spieler ist nicht im Netzwerk Registiert");
                        }
                    }
                    else
                    {
                        sender.sendMessage("Der Spieler ist nicht im Netzwerk Registiert");
                    }
                    return;
                }

                //UTILS

                if(args.length == 5)
                {//perms user Dytanic group remove Admin
                    UUID uniqueId = CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase().get(args[1]);
                    if(uniqueId != null)
                    {
                        OfflinePlayer offlinePlayer = CloudNet.getInstance().getDbHandlers().getPlayerDatabase().getPlayer(uniqueId);
                        if(offlinePlayer != null && offlinePlayer.getPermissionEntity() != null)
                        {
                            if(args[2].equalsIgnoreCase("GROUP"))
                            {
                                if(args[3].equalsIgnoreCase("REMOVE"))
                                {
                                    if(PermissionModule.getInstance().getPermissionPool().getGroups().containsKey(args[4]))
                                    {
                                        GroupEntityData groupEntityData = null;
                                        for(GroupEntityData groupEntity : offlinePlayer.getPermissionEntity().getGroups())
                                        {
                                            if(groupEntity.getGroup().equalsIgnoreCase(args[4]))
                                            {
                                                groupEntityData = groupEntity;
                                            }
                                        }

                                        if(groupEntityData != null)
                                        {
                                            offlinePlayer.getPermissionEntity().getGroups().remove(groupEntityData);
                                        }

                                        if(offlinePlayer.getPermissionEntity().getGroups().size() == 0)
                                        {
                                            offlinePlayer.getPermissionEntity().getGroups().add(new GroupEntityData(permissionPool.getDefaultGroup().getName(), 0));
                                        }
                                        updatePlayer(offlinePlayer);
                                        sender.sendMessage("Der Spieler " + offlinePlayer.getName() + " ist nun nicht mehr in der Gruppe " + args[4]);
                                    }
                                }
                            }

                            if(args[2].equalsIgnoreCase("ADD"))
                            {
                                if(args[3].equalsIgnoreCase("PERMISSION"))
                                {
                                    offlinePlayer.getPermissionEntity().getPermissions().put(args[4].replaceFirst("-", ""), !args[4].startsWith("-"));
                                    updatePlayer(offlinePlayer);
                                    sender.sendMessage("Der Spieler \"" + offlinePlayer.getName() + "\" hat nun die Permission " + args[4]);
                                }
                            }

                            if(args[2].equalsIgnoreCase("REMOVE"))
                            {
                                if(args[3].equalsIgnoreCase("PERMISSION"))
                                {
                                    offlinePlayer.getPermissionEntity().getPermissions().remove(args[4]);
                                    updatePlayer(offlinePlayer);
                                    sender.sendMessage("Der Spieler \"" + offlinePlayer.getName() + "\" hat nun nicht mehr die Permission " + args[4]);
                                }
                            }
                        }
                        else
                        {
                            sender.sendMessage("Der Spieler ist nicht im Netzwerk Registiert");
                        }
                    }
                    else
                    {
                        sender.sendMessage("Der Spieler ist nicht im Netzwerk Registiert");
                    }
                    return;
                }

                if(args.length == 6)
                {
                    UUID uniqueId = CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase().get(args[1]);
                    if(uniqueId != null)
                    {
                        OfflinePlayer offlinePlayer = CloudNet.getInstance().getDbHandlers().getPlayerDatabase().getPlayer(uniqueId);
                        if(offlinePlayer != null && offlinePlayer.getPermissionEntity() != null)
                        {
                            if(args[2].equalsIgnoreCase("GROUP"))
                            {
                                if(args[3].equalsIgnoreCase("SET"))
                                {
                                    if(permissionPool.getGroups().containsKey(args[4]))
                                    {
                                        offlinePlayer.getPermissionEntity().getGroups().clear();
                                        offlinePlayer.getPermissionEntity().getGroups()
                                                .add(new GroupEntityData(args[4],
                                                        (args[5].equalsIgnoreCase("lifetime") ? 0L : NetworkUtils.checkIsNumber(args[5]) ? calcDays(Integer.parseInt(args[5])) : 0L)));
                                        updatePlayer(offlinePlayer);
                                        sender.sendMessage("Die Permission gruppe von " + offlinePlayer.getName() + " ist nun " + args[4]);
                                    }
                                    else
                                    {
                                        sender.sendMessage("Die Permission Gruppe exisitert nicht");
                                    }
                                    return;
                                }
                                if(args[3].equalsIgnoreCase("ADD"))
                                {
                                    if(permissionPool.getGroups().containsKey(args[4]))
                                    {
                                        offlinePlayer.getPermissionEntity().getGroups()
                                                .add(new GroupEntityData(args[4],
                                                        (args[5].equalsIgnoreCase("lifetime") ? 0L : NetworkUtils.checkIsNumber(args[4]) ? calcDays(Integer.parseInt(args[4])) : 0L)));
                                        updatePlayer(offlinePlayer);
                                        sender.sendMessage("Der Spieler " + offlinePlayer.getName() + " ist nun auch in der Gruppe " + args[4]);
                                    }
                                    else
                                    {
                                        sender.sendMessage("Die Permission Gruppe exisitert nicht");
                                    }
                                    return;
                                }
                            }
                        }
                        else
                        {
                            sender.sendMessage("Der Spieler ist nicht im Netzwerk Registiert");
                        }
                    }
                    else
                    {
                        sender.sendMessage("Der Spieler ist nicht im Netzwerk Registiert");
                    }
                    return;
                }

                return;
            }

            if(args[0].equalsIgnoreCase("create"))
            {
                if(args.length == 2)
                {
                    if(!permissionPool.getGroups().containsKey(args[1]))
                    {
                        PermissionGroup permissionGroup = new DefaultPermissionGroup(args[1]);
                        PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                        CloudNet.getInstance().getNetworkManager().reload();
                        CloudNet.getInstance().getNetworkManager().updateAll0();
                        sender.sendMessage("Die Gruppe " + args[1] + " wurde Erstellt!");
                    }
                    else
                    {
                        sender.sendMessage("Die Permission gruppe existiert bereits!");
                    }
                }
            }
        }
        else
        {
            sender.sendMessage(
                    "CloudNet-Permissions: [\"_\" = \" \"]",
                    " ",
                    "perms CREATE <groupName>",
                    "perms GROUP",
                    "perms GROUP <name>",
                    "perms GROUP <name> add permission <permission>",
                    "perms GROUP <name> remove permission <permission>",
                    "perms GROUP <name> add permission <permission> <group>",
                    "perms GROUP <name> remove permission <permission> <group>",
                    "perms GROUP <name> setDisplay <display>",
                    "perms GROUP <name> setJoinPower <joinPower>",
                    "perms GROUP <name> setSuffix <joinPower>",
                    "perms GROUP <name> setPrefix <prefix>",
                    "perms GROUP <name> setTagId <tagId>",
                    "perms GROUP <name> setDefault <true : false>",
                    "perms GROUP <name> setJoinPower <joinPower>",
                    "perms USER <user>",
                    "perms USER <user> GROUP SET <name> <lifetime | time in days>",
                    "perms USER <user> GROUP ADD <name> <lifetime | time in days>",
                    "perms USER <user> GROUP REMOVE <name>",
                    "perms USER <user> ADD PERMISSION <permission>",
                    "perms USER <user> REMOVE PERMISSION <permission>"
            );
        }
    }

    private void updatePlayer(OfflinePlayer offlinePlayer)
    {
        CloudNet.getInstance().getDbHandlers().getPlayerDatabase().updatePlayer(offlinePlayer);
        if(CloudNet.getInstance().getNetworkManager().getOnlinePlayers().containsKey(offlinePlayer.getUniqueId()))
        {
            CloudNet.getInstance().getNetworkManager().getOnlinePlayers().get(offlinePlayer.getUniqueId()).setPermissionEntity(offlinePlayer.getPermissionEntity());
            CloudNet.getInstance().getNetworkManager().handlePlayerUpdate(CloudNet.getInstance().getNetworkManager().getOnlinePlayers().get(offlinePlayer.getUniqueId()));
        }
        CloudNet.getInstance().getNetworkManager().sendAllUpdate(new PacketOutUpdateOfflinePlayer(offlinePlayer));
    }

    private long calcDays(int value)
    {
        return System.currentTimeMillis() + ((60 * 60 * 1000 * 24 * value));
    }
}