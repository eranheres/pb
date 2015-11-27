package PbValidator;

import lombok.Data;

import java.util.List;

@Data
public class PbPlayerHand {
    public Integer name_known;
    public Double balance;
    public Double currentbet;
    public Double fillerbytes;
    public List<String> cards;
    public Integer balance_known;
    public Integer fillerbits;
}
