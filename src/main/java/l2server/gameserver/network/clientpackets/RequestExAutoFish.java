package l2server.gameserver.network.clientpackets;

import l2server.gameserver.datatables.SkillTable;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.log.Log;

/**
 * @author MegaParzor!
 */
public class RequestExAutoFish extends L2GameClientPacket
{
    private boolean _start;

	@Override
	public void readImpl()
	{
        _start = readC() != 0;
	}

	@Override
	public void runImpl()
	{
        final L2PcInstance player = getClient().getActiveChar();
        if (player == null)
        {
            return;
        }

        if (_start)
        {
            player.getFishing().startFishing();
        }
        else
        {
            player.getFishing().stopFishing();
        }
	}
}
