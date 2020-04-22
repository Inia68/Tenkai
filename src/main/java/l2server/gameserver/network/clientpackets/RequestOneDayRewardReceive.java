package l2server.gameserver.network.clientpackets;

import l2server.gameserver.datatables.DailyMissionData;
import l2server.gameserver.model.DailyMissionDataHolder;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.network.serverpackets.ExOneDayReceiveRewardList;

import java.util.Collection;

/**
 * @author MegaParzor!
 */
public class RequestOneDayRewardReceive extends L2GameClientPacket
{
    private int _reward;

	@Override
	public void readImpl()
	{
        _reward = readC();
	}

	@Override
	public void runImpl()
	{

        final L2PcInstance player = getClient().getActiveChar();
        if (player == null)
        {
            return;
        }

        final Collection<DailyMissionDataHolder> reward = DailyMissionData.getInstance().getDailyMissionData(_reward);
        if (reward.isEmpty())
        {
            return;
        }

        reward.stream().filter(o -> o.isDisplayable(player)).forEach(r -> r.requestReward(player));
	}
}
