package l2server.gameserver.network.serverpackets;

import l2server.gameserver.model.Location;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.interfaces.ILocational;

/**
 * @author MegaParzor!
 */
public class ExUserInfoFishing extends L2GameServerPacket
{
    private final L2PcInstance _player;
    private final boolean _isFishing;
    private final ILocational _baitLocation;

    public ExUserInfoFishing(L2PcInstance player, boolean isFishing, ILocational baitLocation)
    {
        _player = player;
        _isFishing = isFishing;
        _baitLocation = baitLocation;
    }

    public ExUserInfoFishing(L2PcInstance player, boolean isFishing)
    {
        _player = player;
        _isFishing = isFishing;
        _baitLocation = null;
    }

    @Override
	public void writeImpl()
	{

        writeD(_player.getObjectId());
        writeC(_isFishing ? 1 : 0);
        if (_baitLocation == null)
        {
            writeD(0);
            writeD(0);
            writeD(0);
        }
        else
        {
            writeD(_baitLocation.getX());
            writeD(_baitLocation.getY());
            writeD(_baitLocation.getZ());
        }
	}


}
