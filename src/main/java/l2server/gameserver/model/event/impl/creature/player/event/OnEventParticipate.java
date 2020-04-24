package l2server.gameserver.model.event.impl.creature.player.event;

import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.event.EventType;
import l2server.gameserver.model.event.impl.IBaseEvent;

public class OnEventParticipate implements IBaseEvent
{
    private final L2PcInstance _player;

    public OnEventParticipate(L2PcInstance player)
    {
        _player = player;
    }

    public L2PcInstance getPlayer()
    {
        return _player;
    }


    @Override
    public EventType getType()
    {
        return EventType.ON_EVENT_PARTICIPATE;
    }
}
