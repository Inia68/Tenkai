package transformations;

import l2server.gameserver.datatables.SkillTable;
import l2server.gameserver.instancemanager.TransformationManager;
import l2server.gameserver.model.L2Transformation;

public class DragonMasterLee extends L2Transformation
{
	private static final int[] SKILLS = {619};

	public DragonMasterLee()
	{
		// id, colRadius, colHeight
		super(20005, 8, 19.3);
	}

	@Override
	public void onTransform()
	{
		if (getPlayer().getTransformationId() != 20005 || getPlayer().isCursedWeaponEquipped())
		{
			return;
		}

		transformedSkills();
	}

	public void transformedSkills()
	{
		// Transform Dispel
		getPlayer().addSkill(SkillTable.getInstance().getInfo(619, 1), false);

		getPlayer().setTransformAllowedSkills(SKILLS);
	}

	@Override
	public void onUntransform()
	{
		removeSkills();
	}

	public void removeSkills()
	{
		// Transform Dispel
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(619, 1), false);

		getPlayer().setTransformAllowedSkills(EMPTY_ARRAY);
	}

	public static void main(String[] args)
	{
		TransformationManager.getInstance().registerTransformation(new DragonMasterLee());
	}
}
