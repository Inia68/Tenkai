package l2server.gameserver.network.clientpackets;

import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.network.serverpackets.ExVipAttendanceList;
import l2server.log.Log;

/**
 * @author MegaParzor!
 */
public class RequestVipAttendanceItemList extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{
	}

	@Override
	public void runImpl()
	{
		// TODO
		Log.info(getType() + " packet was received from " + getClient() + ".");
        final L2PcInstance player = getClient().getActiveChar();
        if (player == null)
        {
            return;
        }

        player.sendPacket(new ExVipAttendanceList(player));
	}
}
