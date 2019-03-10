package org.shredzone.flattr4j.connector;

import org.shredzone.flattr4j.exception.FlattrException;

public interface Connector {
    Connection create() throws FlattrException;

    Connection create(RequestType requestType) throws FlattrException;
}
