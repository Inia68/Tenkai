package l2server.gameserver.model.event.impl.creature;

import l2server.gameserver.model.actor.L2Character;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.event.EventType;
import l2server.gameserver.model.event.impl.IBaseEvent;

public class OnCreatureKilled implements IBaseEvent
{
    private final L2Character _attacker;
    private final L2Character _target;

    public OnCreatureKilled(L2Character attacker, L2Character target)
    {
        _attacker = attacker;
        _target = target;
    }

    public L2Character getAttacker()
    {
        return _attacker;
    }

    public L2Character getTarget()
    {
        return _target;
    }

    @Override
    public EventType getType()
    {
        return EventType.ON_CREATURE_KILLED;
    }
}