package l2server.gameserver.network.serverpackets;

import l2server.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author MegaParzor!
 */
public class ExBRGamePoint extends L2GameServerPacket
{
    private final int _charId;
    private final int _charPoints;

	public ExBRGamePoint(L2PcInstance player)
	{
        _charId = player.getObjectId();
        _charPoints = player.getPrimePoints();
	}

	@Override
	public void writeImpl()
	{
        writeD(_charId);
        writeQ(_charPoints);
        writeD(0x00);
	}
}
