package l2server.gameserver.model.event.listeners;

import l2server.gameserver.model.event.EventType;
import l2server.gameserver.model.event.ListenersContainer;
import l2server.gameserver.model.event.impl.IBaseEvent;
import l2server.gameserver.model.event.returns.AbstractEventReturn;

public class RunnableEventListener extends AbstractEventListener
{
    private final Runnable _callback;

    public RunnableEventListener(ListenersContainer container, EventType type, Runnable callback, Object owner)
    {
        super(container, type, owner);
        _callback = callback;
    }

    @Override
    public <R extends AbstractEventReturn> R executeEvent(IBaseEvent event, Class<R> returnBackClass)
    {
        _callback.run();
        return null;
    }
}

