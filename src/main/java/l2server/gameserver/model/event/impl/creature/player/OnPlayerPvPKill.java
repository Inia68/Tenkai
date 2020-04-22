package l2server.gameserver.model.event.impl.creature.player;

import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.event.EventType;
import l2server.gameserver.model.event.impl.IBaseEvent;

public class OnPlayerPvPKill implements IBaseEvent
{
    private final L2PcInstance _player;
    private final L2PcInstance _target;

    public OnPlayerPvPKill(L2PcInstance player, L2PcInstance target)
    {
        _player = player;
        _target = target;
    }

    public L2PcInstance getPlayer()
    {
        return _player;
    }

    public L2PcInstance getTarget()
    {
        return _target;
    }

    @Override
    public EventType getType()
    {
        return EventType.ON_PLAYER_PVP_KILL;
    }
}
