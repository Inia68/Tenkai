package l2server.gameserver.network.clientpackets;

import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.network.serverpackets.ExOneDayReceiveRewardList;

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
        player.sendPacket(new ExOneDayReceiveRewardList(player));
	}
}
