package PbValidator;

public interface PbValidation {
    String STATUS_OK = "OK";

    String validate(PbTableData previous, PbTableData current);
}
