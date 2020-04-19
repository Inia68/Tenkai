package l2server.gameserver.Nodes;

import l2server.gameserver.model.L2Spawn;
import l2server.gameserver.model.L2World;
import l2server.gameserver.model.actor.L2Npc;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.network.serverpackets.NpcHtmlMessage;
import l2server.gameserver.templates.chars.L2NpcTemplate;

import java.util.Vector;

/**
 * @author Vasper
 * @since 4/13/2017
 */
public class L2Node extends L2Npc
{
	private int territoryId;
	
	private int level;
	private float radius;
	
	L2NpcTemplate tmpl;
	L2Spawn spawn;
	
	private String nodeMessage;
	public Vector<Integer> ownersId;
	

	public Vector GetOwnersId()
	{
		return ownersId;
	}
	
	public L2Spawn GetNpcTemplate()
	{
		return (spawn);
	}
	
	public long GetOwnTime()
	{
		return (30000 * level);
	}
	
	public int GetLevel()
	{
		return (level);
	}
	
	public void SetMyMessage(String message)
	{
		this.nodeMessage = message;
		spawn.getNpc().setName(message);
	}
	
	public String TalkToMe(L2PcInstance player)
	{
		StringBuilder tb = new StringBuilder();
		tb.append("<html><br1>Level : " + level + "<br>");
		tb.append("Owners : <br>");
		
		if (ownersId.isEmpty())
		{
			tb.append("None.<br>");
			tb.append(" <button action=\"bypass Inia;_node;conquer\" value=\"Conquer\"\n" + "width=180 height=21 back=\"L2UI_CT1.Button_DF_Down\"\n" + "fore=\"L2UI_CT1.Button_DF\">");
		}
		else
		{
			for (int id : ownersId)
			{
				L2PcInstance owner = L2World.getInstance().getPlayer(id);
				tb.append("" + owner.getName() + "<br>");
			}
			tb.append(" <button action=\"bypass Inia;_node;conquer\" value=\"Steal\"\n" + "width=180 height=21 back=\"L2UI_CT1.Button_DF_Down\"\n" + "fore=\"L2UI_CT1.Button_DF\">");
			
		}
		tb.append("</html>");
		NpcHtmlMessage msg = new NpcHtmlMessage(territoryId);
		msg.setHtml(tb.toString());
		player.sendPacket(msg);
		
		return "";
		
	}
	
	
	
	public void AddOwner(L2PcInstance player)
	{
		if (!ownersId.contains(player.getObjectId()))
		{
			ownersId.add(player.getObjectId());
			player.sendMessage("Added to the owners !");
			return ;
		}
		player.sendMessage("You're already a member of this node !");
	}
	
	public void RemoveOwner(L2PcInstance player)
	{
		if (ownersId.contains(player.getObjectId()))
		{
			ownersId.remove(player.getObjectId());
			player.sendMessage("Removed from the owners !");
			return ;
		}
		player.sendMessage("You're not a member of this node !");
	}
	
	public void RemoveAllOwners()
	{
		ownersId.clear();
	}
	
	public L2Node (int id, L2NpcTemplate template , int territoryId, float radius)
	{
		super(id, template);
		this.territoryId = 50101;
		this.level = 1;
		this.radius = radius;
		ownersId = null;
	}
	
	public L2Node (int id, L2NpcTemplate template ,int territoryId, float radius, int level)
	{
		super(id, template);
		this.territoryId = 50101;
		this.level = level;
		this.radius = radius;
		ownersId = null;
	}
	

}
