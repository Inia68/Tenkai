package l2server.gameserver.model.event.returns;

public class TerminateReturn extends AbstractEventReturn
{
    private final boolean _terminate;

    public TerminateReturn(boolean terminate, boolean override, boolean abort)
    {
        super(override, abort);
        _terminate = terminate;
    }

    /**
     * @return {@code true} if execution has to be terminated, {@code false} otherwise.
     */
    public boolean terminate()
    {
        return _terminate;
    }
}
