package l2server.gameserver.handler.dailymissionhandlers;

import l2server.Config;
import l2server.gameserver.handler.AbstractDailyMissionHandler;
import l2server.gameserver.model.*;
import l2server.gameserver.model.actor.L2Character;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.event.Containers;
import l2server.gameserver.model.event.EventType;
import l2server.gameserver.model.event.impl.creature.npc.OnAttackableKill;
import l2server.gameserver.model.event.impl.creature.player.OnPlayerPvPKill;
import l2server.gameserver.model.event.listeners.ConsumerEventListener;
import l2server.gameserver.templates.chars.L2NpcTemplate;

import java.util.ArrayList;
import java.util.List;

public class PlayerDailyMissionHandler extends AbstractDailyMissionHandler
{
    private final int _amount;
    private final int _levelDiff;

    public PlayerDailyMissionHandler(DailyMissionDataHolder holder)
    {
        super(holder);
        _amount = holder.getRequiredCompletions();
        _levelDiff = holder.getParams().getInt("levelDiff", 0);
    }

    @Override
    public void init()
    {
        Containers.Players().addListener(new ConsumerEventListener(this, EventType.ON_PLAYER_PVP_KILL, (OnPlayerPvPKill event) -> OnPlayerPvPKill(event), this));
    }

    @Override
    public boolean isAvailable(L2PcInstance player)
    {
        final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), false);
        if (entry != null)
        {
            switch (entry.getStatus())
            {
                case NOT_AVAILABLE: // Initial state
                {
                    if (entry.getProgress() >= _amount)
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

    private void OnPlayerPvPKill(OnPlayerPvPKill event)
    {
        final L2PcInstance player = event.getPlayer();
        final L2PcInstance killed = event.getTarget();
        if (Math.abs(player.getLevel() - killed.getLevel()) > _levelDiff)
        {
            return;
        }
        processPlayerProgress(player);
    }

    private void processPlayerProgress(L2PcInstance player)
    {
        final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), true);
        if (entry.getStatus() == DailyMissionStatus.NOT_AVAILABLE)
        {
            if (entry.increaseProgress() >= _amount)
            {
                entry.setStatus(DailyMissionStatus.AVAILABLE);
            }
            storePlayerEntry(entry);
        }
    }
}
