package l2server.gameserver.model.event.timers;

public interface IEventTimerEvent<T>
{
    /**
     * notified upon timer execution method.
     * @param holder
     */
    void onTimerEvent(TimerHolder<T> holder);
}
