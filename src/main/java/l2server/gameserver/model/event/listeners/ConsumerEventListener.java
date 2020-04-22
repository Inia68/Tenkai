package l2server.gameserver.model.event.listeners;

import java.util.function.Consumer;

import l2server.gameserver.model.event.EventType;
import l2server.gameserver.model.event.ListenersContainer;
import l2server.gameserver.model.event.impl.IBaseEvent;
import l2server.gameserver.model.event.returns.AbstractEventReturn;

public class ConsumerEventListener extends AbstractEventListener
{
    private final Consumer<IBaseEvent> _callback;

    @SuppressWarnings("unchecked")
    public ConsumerEventListener(ListenersContainer container, EventType type, Consumer<? extends IBaseEvent> callback, Object owner)
    {
        super(container, type, owner);
        _callback = (Consumer<IBaseEvent>) callback;
    }

    @Override
    public <R extends AbstractEventReturn> R executeEvent(IBaseEvent event, Class<R> returnBackClass)
    {
        _callback.accept(event);
        return null;
    }
}
