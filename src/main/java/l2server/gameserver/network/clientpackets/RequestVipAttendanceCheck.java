package l2server.gameserver.network.clientpackets;

import l2server.Config;
import l2server.gameserver.Item;
import l2server.gameserver.datatables.AttendanceTable;
import l2server.gameserver.datatables.ItemTable;
import l2server.gameserver.model.AttendanceInfoHolder;
import l2server.gameserver.model.actor.instance.L2PcInstance;
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
		// TODO
		Log.info(getType() + " packet was received from " + getClient() + ".");
        final L2PcInstance player = getClient().getActiveChar();
        if (player == null)
        {
            return;
        }

        if (!enabled)
        {
            // player.sendPacket(SystemMessageId.DUE_TO_A_SYSTEM_ERROR_THE_ATTENDANCE_REWARD_CANNOT_BE_RECEIVED_PLEASE_TRY_AGAIN_LATER_BY_GOING_TO_MENU_ATTENDANCE_CHECK);
            player.sendMessage("Attendance reward disabled");
            return;
        }

        // Check login delay.
        if (player.getUptime() < (rewardDelay * 60 * 1000))
        {
            // player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_LEVEL_REQUIREMENTS_TO_RECEIVE_THE_ATTENDANCE_REWARD_PLEASE_CHECK_THE_REQUIRED_LEVEL_YOU_CAN_REDEEM_YOUR_REWARD_30_MINUTES_AFTER_LOGGING_IN);
            player.sendSysMessage("You can redeem your reward " + rewardDelay + " minutes after logging in.");
            return;
        }

        final AttendanceInfoHolder attendanceInfo = player.getAttendanceInfo();
        final boolean isRewardAvailable = attendanceInfo.isRewardAvailable();
        final int rewardIndex = attendanceInfo.getRewardIndex();
        final List<AttendanceTable.AttendanceItem> reward = AttendanceTable.getInstance().getRewards(rewardIndex);

        long weight = 0;
        long slots = 0;
        for (AttendanceTable.AttendanceItem item: reward) {
            weight += ItemTable.getInstance().getTemplate(item.getId()).getWeight() * item.getAmount();
            slots += ItemTable.getInstance().getTemplate(item.getId()).isStackable() ? 1 : 50;
        };

        if (!player.getInventory().validateWeight(weight) || !player.getInventory().validateCapacity(slots))
        {
            player.sendMessage("THE_ATTENDANCE_REWARD_CANNOT_BE_RECEIVED_BECAUSE_THE_INVENTORY_WEIGHT_QUANTITY_LIMIT_HAS_BEEN_EXCEEDED");
            return;
        }

        // Reward.
        if (isRewardAvailable)
        {
            // Save date and index.
            player.setAttendanceInfo(rewardIndex + 1);
            // Add items to player.
            for (AttendanceTable.AttendanceItem item: reward) {
                player.addItem("Attendance Reward", item.getId(), item.getAmount(), player, true);
            };
            // Send confirm packet.
        } else {
        }
        player.sendPacket(new ExConfirmVipAttendanceCheck(isRewardAvailable, rewardIndex + 1));
	}
}
