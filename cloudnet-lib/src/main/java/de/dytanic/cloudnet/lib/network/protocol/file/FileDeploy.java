/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol.file;

import de.dytanic.cloudnet.lib.network.protocol.ProtocolBuffer;
import de.dytanic.cloudnet.lib.network.protocol.ProtocolStream;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;

/**
 * Created by Tareko on 09.09.2017.
 */
@NoArgsConstructor
@AllArgsConstructor
public class FileDeploy extends ProtocolStream {

    protected String dest;

    protected byte[] bytes;

    @Override
    public void write(ProtocolBuffer out) throws Exception
    {
        out.writeString(dest);
        out.writeVarInt(bytes.length);
        out.writeBytes(bytes);
    }

    @Override
    public void read(ProtocolBuffer in) throws Exception
    {
        if(in.readableBytes() != 0)
        {
            this.dest = in.readString();
            this.bytes = in.readBytes(in.readVarInt()).array();
            toWrite();
        }
    }

    public void toWrite()
    {
        try
        {
            File file = new File(dest);
            file.getParentFile().mkdirs();
            file.createNewFile();

            try(FileOutputStream fileOutputStream = new FileOutputStream(file))
            {
                fileOutputStream.write(bytes);
                fileOutputStream.flush();
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}