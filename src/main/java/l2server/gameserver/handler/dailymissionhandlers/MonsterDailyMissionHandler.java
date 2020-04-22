package l2server.gameserver.handler.dailymissionhandlers;

import l2server.Config;
import l2server.gameserver.handler.AbstractDailyMissionHandler;
import l2server.gameserver.model.*;
import l2server.gameserver.model.actor.L2Character;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.event.Containers;
import l2server.gameserver.model.event.EventType;
import l2server.gameserver.model.event.impl.creature.npc.OnAttackableKill;
import l2server.gameserver.model.event.listeners.ConsumerEventListener;

import java.util.ArrayList;
import java.util.List;

public class MonsterDailyMissionHandler extends AbstractDailyMissionHandler
{
    private final int _amount;
    private final int _minLevel;
    private final int _maxLevel;
    private final List<Integer> _ids = new ArrayList<>();

    public MonsterDailyMissionHandler(DailyMissionDataHolder holder)
    {
        super(holder);
        _amount = holder.getRequiredCompletions();
        _minLevel = holder.getParams().getInt("minLevel", 0);
        _maxLevel = holder.getParams().getInt("maxLevel", Byte.MAX_VALUE);
        final String ids = holder.getParams().getString("ids", "");
        if (!ids.isEmpty())
        {
            for (String s : ids.split(","))
            {
                final int id = Integer.parseInt(s);
                if (!_ids.contains(id))
                {
                    _ids.add(id);
                }
            }
        }
    }

    @Override
    public void init()
    {
        Containers.Monsters().addListener(new ConsumerEventListener(this, EventType.ON_ATTACKABLE_KILL, (OnAttackableKill event) -> onAttackableKill(event), this));
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

    private void onAttackableKill(OnAttackableKill event)
    {
        final L2Character monster = event.getTarget();
        if (!_ids.isEmpty() && !_ids.contains(monster.getInstanceId()))
        {
            return;
        }

        final L2PcInstance player = event.getAttacker();
        if (_minLevel > 0)
        {
            final int monsterLevel = monster.getLevel();
            if ((monsterLevel < _minLevel) || (monsterLevel > _maxLevel) || ((player.getLevel() - monsterLevel) > 5))
            {
                return;
            }
        }

        final L2Party party = player.getParty();
        if (party != null)
        {
            final L2CommandChannel channel = party.getCommandChannel();
            final List<L2PcInstance> members = channel != null ? channel.getMembers() : party.getPartyMembers();
            members.stream().filter(member -> member.getDistanceSq(monster) <= Config.ALT_PARTY_RANGE).forEach(this::processPlayerProgress);
        }
        else
        {
            processPlayerProgress(player);
        }
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
