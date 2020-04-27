package l2server.gameserver.model.event;

import l2server.gameserver.model.event.impl.IBaseEvent;
import l2server.gameserver.model.event.impl.OnDayNightChange;
import l2server.gameserver.model.event.impl.creature.OnCreatureKilled;
import l2server.gameserver.model.event.impl.creature.npc.OnAttackableKill;
import l2server.gameserver.model.event.impl.creature.player.OnPlayerFishing;
import l2server.gameserver.model.event.impl.creature.player.OnPlayerLevelChanged;
import l2server.gameserver.model.event.impl.creature.player.OnPlayerPvPKill;
import l2server.gameserver.model.event.impl.creature.player.event.OnEventParticipate;
import l2server.gameserver.model.event.returns.TerminateReturn;

public enum EventType
{
    // Attackable events
     ON_CREATURE_KILLED(OnCreatureKilled.class, void.class, TerminateReturn.class),
     ON_ATTACKABLE_KILL(OnAttackableKill.class, void.class),

    ON_EVENT_PARTICIPATE(OnEventParticipate.class, void.class),
    ON_PLAYER_FISHING(OnPlayerFishing.class, void.class),

    ON_PLAYER_LEVEL_CHANGED(OnPlayerLevelChanged.class, void.class),
    ON_PLAYER_PVP_KILL(OnPlayerPvPKill.class, void.class),
    ON_DAY_NIGHT_CHANGE(OnDayNightChange .class, void.class);

    private final Class<? extends IBaseEvent> _eventClass;
    private final Class<?>[] _returnClass;

    EventType(Class<? extends IBaseEvent> eventClass, Class<?>... returnClasss)
    {
        _eventClass = eventClass;
        _returnClass = returnClasss;
    }

    public Class<? extends IBaseEvent> getEventClass()
    {
        return _eventClass;
    }

    public Class<?>[] getReturnClasses()
    {
        return _returnClass;
    }

    public boolean isEventClass(Class<?> clazz)
    {
        return _eventClass == clazz;
    }

    public boolean isReturnClass(Class<?> clazz)
    {
        return contains(_returnClass, clazz);
    }

    public static <T> boolean contains(T[] array, T obj)
    {
        for (T element : array)
        {
            if (element.equals(obj))
            {
                return true;
            }
        }
        return false;
    }

}
