package transformations;

import l2server.gameserver.datatables.SkillTable;
import l2server.gameserver.instancemanager.TransformationManager;
import l2server.gameserver.model.L2Transformation;

public class ClockWorkCucuru extends L2Transformation
{
	private static final int[] SKILLS = {5491, 839};

	public ClockWorkCucuru()
	{
		// id, colRadius, colHeight
		super(137, 20, 40);
	}

	@Override
	public void onTransform()
	{
		if (getPlayer().getTransformationId() != 137 || getPlayer().isCursedWeaponEquipped())
		{
			return;
		}
		transformedSkills();
		getPlayer().setMount(13330, getPlayer().getLevel(), 4);
	}

	public void transformedSkills()
	{
		// Decrease Bow/Crossbow Attack Speed
		getPlayer().addSkill(SkillTable.getInstance().getInfo(5491, 1), false);
		// Dismount
		getPlayer().addSkill(SkillTable.getInstance().getInfo(839, 1), false);
		getPlayer().setTransformAllowedSkills(SKILLS);
	}

	@Override
	public void onUntransform()
	{
		removeSkills();
		getPlayer().setMount(0, 0, 0);
	}

	public void removeSkills()
	{
		// Decrease Bow/Crossbow Attack Speed
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(5491, 1), false);
		// Dismount
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(839, 1), false);
		getPlayer().setTransformAllowedSkills(EMPTY_ARRAY);
	}

	public static void main(String[] args)
	{
		TransformationManager.getInstance().registerTransformation(new ClockWorkCucuru());
	}
}
