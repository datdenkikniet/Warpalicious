package nl.datdenkikniet.warpalicious.handling.IO;

import nl.datdenkikniet.warpalicious.handling.Warp;

import java.util.Collection;

public interface IOHandler {

    boolean saveWarps(Collection<Warp> warps);

    Collection<Warp> loadWarps();

    boolean saveWarp(Warp warp);

    boolean deleteWarp(Warp warp);

}
