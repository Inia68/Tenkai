package l2server.gameserver.network.serverpackets;

import l2server.gameserver.model.Location;
import l2server.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author MegaParzor!
 */
public class ExAutoFishAvailable extends L2GameServerPacket
{

    public static final ExAutoFishAvailable YES = new ExAutoFishAvailable(true);
    public static final ExAutoFishAvailable NO = new ExAutoFishAvailable(false);

    private final boolean _available;

    private ExAutoFishAvailable(boolean available)
    {
        _available = available;
    }


    @Override
	public void writeImpl()
	{
        writeC(_available ? 1 : 0);
	}


}
