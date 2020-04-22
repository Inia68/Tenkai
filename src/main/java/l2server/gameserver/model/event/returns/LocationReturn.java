package l2server.gameserver.model.event.returns;

import l2server.gameserver.model.Location;
import l2server.gameserver.model.entity.Instance;

public class LocationReturn extends TerminateReturn
{
    private final boolean _overrideLocation;
    private int _x;
    private int _y;
    private int _z;
    private int _heading;
    private Instance _instance;

    public LocationReturn(boolean terminate, boolean overrideLocation)
    {
        super(terminate, false, false);
        _overrideLocation = overrideLocation;
    }

    public LocationReturn(boolean terminate, boolean overrideLocation, Location targetLocation, Instance instance)
    {
        super(terminate, false, false);
        _overrideLocation = overrideLocation;
        if (targetLocation != null)
        {
            setX(targetLocation.getX());
            setY(targetLocation.getY());
            setZ(targetLocation.getZ());
            setHeading(targetLocation.getHeading());
            setInstance(instance);
        }
    }

    public void setX(int x)
    {
        _x = x;
    }

    public void setY(int y)
    {
        _y = y;
    }

    public void setZ(int z)
    {
        _z = z;
    }

    public void setHeading(int heading)
    {
        _heading = heading;
    }

    public void setInstance(Instance instance)
    {
        _instance = instance;
    }

    public boolean overrideLocation()
    {
        return _overrideLocation;
    }

    public int getX()
    {
        return _x;
    }

    public int getY()
    {
        return _y;
    }

    public int getZ()
    {
        return _z;
    }

    public int getHeading()
    {
        return _heading;
    }

    public Instance getInstance()
    {
        return _instance;
    }
}
