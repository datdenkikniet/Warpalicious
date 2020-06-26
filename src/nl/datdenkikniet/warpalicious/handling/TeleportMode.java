package nl.datdenkikniet.warpalicious.handling;

import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;

public enum TeleportMode {
  SIGN(0, 0, false, false, null, null, 1, 1, false, false),
  COMMAND(0, 0, false, false, null, null, 1, 1, false, false);

  TeleportMode(int creativeDelay, int survivalDelay, boolean creativePerm, boolean survivalPerm,
      Particle arriveEffect, Particle departEffect, int arriveCount, int departCount,
      boolean creativeMove, boolean survivalMove) {
    setValues(creativeDelay, survivalDelay, creativePerm, survivalPerm, arriveEffect, departEffect,
        arriveCount, departCount, creativeMove, survivalMove);
  }

  private int delayInCrea, delayInSurv;
  private boolean creaPerm, survPerm, creaMove, survMove;
  private Particle arrivEffect;
  private Particle depEffect;
  private int arrivCount, depCount;

  public boolean allowMove(GameMode mode) {
    if (mode == GameMode.SURVIVAL || mode == GameMode.ADVENTURE) {
      return survMove;
    } else {
      return creaMove;
    }
  }

  public int getDelay(GameMode mode) {
    if (mode == GameMode.SURVIVAL || mode == GameMode.ADVENTURE) {
      return delayInSurv;
    } else {
      return delayInCrea;
    }
  }

  public boolean getPerm(GameMode mode) {
    if (mode == GameMode.SURVIVAL || mode == GameMode.ADVENTURE) {
      return survPerm;
    } else {
      return creaPerm;
    }
  }

  public Particle getEffect(Direction dir) {
    if (dir == Direction.ARRIVAL) {
      return arrivEffect;
    } else {
      return depEffect;
    }
  }

  public int getEffectCount(Direction dir) {
    if (dir == Direction.ARRIVAL) {
      return arrivCount;
    } else {
      return depCount;
    }
  }

  public void setValues(int creativeDelay, int survivalDelay, boolean creativePerm,
      boolean survivalPerm, Particle arriveEffect, Particle departEffect, int arriveCount,
      int departCount, boolean creativeMove, boolean survivalMove) {
    delayInCrea = creativeDelay;
    delayInSurv = survivalDelay;
    creaPerm = creativePerm;
    survPerm = survivalPerm;
    arrivEffect = arriveEffect;
    depEffect = departEffect;
    arrivCount = arriveCount;
    depCount = departCount;
    creaMove = creativeMove;
    survMove = survivalMove;
  }

  public static int getPermissionDelay(Player player, TeleportMode mode) {
    int delay = 0;
    String permStart =
        "warpalicious.delay." + mode.name().toLowerCase() + "." + player.getGameMode().name()
            .toLowerCase() + ".";
    ArrayList<Integer> numbers = new ArrayList<>();
    for (PermissionAttachmentInfo atch : player.getEffectivePermissions()) {
      if (atch.getPermission().startsWith(permStart)) {
        String permDelay = atch.getPermission().split("\\.")[4];
        try {
          int currentDelay = Integer.valueOf(permDelay);
          if (currentDelay > delay) {
            delay = currentDelay;
          }
          numbers.add(currentDelay);
        } catch (NumberFormatException ignored) {

        }
      }
    }
    for (Integer i : numbers) {
      if (delay > i) {
        delay = i;
      }
    }
    return delay;
  }
}
