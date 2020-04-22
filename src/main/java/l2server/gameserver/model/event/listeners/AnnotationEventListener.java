package l2server.gameserver.model.event.listeners;


import l2server.gameserver.model.event.EventType;
import l2server.gameserver.model.event.ListenersContainer;
import l2server.gameserver.model.event.impl.IBaseEvent;
import l2server.gameserver.model.event.returns.AbstractEventReturn;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnnotationEventListener extends AbstractEventListener
{
    private static final Logger LOGGER = Logger.getLogger(AnnotationEventListener.class.getName());
    private final Method _callback;

    public AnnotationEventListener(ListenersContainer container, EventType type, Method callback, Object owner, int priority)
    {
        super(container, type, owner);
        _callback = callback;
        setPriority(priority);
    }

    @Override
    public <R extends AbstractEventReturn> R executeEvent(IBaseEvent event, Class<R> returnBackClass)
    {
        try
        {
            final Object result = _callback.invoke(getOwner(), event);
            if (_callback.getReturnType() == returnBackClass)
            {
                return returnBackClass.cast(result);
            }
        }
        catch (Exception e)
        {
            LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Error while invoking " + _callback.getName() + " on " + getOwner(), e);
        }
        return null;
    }
}

