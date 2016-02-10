package com.pb.helpers;

import com.pb.dao.Snapshot;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Component;

/**
 * provides general helpers for table
 */
@Component
public class TableHelper {
    public int userChair(Snapshot snapshot) {
        return snapshot.getSymbols().get(Snapshot.SYMBOLS.USERCHAIR).intValue();
    }
    /*
     * returns the position of the opponent with the maximum stack size
     */
    public int maxPlayingOpponentPosition(Snapshot snapshot) {
        Integer maxStackPos = -1;
        Integer userChair = userChair(snapshot);
        for (int i=0; i<snapshot.getPlayers().length; i++) {
            Snapshot.Player player = snapshot.getPlayers()[i];
            if ((player.getPlaying() == 0) || (userChair == i)) {
                continue;
            }
            if ((maxStackPos == -1) ||
                (snapshot.getPlayers()[maxStackPos].getBalance() < player.getBalance()))
                maxStackPos = i;
        }
        return maxStackPos;
    }

    /*
     * returns the stack size of the opponent with the maximum stack size
     */
    public Double maxPlayingOpponentStackSize(Snapshot snapshot) {
        Integer pos = maxPlayingOpponentPosition(snapshot);
        return snapshot.getPlayers()[pos].getBalance();
    }

    /*
     * returns the position of the opponent with the maximum stack+bet size
     */
    public int maxStackBetPlayingOpponentPosition(Snapshot snapshot) {
        Integer maxStackPos = -1;
        Integer userChair = userChair(snapshot);
        for (int i=0; i<snapshot.getPlayers().length; i++) {
            Snapshot.Player player = snapshot.getPlayers()[i];
            if ((player.getPlaying() == 0) || (userChair == i)) {
                continue;
            }
            if (maxStackPos == -1) {
                maxStackPos = i;
                continue;
            }
            Double max = snapshot.getPlayers()[maxStackPos].getBalance() + snapshot.getPlayers()[maxStackPos].getCurrentbet();
            Double current = player.getBalance() + player.getCurrentbet();
            if (max < current)
                maxStackPos = i;
        }
        return maxStackPos;
    }

    /*
     * returns the stack size of the opponent with the maximum stack size
     */
    public Double maxStackBetPlayingOpponentStackBetSize(Snapshot snapshot) {
        Integer pos = maxStackBetPlayingOpponentPosition(snapshot);
        return snapshot.getPlayers()[pos].getBalance() + snapshot.getPlayers()[pos].getCurrentbet();
    }
}
