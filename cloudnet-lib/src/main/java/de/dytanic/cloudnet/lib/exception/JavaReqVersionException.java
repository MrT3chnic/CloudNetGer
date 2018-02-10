/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.exception;

/**
 * Created by Tareko on 18.09.2017.
 */
public class JavaReqVersionException extends RuntimeException {

    public JavaReqVersionException()
    {
        super("Du musst Java 8 haben, bitte Prüfe deine Version mit diesen Command \"java -version\"");
    }
}