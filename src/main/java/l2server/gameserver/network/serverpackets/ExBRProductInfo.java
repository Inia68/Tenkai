package l2server.gameserver.network.serverpackets;

import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.primeshop.PrimeShopGroup;
import l2server.gameserver.model.primeshop.PrimeShopItem;

/**
 * @author MegaParzor!
 */
public class ExBRProductInfo extends L2GameServerPacket
{
    private final PrimeShopGroup _item;
    private final long _charPoints;
    private final long _charAdena;
    private final long _charCoins;

	public ExBRProductInfo(PrimeShopGroup item, L2PcInstance player)
	{
        _item = item;
        _charPoints = player.getPrimePoints();
        _charAdena = player.getAdena();
        _charCoins = player.getInventory().getInventoryItemCount(23805, -1);
	}

	@Override
	public void writeImpl()
	{
        writeD(_item.getBrId());
        writeD(_item.getPrice());
        writeD(_item.getItems().size());
        for (PrimeShopItem item : _item.getItems())
        {
            writeD(item.getId());
            writeD((int) item.getCount());
            writeD(item.getWeight());
            writeD(item.isTradable());
        }
        writeQ(_charAdena);
        writeQ(_charPoints);
        writeQ(_charCoins); // Hero coins
	}
}
