package com.redis.search.Util;

import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.util.SafeEncoder;

public class RediSearchCommands {

    public enum Command implements ProtocolCommand {
        SEARCH("FT.SEARCH");

        private final byte[] raw;

        Command(String alt) {
            raw = SafeEncoder.encode(alt);
        }

        @Override
        public byte[] getRaw() {
            return raw;
        }
    }

}
