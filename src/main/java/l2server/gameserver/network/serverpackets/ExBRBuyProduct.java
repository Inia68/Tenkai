package l2server.gameserver.network.serverpackets;

import l2server.gameserver.model.interfaces.IIdentifiable;

/**
 * @author MegaParzor!
 */
public class ExBRBuyProduct extends L2GameServerPacket
{
    public enum ExBrProductReplyType implements IIdentifiable
    {
        SUCCESS(1),
        LACK_OF_POINT(-1),
        INVALID_PRODUCT(-2),
        USER_CANCEL(-3),
        INVENTROY_OVERFLOW(-4),
        CLOSED_PRODUCT(-5),
        SERVER_ERROR(-6),
        BEFORE_SALE_DATE(-7),
        AFTER_SALE_DATE(-8),
        INVALID_USER(-9),
        INVALID_ITEM(-10),
        INVALID_USER_STATE(-11),
        NOT_DAY_OF_WEEK(-12),
        NOT_TIME_OF_DAY(-13),
        SOLD_OUT(-14);
        private final int _id;

        ExBrProductReplyType(int id)
        {
            _id = id;
        }

        public int getId()
        {
            return _id;
        }
    }

    private final int _reply;

    public ExBRBuyProduct(ExBrProductReplyType type)
    {
        _reply = type.getId();
    }

	@Override
	public void writeImpl()
	{

        writeD(_reply);
	}
}
