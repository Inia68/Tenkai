package l2server.gameserver.handler;

import l2server.gameserver.model.DailyMissionDataHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DailyMissionHandler {
    private final Map<String, Function<DailyMissionDataHolder, AbstractDailyMissionHandler>> _handlerFactories = new HashMap<>();

    public void registerHandler(String name, Function<DailyMissionDataHolder, AbstractDailyMissionHandler> handlerFactory)
    {
        _handlerFactories.put(name, handlerFactory);
    }

    public Function<DailyMissionDataHolder, AbstractDailyMissionHandler> getHandler(String name)
    {
        return _handlerFactories.get(name);
    }

    public int size()
    {
        return _handlerFactories.size();
    }

    public void executeScript()
    {
        try
        {
            // ScriptEngineManager.getInstance().executeScript(ScriptEngineManager.ONE_DAY_REWARD_MASTER_HANDLER);
        }
        catch (Exception e)
        {
            throw new Error("Problems while running DailyMissionMasterHandler", e);
        }
    }

    public static DailyMissionHandler getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder
    {
        protected static final DailyMissionHandler INSTANCE = new DailyMissionHandler();
    }
}
