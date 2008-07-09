package us;

public class AimCenter implements IAimingStrategy {

	public double getTurnGunRightAmount(Target aim, UBot uBot) {
		uBot.out.println("Aim[" + aim.getName() + "]: ab|aa|bg|bh " + aim.getBearing()
				+ "|" + aim.getAbsoluteBearing(uBot)
				+ "|" + uBot.getGunHeading() + "|"
				+ uBot.getHeading());

		return aim.getAbsoluteBearing(uBot) - uBot.getGunHeading();
	}

}
