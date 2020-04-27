package l2server.gameserver.network.clientpackets;

import l2server.Config;
import l2server.gameserver.Item;
import l2server.gameserver.datatables.AttendanceRewardData;
import l2server.gameserver.datatables.AttendanceTable;
import l2server.gameserver.datatables.ItemTable;
import l2server.gameserver.model.AttendanceInfoHolder;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.holder.ItemHolder;
import l2server.gameserver.network.SystemMessageId;
import l2server.gameserver.network.serverpackets.ExConfirmVipAttendanceCheck;
import l2server.gameserver.network.serverpackets.SystemMessage;
import l2server.gameserver.templates.chars.L2PcTemplate;
import l2server.gameserver.templates.item.L2Item;
import l2server.log.Log;

import javax.swing.plaf.basic.BasicComboBoxUI;
import java.util.List;

/**
 * @author MegaParzor!
 */
public class RequestVipAttendanceCheck extends L2GameClientPacket
{
    private final boolean enabled = true;
    private final int rewardDelay = 15;
	@Override
	public void readImpl()
	{
	}

	@Override
	public void runImpl()
	{
        final L2PcInstance player = getClient().getActiveChar();
        if (player == null)
        {
            return;
        }


        // Check login delay.
        if (player.getUptime() < (15 * 60 * 1000))
        {
            // player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_LEVEL_REQUIREMENTS_TO_RECEIVE_THE_ATTENDANCE_REWARD_PLEASE_CHECK_THE_REQUIRED_LEVEL_YOU_CAN_REDEEM_YOUR_REWARD_30_MINUTES_AFTER_LOGGING_IN);
            player.sendMessage("You can redeem your reward 15 minutes after logging in.");
            return;
        }

        final AttendanceInfoHolder attendanceInfo = player.getAttendanceInfo();
        final boolean isRewardAvailable = attendanceInfo.isRewardAvailable();
        final int rewardIndex = attendanceInfo.getRewardIndex();
        final ItemHolder reward = AttendanceRewardData.getInstance().getRewards().get(rewardIndex);
        final L2Item itemTemplate = ItemTable.getInstance().getTemplate(reward.getId());

        // Weight check.
        final long weight = itemTemplate.getWeight() * reward.getCount();
        final long slots = itemTemplate.isStackable() ? 1 : reward.getCount();
        if (!player.getInventory().validateWeight(weight) || !player.getInventory().validateCapacity(slots))
        {
            player.sendMessage("THE_ATTENDANCE_REWARD_CANNOT_BE_RECEIVED_BECAUSE_THE_INVENTORY_WEIGHT_QUANTITY_LIMIT_HAS_BEEN_EXCEEDED");
            return;
        }

        if (isRewardAvailable)
        {
            player.setAttendanceInfo(rewardIndex + 1);
            player.addItem("Attendance Reward", reward.getId(), reward.getCount(), player, true);
            player.sendPacket(new ExConfirmVipAttendanceCheck(isRewardAvailable, rewardIndex + 1));
        }
	}
}
