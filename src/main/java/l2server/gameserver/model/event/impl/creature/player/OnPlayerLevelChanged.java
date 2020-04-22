package l2server.gameserver.model.event.impl.creature.player;

import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.event.EventType;
import l2server.gameserver.model.event.impl.IBaseEvent;

public class OnPlayerLevelChanged implements IBaseEvent
{
    private final L2PcInstance _player;
    private final int _oldLevel;
    private final int _newLevel;

    public OnPlayerLevelChanged(L2PcInstance player, int oldLevel, int newLevel)
    {
        _player = player;
        _oldLevel = oldLevel;
        _newLevel = newLevel;
    }

    public L2PcInstance getPlayer()
    {
        return _player;
    }

    public int getOldLevel()
    {
        return _oldLevel;
    }

    public int getNewLevel()
    {
        return _newLevel;
    }

    @Override
    public EventType getType()
    {
        return EventType.ON_PLAYER_LEVEL_CHANGED;
    }
}
