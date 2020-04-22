package l2server.gameserver.datatables;


import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import l2server.gameserver.Item;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.primeshop.PrimeShopGroup;
import l2server.gameserver.model.primeshop.PrimeShopItem;
import l2server.gameserver.network.serverpackets.ExBRProductInfo;
import l2server.gameserver.templates.StatsSet;
import l2server.gameserver.templates.item.L2Item;
import l2server.util.IXmlReader;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/**
 * @author Gnacik, UnAfraid
 */
public class PrimeShopData implements IXmlReader
{
    private static final Logger LOGGER = Logger.getLogger(PrimeShopData.class.getName());

    private final Map<Integer, PrimeShopGroup> _primeItems = new LinkedHashMap<>();

    protected PrimeShopData()
    {
        load();
    }

    @Override
    public void load()
    {
        _primeItems.clear();
        parseDatapackFile("data/PrimeShop.xml");

        if (!_primeItems.isEmpty())
        {
            LOGGER.info(getClass().getSimpleName() + ": Loaded " + _primeItems.size() + " items.");
        }
        else
        {
            LOGGER.info(getClass().getSimpleName() + ": System is disabled.");
        }
    }

    @Override
    public void parseDocument(Document doc, File f)
    {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
        {
            if ("list".equalsIgnoreCase(n.getNodeName()))
            {
                final NamedNodeMap at = n.getAttributes();
                final Node attribute = at.getNamedItem("enabled");
                if ((attribute != null) && Boolean.parseBoolean(attribute.getNodeValue()))
                {
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
                    {
                        if ("item".equalsIgnoreCase(d.getNodeName()))
                        {
                            NamedNodeMap attrs = d.getAttributes();
                            Node att;
                            final StatsSet set = new StatsSet();
                            for (int i = 0; i < attrs.getLength(); i++)
                            {
                                att = attrs.item(i);
                                set.set(att.getNodeName(), att.getNodeValue());
                            }

                            final List<PrimeShopItem> items = new ArrayList<>();
                            for (Node b = d.getFirstChild(); b != null; b = b.getNextSibling())
                            {
                                if ("item".equalsIgnoreCase(b.getNodeName()))
                                {
                                    attrs = b.getAttributes();

                                    final int itemId = parseInteger(attrs, "itemId");
                                    final int count = parseInteger(attrs, "count");
                                    final L2Item item = ItemTable.getInstance().getTemplate(itemId);
                                    if (item == null)
                                    {
                                        LOGGER.severe(getClass().getSimpleName() + ": Item template null for itemId: " + itemId + " brId: " + set.getInteger("id"));
                                        return;
                                    }

                                    items.add(new PrimeShopItem(itemId, count, item.getWeight(), item.isTradeable() ? 1 : 0));
                                }
                            }

                            _primeItems.put(set.getInteger("id"), new PrimeShopGroup(set, items));
                        }
                    }
                }
            }
        }
    }

    public void showProductInfo(L2PcInstance player, int brId)
    {
        final PrimeShopGroup item = _primeItems.get(brId);
        if ((player == null) || (item == null))
        {
            return;
        }

        player.sendPacket(new ExBRProductInfo(item, player));
    }

    public PrimeShopGroup getItem(int brId)
    {
        return _primeItems.get(brId);
    }

    public Map<Integer, PrimeShopGroup> getPrimeItems()
    {
        return _primeItems;
    }

    public static PrimeShopData getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder
    {
        protected static final PrimeShopData INSTANCE = new PrimeShopData();
    }
}