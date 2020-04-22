package l2server.gameserver.network.clientpackets;

import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.network.serverpackets.ExBRGamePoint;

/**
 * @author MegaParzor!
 */
public class RequestBRGamePoint extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{
	}

	@Override
	public void runImpl()
	{
        final L2PcInstance player = getClient().getActiveChar();
        if (player != null)
        {
            getClient().sendPacket(new ExBRGamePoint(player));
        }}
}
