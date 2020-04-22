package l2server.gameserver.model.event.listeners;


import l2server.gameserver.model.event.EventType;
import l2server.gameserver.model.event.ListenersContainer;
import l2server.gameserver.model.event.impl.IBaseEvent;
import l2server.gameserver.model.event.returns.AbstractEventReturn;

import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FunctionEventListener extends AbstractEventListener
{
    private static final Logger LOGGER = Logger.getLogger(FunctionEventListener.class.getName());
    private final Function<IBaseEvent, ? extends AbstractEventReturn> _callback;

    @SuppressWarnings("unchecked")
    public FunctionEventListener(ListenersContainer container, EventType type, Function<? extends IBaseEvent, ? extends AbstractEventReturn> callback, Object owner)
    {
        super(container, type, owner);
        _callback = (Function<IBaseEvent, ? extends AbstractEventReturn>) callback;
    }

    @Override
    public <R extends AbstractEventReturn> R executeEvent(IBaseEvent event, Class<R> returnBackClass)
    {
        try
        {
            return returnBackClass.cast(_callback.apply(event));
        }
        catch (Exception e)
        {
            LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Error while invoking " + event + " on " + getOwner(), e);
        }
        return null;
    }
}
