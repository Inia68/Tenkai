package l2server.gameserver.handler.dailymissionhandlers;

import l2server.gameserver.datatables.DailyMissionData;
import l2server.gameserver.handler.AbstractDailyMissionHandler;
import l2server.gameserver.model.DailyMissionDataHolder;
import l2server.gameserver.model.DailyMissionPlayerEntry;
import l2server.gameserver.model.DailyMissionStatus;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.event.Containers;
import l2server.gameserver.model.event.EventType;
import l2server.gameserver.model.event.impl.creature.player.OnPlayerPvPKill;
import l2server.gameserver.model.event.impl.creature.player.event.OnEventParticipate;
import l2server.gameserver.model.event.listeners.ConsumerEventListener;

public class EventDailyMissionHandler extends AbstractDailyMissionHandler {
    private final int _amount;

    public EventDailyMissionHandler(DailyMissionDataHolder holder) {
        super(holder);
        _amount = holder.getRequiredCompletions();
    }

    @Override
    public void init() {
        Containers.Players().addListener(new ConsumerEventListener(this, EventType.ON_EVENT_PARTICIPATE, (OnEventParticipate event) -> onEventParticipate(event), this));
    }

    @Override
    public int getStatus(L2PcInstance player) {
        final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), false);
        if (entry != null) {
            final long lastCompleted = entry.getLastCompleted();
            if (lastCompleted != 0 && (System.currentTimeMillis() - lastCompleted) > 86413793L) // (1 day) delay.
            {
                entry.setLastCompleted(0);
                entry.setProgress(0);
                entry.setStatus(DailyMissionStatus.NOT_AVAILABLE);
                storePlayerEntry(entry);
                return DailyMissionStatus.NOT_AVAILABLE.getClientId();
            }
        }
        return entry != null ? entry.getStatus().getClientId() : DailyMissionStatus.NOT_AVAILABLE.getClientId();
    }

    @Override
    public boolean isAvailable(L2PcInstance player) {
        final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), false);
        if (entry != null) {
            switch (entry.getStatus()) {
                case NOT_AVAILABLE: // Initial state
                {
                    if (entry.getProgress() >= _amount) {
                        entry.setStatus(DailyMissionStatus.AVAILABLE);
                        storePlayerEntry(entry);
                    }
                    break;
                }
                case AVAILABLE: {
                    return true;
                }
            }
        }
        return false;
    }

    private void onEventParticipate(OnEventParticipate event) {
        L2PcInstance player = event.getPlayer();
        processPlayerProgress(player);
    }

    private void processPlayerProgress(L2PcInstance player) {
        final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), true);
        final long lastCompleted = entry.getLastCompleted();
        if (lastCompleted == 0 && entry.getStatus() == DailyMissionStatus.NOT_AVAILABLE) // Initial entry.
        {
            if (entry.increaseProgress() >= _amount) {
                entry.setLastCompleted(System.currentTimeMillis());
                entry.setStatus(DailyMissionStatus.AVAILABLE);
            }
            storePlayerEntry(entry);
        } else if ((System.currentTimeMillis() - lastCompleted) > 86413793L) // (1 day) delay.
        {
            entry.setLastCompleted(0);
            entry.setProgress(0);
            entry.setStatus(DailyMissionStatus.NOT_AVAILABLE);
            storePlayerEntry(entry);
        }
    }
}
