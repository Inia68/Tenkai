package l2server.gameserver.model.interfaces;

import java.time.Duration;

public interface IParserAdvUtils extends IParserUtils
{
    boolean getBoolean(String key);

    byte getByte(String key);

    short getShort(String key);

    int getInt(String key);

    long getLong(String key);

    float getFloat(String key);

    double getDouble(String key);

    String getString(String key);

    Duration getDuration(String key);

    <T extends Enum<T>> T getEnum(String key, Class<T> clazz);
}

