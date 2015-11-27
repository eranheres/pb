package PbValidator;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PbTableData {
    public PbTableData() {
        cards = new ArrayList<>();
        pots  = new ArrayList<>();
        players = new ArrayList<>();
    }
    public String title;
    public Integer is_playing;
    public Integer is_posting;
    public Integer fillerbits;
    public Integer dealer_chair;
    public List<String> cards;
    public List<Double> pots;
    public List<PbPlayerHand> players;
}
