package PbGateway;

import lombok.Data;

import java.util.List;

@Data
public class PbTableData {
    public String title;
    public Integer is_playing;
    public Integer is_posting;
    public Integer fillerbits;
    public Integer dealer_chair;
    public List<Double> pots;
    public List<PtPlayerHand> players;
}
