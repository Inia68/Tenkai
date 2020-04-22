package l2server.gameserver.model.event.listeners;

import l2server.gameserver.model.event.EventType;
import l2server.gameserver.model.event.ListenersContainer;
import l2server.gameserver.model.event.impl.IBaseEvent;
import l2server.gameserver.model.event.returns.AbstractEventReturn;

public class DummyEventListener extends AbstractEventListener
{
    public DummyEventListener(ListenersContainer container, EventType type, Object owner)
    {
        super(container, type, owner);
    }

    @Override
    public <R extends AbstractEventReturn> R executeEvent(IBaseEvent event, Class<R> returnBackClass)
    {
        return null;
    }
}

