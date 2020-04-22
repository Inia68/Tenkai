package l2server.gameserver.model.event.timers;

public interface IEventTimerCancel<T>
{
    /**
     * Notified upon timer cancellation.
     * @param holder
     */
    void onTimerCancel(TimerHolder<T> holder);
}
