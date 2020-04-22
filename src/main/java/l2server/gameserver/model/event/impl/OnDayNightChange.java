package l2server.gameserver.model.event.impl;

import l2server.gameserver.model.event.EventType;

public class OnDayNightChange implements IBaseEvent
{
    private final boolean _isNight;

    public OnDayNightChange(boolean isNight)
    {
        _isNight = isNight;
    }

    public boolean isNight()
    {
        return _isNight;
    }

    @Override
    public EventType getType()
    {
        return EventType.ON_DAY_NIGHT_CHANGE;
    }
}

