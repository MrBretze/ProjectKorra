package com.projectkorra.projectkorra.earthbending.combo;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.earthbending.EarthDome;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.ParticleEffect.BlockData;

public class EarthDomeOthers extends EarthAbility {

	public Vector direction;
	public double range = 0, maxRange;
	public Location loc;

	public EarthDomeOthers(final Player player) {
		super(player);

		if (this.bPlayer.isOnCooldown("EarthDome")) {
			return;
		}
		this.loc = player.getLocation().clone();

		if (GeneralMethods.isRegionProtectedFromBuild(player, this.loc)) {
			return;
		}
		if (!isEarthbendable(this.loc.getBlock().getRelative(BlockFace.DOWN).getType(), true, true, true)) {
			return;
		}
		this.direction = this.loc.getDirection().setY(0);
		this.maxRange = getConfig().getDouble("Abilities.Earth.EarthDome.Range");
		this.start();
	}

	@Override
	public void progress() {
		if (!this.player.isOnline() || this.player.isDead()) {
			this.remove(true);
			return;
		}
		if (this.range >= this.maxRange) {
			this.remove(true);
			return;
		}
		if (GeneralMethods.isRegionProtectedFromBuild(this.player, this.loc)) {
			this.remove(true);
			return;
		}

		this.range++;
		this.loc.add(this.direction.normalize());
		Block top = GeneralMethods.getTopBlock(this.loc, 2);

		while (!this.isEarthbendable(top)) {
			if (this.isTransparent(top)) {
				top = top.getRelative(BlockFace.DOWN);
			} else {
				this.remove(true);
				return;
			}
		}

		if (!this.isTransparent(top.getRelative(BlockFace.UP))) {
			this.remove(true);
			return;
		}

		this.loc.setY(top.getY() + 1);

		ParticleEffect.CRIT.display(this.loc, 0.4f, 0, 0.4f, 0.001f, 9);
		ParticleEffect.BLOCK_DUST.display(new BlockData(this.loc.getBlock().getRelative(BlockFace.DOWN).getType(), (byte) 0), 0.2f, 0.1f, 0.2f, 0.001f, 7, this.loc, 255);

		for (final Entity entity : GeneralMethods.getEntitiesAroundPoint(this.loc, 2)) {
			if (!(entity instanceof LivingEntity) || entity.getEntityId() == this.player.getEntityId()) {
				continue;
			}

			new EarthDome(this.player, entity.getLocation().clone().subtract(0, 1, 0));
			this.remove(false);
			return;
		}
	}

	public void remove(final boolean cooldown) {
		super.remove();
		if (cooldown) {
			this.bPlayer.addCooldown("EarthDome", getConfig().getLong("Abilities.Earth.EarthDome.Cooldown"));
		}
	}

	@Override
	public boolean isSneakAbility() {
		return false;
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public long getCooldown() {
		return 0;
	}

	@Override
	public String getName() {
		return "EarthDome";
	}

	@Override
	public Location getLocation() {
		return this.loc != null ? this.loc : null;
	}

	@Override
	public boolean isHiddenAbility() {
		return true;
	}
}
