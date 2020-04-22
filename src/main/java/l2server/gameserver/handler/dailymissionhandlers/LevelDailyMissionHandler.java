package l2server.gameserver.handler.dailymissionhandlers;

import l2server.gameserver.handler.AbstractDailyMissionHandler;
import l2server.gameserver.model.DailyMissionDataHolder;
import l2server.gameserver.model.DailyMissionPlayerEntry;
import l2server.gameserver.model.DailyMissionStatus;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.event.Containers;
import l2server.gameserver.model.event.EventType;
import l2server.gameserver.model.event.impl.creature.player.OnPlayerLevelChanged;
import l2server.gameserver.model.event.listeners.ConsumerEventListener;
import org.springframework.context.event.EventListener;

public class LevelDailyMissionHandler extends AbstractDailyMissionHandler
{
    private final int _level;
    private final boolean _dualclass;

    public LevelDailyMissionHandler(DailyMissionDataHolder holder)
    {
        super(holder);
        _level = holder.getParams().getInt("level");
        _dualclass = holder.getParams().getBoolean("dualclass", false);
    }

    public LevelDailyMissionHandler()
    {
        super(null);
        _level = 0;
        _dualclass = false;
    }

    @Override
    public void init()
    {
        Containers.Players().addListener(new ConsumerEventListener(this, EventType.ON_PLAYER_LEVEL_CHANGED, (OnPlayerLevelChanged event) -> onPlayerLevelChanged(event), this));
    }

    @Override
    public boolean isAvailable(L2PcInstance player)
    {
        final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), false);
        if (entry != null)
        {
            switch (entry.getStatus())
            {
                case NOT_AVAILABLE:
                {
                    if ((player.getLevel() >= _level)) // Check for dual class
                    {
                        entry.setStatus(DailyMissionStatus.AVAILABLE);
                        storePlayerEntry(entry);
                    }
                    break;
                }
                case AVAILABLE:
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void reset()
    {
        // Level rewards doesn't reset daily
    }

    private void onPlayerLevelChanged(OnPlayerLevelChanged event)
    {
        final L2PcInstance player = event.getPlayer();
        if ((player.getLevel() >= _level)) // Check for dual class
        {
            final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), true);
            if (entry.getStatus() == DailyMissionStatus.NOT_AVAILABLE)
            {
                entry.setStatus(DailyMissionStatus.AVAILABLE);
            } else if (entry.getStatus() == DailyMissionStatus.AVAILABLE) {
                entry.setProgress(player.getLevel());
                storePlayerEntry(entry);
            }
        }
    }

    private void processPlayerProgress(L2PcInstance player)
    {
    }
}
