/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.server.process;

import de.dytanic.cloudnet.lib.cloudserver.CloudServerMeta;
import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.server.ServerProcessMeta;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import de.dytanic.cloudnetwrapper.server.BungeeCord;
import de.dytanic.cloudnetwrapper.server.CloudGameServer;
import de.dytanic.cloudnetwrapper.server.GameServer;
import de.dytanic.cloudnetwrapper.server.ServerStage;
import lombok.Getter;
import lombok.Setter;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class ServerProcessQueue implements Runnable {

    @Setter
    private volatile boolean running = true;

    private final Queue<ServerProcess> servers = new ConcurrentLinkedQueue<>();
    private final Queue<ProxyProcessMeta> proxys = new ConcurrentLinkedQueue<>();
    private final Queue<CloudServerMeta> cloudServers = new ConcurrentLinkedQueue<>();

    private final Queue<GameServer> startups = new ConcurrentLinkedQueue<>();

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private final int process_queue_size;

    public ServerProcessQueue(int process_queue_size)
    {
        this.process_queue_size = process_queue_size;
    }

    public void putProcess(ServerProcessMeta serverProcessMeta)
    {
        this.servers.offer(new ServerProcess(serverProcessMeta, ServerStage.SETUP));
    }

    public void putProcess(CloudServerMeta serverProcessMeta)
    {
        this.cloudServers.offer(serverProcessMeta);
    }

    public void putProcess(ProxyProcessMeta proxyProcessMeta)
    {
        this.proxys.offer(proxyProcessMeta);
    }

    @Override
    public void run()
    {
        {
            short i = 0;
            int memory = CloudNetWrapper.getInstance().getUsedMemory();
            while (running && !servers.isEmpty() && (CloudNetWrapper.getInstance().getWrapperConfig().getPercentOfCPUForANewServer() == 0 || CloudNetWrapper.getInstance().getCpuUsage() <= CloudNetWrapper.getInstance().getWrapperConfig().getPercentOfCPUForANewServer()))
            {
                i++;
                if(i == 3) break;

                ServerProcess serverProcess = servers.poll();

                if(!CloudNetWrapper.getInstance().getServerGroups().containsKey(serverProcess.getMeta().getServiceId().getGroup()))
                {
                    this.servers.add(serverProcess);
                    continue;
                }

                if((memory + serverProcess.getMeta().getMemory())
                        < CloudNetWrapper.getInstance().getMaxMemory())
                {
                    GameServer gameServer = null;
                    try
                    {
                        System.out.println("Hole eintrag von [" + serverProcess.getMeta().getServiceId() + "]");
                        gameServer = new GameServer(serverProcess, ServerStage.SETUP, CloudNetWrapper.getInstance().getServerGroups().get(serverProcess.getMeta().getServiceId().getGroup()));
                        if(gameServer.bootstrap())
                        {
                            this.startups.add(gameServer);
                        }else {
                            this.servers.add(serverProcess);
                        }
                    } catch (Exception e)
                    {
                        System.out.println("Fehler beim starten des Game-Servers " + serverProcess.getMeta().getServiceId().toString());
                        e.printStackTrace();
                        this.servers.add(serverProcess);
                    }
                }
                else
                {
                    this.servers.add(serverProcess);
                }
            }
        }

        {
            short i = 0;
            int memory = CloudNetWrapper.getInstance().getUsedMemory();
            while (running && !proxys.isEmpty() && (CloudNetWrapper.getInstance().getWrapperConfig().getPercentOfCPUForANewProxy() == 0 || CloudNetWrapper.getInstance().getCpuUsage() <= CloudNetWrapper.getInstance().getWrapperConfig().getPercentOfCPUForANewProxy()))
            {
                i++;
                if(i == 3) break;

                ProxyProcessMeta serverProcess = proxys.poll();

                if(!CloudNetWrapper.getInstance().getProxyGroups().containsKey(serverProcess.getServiceId().getGroup()))
                {
                    this.proxys.add(serverProcess);
                    continue;
                }

                if((memory + serverProcess.getMemory())
                        < CloudNetWrapper.getInstance().getMaxMemory())
                {

                    BungeeCord gameServer = new BungeeCord(serverProcess, CloudNetWrapper.getInstance().getProxyGroups().get(serverProcess.getServiceId().getGroup()));

                    try
                    {
                        System.out.println("Hole eintrag von [" + gameServer.getServiceId() + "]");
                        if(!gameServer.bootstrap())
                        {
                            this.proxys.add(serverProcess);
                        }
                    } catch (Exception e)
                    {
                        System.out.println("Fehler beim starten des Proxy-Servers " + gameServer.toString());
                        e.printStackTrace();
                        this.proxys.add(serverProcess);
                    }
                }
                else
                {
                    this.proxys.add(serverProcess);
                }
            }
        }
    }

    public void patchAsync(ServerProcessMeta process)
    {
        if(!CloudNetWrapper.getInstance().getServerGroups().containsKey(process.getServiceId().getGroup()))
        {
            this.servers.add(new ServerProcess(process, ServerStage.SETUP));
            return;
        }
        GameServer gameServer = new GameServer(new ServerProcess(process, ServerStage.SETUP), ServerStage.SETUP, CloudNetWrapper.getInstance().getServerGroups().get(process.getServiceId().getGroup()));
        this.executorService.submit(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    gameServer.bootstrap();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    public void patchAsync(CloudServerMeta cloudServerMeta)
    {
        CloudGameServer cloudGameServer = new CloudGameServer(cloudServerMeta);
        this.executorService.execute(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    cloudGameServer.bootstrap();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    public void patchAsync(ProxyProcessMeta proxyProcessMeta)
    {
        BungeeCord bungeeCord = new BungeeCord(proxyProcessMeta, CloudNetWrapper.getInstance().getProxyGroups().get(proxyProcessMeta.getServiceId().getGroup()));

        if(!CloudNetWrapper.getInstance().getProxyGroups().containsKey(proxyProcessMeta.getServiceId().getGroup()))
        {
            this.proxys.add(proxyProcessMeta);
            return;
        }

        this.executorService.execute(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    bungeeCord.bootstrap();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

}