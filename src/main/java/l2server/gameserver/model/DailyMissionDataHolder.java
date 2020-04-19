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
import l2server.gameserver.model.itemcontainer.ItemContainer;
import l2server.gameserver.templates.StatsSet;

public class DailyMissionDataHolder {
    private final int _id;
    private final int _rewardId;
    private final List<ItemContainer> _rewardsItems;
    private final List<Integer> _classRestriction;
    private final int _requiredCompletions;
    private final StatsSet _params;
    private final boolean _dailyReset;
    private final boolean _isOneTime;
    private final AbstractDailyMissionHandler _handler;

    public DailyMissionDataHolder()
    {
        List<Integer> re = null;
        re.add(1);
        ItemContainer test = null;
        test.addItem("test", 57, 200, null, null);
        List<ItemContainer> re2 = null;
        re2.add(test);

        // final Function<DailyMissionDataHolder, AbstractDailyMissionHandler> handler = DailyMissionHandler.getInstance().getHandler(set.getString("handler"));
        _id = 4; // set.getInteger("id");
        _rewardId = 57; // set.getInteger("reward_id", 0);
        _requiredCompletions = 10; // set.getInteger("requiredCompletion", 0);
        _rewardsItems = re2; //set.getIntegerArray("items", ItemContainer.class);
        _classRestriction =  re; ////set.getList("classRestriction", ClassId.class);
        _params = null; // set.getObject("params", StatSet.class);
        _dailyReset = true; // set.getBool("dailyReset", true);
        _isOneTime = false; // set.getBool("isOneTime", true);
        _handler = null; // handler != null ? handler.apply(this) : null;
    }

    public int getId()
    {
        return _id;
    }

    public int getRewardId()
    {
        return _rewardId;
    }

    public List<Integer> getClassRestriction()
    {
        return _classRestriction;
    }

    public List<ItemContainer> getRewards()
    {
        return _rewardsItems;
    }

    public int getRequiredCompletions()
    {
        return _requiredCompletions;
    }

    public StatsSet getParams()
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
        return (!_isOneTime || (getStatus(player) != DailyMissionStatus.COMPLETED.getClientId())) && (_classRestriction.isEmpty() || _classRestriction.contains(player.getClassId()));
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
        return DailyMissionStatus.AVAILABLE.getClientId(); // _handler != null ? _handler.getStatus(player) : DailyMissionStatus.NOT_AVAILABLE.getClientId();
    }

    public int getProgress(L2PcInstance player)
    {
        return 40; // _handler != null ? _handler.getProgress(player) : DailyMissionStatus.NOT_AVAILABLE.getClientId();
    }

    public void reset()
    {
        if (_handler != null)
        {
            _handler.reset();
        }
    }
}
