package l2server.gameserver.model.event.impl.creature.npc;

import l2server.gameserver.model.actor.L2Attackable;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.event.EventType;
import l2server.gameserver.model.event.impl.IBaseEvent;

public class OnAttackableKill implements IBaseEvent
{
    private final L2PcInstance _attacker;
    private final L2Attackable _target;
    private final boolean _isSummon;

    public OnAttackableKill(L2PcInstance attacker, L2Attackable target, boolean isSummon)
    {
        _attacker = attacker;
        _target = target;
        _isSummon = isSummon;
    }

    public L2PcInstance getAttacker()
    {
        return _attacker;
    }

    public L2Attackable getTarget()
    {
        return _target;
    }

    public boolean isSummon()
    {
        return _isSummon;
    }

    @Override
    public EventType getType()
    {
        return EventType.ON_ATTACKABLE_KILL;
    }
}