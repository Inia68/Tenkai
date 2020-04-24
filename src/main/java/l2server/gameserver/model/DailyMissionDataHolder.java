package l2server.gameserver.model;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;


import kotlin.reflect.jvm.internal.impl.name.ClassId;
import l2server.gameserver.Item;
import l2server.gameserver.handler.AbstractDailyMissionHandler;
import l2server.gameserver.handler.DailyMissionHandler;
import l2server.gameserver.handler.ItemHandler;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.holder.ItemHolder;
import l2server.gameserver.model.itemcontainer.ItemContainer;
import l2server.gameserver.templates.StatsSet;

public class DailyMissionDataHolder
{
    private final int _id;
    private final int _rewardId;
    private final List<ItemHolder> _rewardsItems;
    private final List<ClassId> _classRestriction;
    private final int _requiredCompletions;
    private final StatSet _params;
    private final boolean _dailyReset;
    private final boolean _isOneTime;
    private final AbstractDailyMissionHandler _handler;

    public DailyMissionDataHolder(StatSet set)
    {
        final Function<DailyMissionDataHolder, AbstractDailyMissionHandler> handler = DailyMissionHandler.getInstance().getHandler(set.getString("handler"));
        _id = set.getInt("id");
        _rewardId = set.getInt("reward_id", 0);
        _requiredCompletions = set.getInt("requiredCompletion", 0);
        _rewardsItems = set.getList("items", ItemHolder.class);
        _classRestriction = new LinkedList<>(); // set.getList("classRestriction", ClassId.class);
        _params = set.getObject("params", StatSet.class);
        _dailyReset = set.getBoolean("dailyReset", false);
        _isOneTime = set.getBoolean("isOneTime", true);
        _handler = handler != null ? handler.apply(this) : null;
    }

    public int getId()
    {
        return _id;
    }

    public int getRewardId()
    {
        return _rewardId;
    }

    public List<ClassId> getClassRestriction()
    {
        return _classRestriction;
    }

    public List<ItemHolder> getRewards()
    {
        return _rewardsItems;
    }

    public int getRequiredCompletions()
    {
        return _requiredCompletions;
    }

    public StatSet getParams()
    {
        return _params;
    }

    public boolean dailyReset()
    {
        return _dailyReset;
    }

    public boolean isOneTime()
    {
        return _isOneTime;
    }

    public boolean isDisplayable(L2PcInstance player)
    {
        return (_dailyReset || !_isOneTime || (getStatus(player) != DailyMissionStatus.COMPLETED.getClientId())); //&& (_classRestriction.isEmpty() || _classRestriction.contains(player.getClassId()));
    }

    public void requestReward(L2PcInstance player)
    {
        if ((_handler != null) && isDisplayable(player))
        {
            _handler.requestReward(player);
        }
    }

    public int getStatus(L2PcInstance player)
    {
        return _handler != null ? _handler.getStatus(player) : DailyMissionStatus.NOT_AVAILABLE.getClientId();
    }

    public int getProgress(L2PcInstance player)
    {
        return _handler != null ? _handler.getProgress(player) : DailyMissionStatus.NOT_AVAILABLE.getClientId();
    }

    public void reset()
    {
        if (_handler != null)
        {
            _handler.reset();
        }
    }
}
